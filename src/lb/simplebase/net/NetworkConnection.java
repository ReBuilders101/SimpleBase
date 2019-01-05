package lb.simplebase.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A {@link NetworkConnection} represents the connection between two network targets, seen from one side.<br>
 * It contains the {@link TargetIdentifier} of the connection partner and the own {@link TargetIdentifier}.
 * It provides methods to send packets to the remote partner and accepts received {@link Packet}s, which are sent
 * to a connected {@link NetworkManager} after being constructed by a {@link PacketFactory}.<p>
 * Every connection has a basic lifecycle: First the {@link NetworkConnection} object is created with all information
 * necessary to open the connection. Then the connection is opened by calling the {@link #connect()} method.
 * Now data can be sent through the connection. The connection remains open until either the {@link #close()} method is called,
 * or the connection is closed by the remote partner. After the connection has been closed, no more data can be sent through the connection.
 */
public abstract class NetworkConnection {
	
	private TargetIdentifier local; //is this even necessary?
	private TargetIdentifier remote;
	//No receiver, because receivers do not depend on specific nonnections (entityupdate from any client etc)
	private NetworkManager packetHandler; //This is the Networkmanager
	private volatile boolean open; //Threadsafe for socket listener
	
	protected NetworkConnection(TargetIdentifier local, TargetIdentifier remote, NetworkManager packetHandler, boolean open) {
		this.local = local;
		this.remote = remote;
		this.packetHandler = packetHandler;
		this.open = open; //TODO can it be opened from the beginning
	}

	/**
	 * The {@link TargetIdentifier} of the remote partner. If {@link #isLocalConnection()} is <code>false</code>, this target identifier
	 * contains a {@link InetSocketAddress} that con be used to create a {@link Socket}-based connection to the target.
	 * @return The remote partner's {@link TargetIdentifier}
	 */
	public TargetIdentifier getRemoteTargetId() {
		return remote;
	}
	
	/**
	 * The {@link TargetIdentifier} of the network target that is sending and receiving packets throgh this {@link NetworkConnection}.
	 * It does not contain a valid {@link InetSocketAddress}, because connections to the same target that the rpogram is running on make no sense.
	 * It is however necessary, because one program can contain more than one network target, for example a server and a local client.
	 * @return The own {@link TargetIdentifier}
	 */
	public TargetIdentifier getLocalTargetId() {
		return local;
	}
	
	/**
	 * If the connection is open, {@link Packet}s can be sent through the connection and can be received from the remote partner. A connection can be
	 * closed from either this side ({@link #close()}) or by the remote partner.<br>
	 * If the connection is not open, this means that either the connection has been closed or that it has not yet been made.
	 * @return Whether the connection is open
	 */
	public boolean isConnectionOpen() {
		return open;
	}
	
	/**
	 * Closes this {@link NetworkConnection}. After closing, no {@link Packet}s can be sent or received.
	 * The connected {@link NetworkManager} will be notified when a connection is closed, and
	 * in case of a {@link NetworkManagerServer}, this connection will be removed from the list of active connections.
	 */
	public void close() {
		open = false;
		packetHandler.notifyConnectionClosed(this);
	}
	
	/**
	 * Sets the open flag to true
	 */
	protected final void open() {
		open = true;
	}
	
	private void handleReceivedPacket(Packet received) {
		packetHandler.accept(received, local);
	}
	
	/**
	 * Tries to make a network connection to the remote target, using the connection information
	 * that this instance was created with. This method uses a timeout vaule of 30 seconds.
	 * @throws ConnectionNotConnectedException When the connection could not be made 
	 * @throws ConnectionAlreadyConnectedException When the connection is already connected
	 * @see #connect(int)
	 */
	public void connect() throws ConnectionNotConnectedException, ConnectionAlreadyConnectedException {
		connect(30); //Default timeout 30s
	}
	
	/**
	 * Tries to make a network connection to the remote target, using the connection information
	 * that this instance was created with.
	 * @param timeout The maximal timeout in seconds
	 * @throws ConnectionNotConnectedException When the connection could not be made 
	 * @throws ConnectionAlreadyConnectedException When the connection is already connected
	 */
	public abstract void connect(int timeout) throws ConnectionNotConnectedException, ConnectionAlreadyConnectedException;
	
	/**
	 * Sends the {@link Packet} to the connected network target. 
	 * @param packet The {@link Packet} containing the data that should be sent
	 * @throws ConnectionNotOpenException When the connection has not yet been connected or has been closed
	 * @throws IOException When the {@link Packet} data could not be written to the connection's IO Stream
	 */
	public abstract void sendPacketToTarget(Packet packet) throws ConnectionNotOpenException, IOException;
	
	/**
	 * A local connection is a connection between two network targets that exist within the same program.
	 * To increase speed, {@link Packet} sent through a local connection are not sent to the network. For 
	 * this reason, the {@link TargetIdentifier} returned by {@link #getRemoteTargetId()} does not contain a valid
	 * {@link InetSocketAddress} if this method returns <code>true</code>. 
	 * @return Whether this connection is between two network targets in the same program
	 */
	public abstract boolean isLocalConnection();
	
	//Called from the Networkmanager
 	protected static NetworkConnection createConnection(TargetIdentifier local, TargetIdentifier remote, NetworkManager manager) {
		if(remote.isLocalOnly()) {
			return new LocalNetworkConnection(local, remote, manager);
		} else {
			return new RemoteNetworkConnection(local, remote, manager);
		}
 	}
 	
 	protected static NetworkConnection createConnection(TargetIdentifier local, TargetIdentifier remote, NetworkManager manager, boolean connect)
 			throws ConnectionNotConnectedException, ConnectionAlreadyConnectedException {
		if(remote.isLocalOnly()) {
			return new LocalNetworkConnection(local, remote, manager, connect);
		} else {
			return new RemoteNetworkConnection(local, remote, manager, connect);
		}
 	}
 	
 	//Only for testing
 	protected static NetworkConnection createConnection(TargetIdentifier local, TargetIdentifier remote, NetworkManager manager, boolean connect,
 			@Deprecated boolean copyIfLocal)
 			throws ConnectionNotConnectedException, ConnectionAlreadyConnectedException {
		if(remote.isLocalOnly()) {
			return new LocalCopyNetworkConnection(local, remote, manager, connect);
		} else {
			return new RemoteNetworkConnection(local, remote, manager, connect);
		}
 	}
	
 	
 	//################################################################################
 	
 	
	protected static class LocalNetworkConnection extends NetworkConnection{

		private NetworkConnection partner = null;
		
		protected LocalNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler) {
			super(source, target, packetHandler, false);
		}
		
		protected LocalNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, boolean connect) 
				throws ConnectionNotConnectedException, ConnectionAlreadyConnectedException {
			this(source, target, packetHandler);
			if(connect) connect();
		}
		
		protected LocalNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, NetworkConnection setPartner) {
			this(source, target, packetHandler);
			partner = setPartner;
		}
		
		@Override
		public void sendPacketToTarget(Packet packet) throws ConnectionNotOpenException {
			if(!isConnectionOpen()) {
				throw new ConnectionNotOpenException("Local connection has already been closed", this);
			}else if(partner == null || !partner.isConnectionOpen()) {
				close(); //Close if partner is closed //TODO does this work?
				throw new ConnectionNotOpenException("Local connection partner closed or not connected", partner);
			} else {
				partner.handleReceivedPacket(packet); //Local partner handles packet
			}
		}

		@Override
		public void connect(int timeout) throws ConnectionNotConnectedException, ConnectionAlreadyConnectedException {
			if(partner != null)
				throw new ConnectionAlreadyConnectedException("Local connection already open", this);
			partner = LocalServerManager.waitForLocalConnectionPartner(this, timeout);
			open();
		}

		protected NetworkConnection getPartner() {
			return partner;
		}

		@Override
		public boolean isLocalConnection() {
			return true;
		}
		
	}
	
	//##############################################################################################
	/**
	 * @deprecated Only for testing
	 */
	@Deprecated
	protected static class LocalCopyNetworkConnection extends LocalNetworkConnection {

		protected LocalCopyNetworkConnection(TargetIdentifier source, TargetIdentifier target,
				NetworkManager packetHandler, boolean connect)
				throws ConnectionNotConnectedException, ConnectionAlreadyConnectedException {
			super(source, target, packetHandler, connect);
		}

		protected LocalCopyNetworkConnection(TargetIdentifier source, TargetIdentifier target,
				NetworkManager packetHandler) {
			super(source, target, packetHandler);
		}
		
		@Override
		public void sendPacketToTarget(Packet packet) throws ConnectionNotOpenException {
			if(!isConnectionOpen()) {
				throw new ConnectionNotOpenException("Local connection has already been closed", this);
			}else if(getPartner() == null || !getPartner().isConnectionOpen()) {
				close(); //Close if partner is closed //TODO does this work?
				throw new ConnectionNotOpenException("Local connection partner closed or not connected", getPartner());
			} else {
				ByteBuffer data = new ByteBuffer();
				packet.writeData(data);
				Packet packet2 = packet.createEmptyInstance();
				packet2.readData(data);
				getPartner().handleReceivedPacket(packet2); //Local partner handles copied packet
			}
		}
		
	}
	
	protected static class RemoteNetworkConnection extends NetworkConnection{

		private final Socket connection;
		private final Thread socketListenerThread;
		private final PacketFactory factory;
		private final int id;
		
		private static volatile int ID = 0; 
		
		protected RemoteNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler) {
			this(source, target, packetHandler, new Socket());
		}

		protected RemoteNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, boolean connect) throws ConnectionNotConnectedException, ConnectionAlreadyConnectedException {
			this(source, target, packetHandler);
			if(connect)
				connect();
		}
		
		protected RemoteNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, Socket connectedSocket) {
			super(source, target, packetHandler, false);
			factory = new PacketFactory(packetHandler, this);
			connection = connectedSocket;
			if(connection.isConnected() && !connection.isClosed()) open(); //this doesn't connect twice, it just sets the open flag
			id = ID++; //Assign unique id
			//setup thread
			socketListenerThread = new Thread(this::waitForPacket);
			socketListenerThread.setDaemon(true);
			socketListenerThread.setName("RemoteNetworkConnection-" + id + "-SocketListener");
			socketListenerThread.start();
		}
		
		@Override
		public void sendPacketToTarget(Packet packet) throws IOException, ConnectionNotOpenException {
			if(connection.isClosed() || !connection.isConnected())
				throw new ConnectionNotOpenException("Remote connection is closed or not connected", this);
			ByteBuffer data = new ByteBuffer();
			packet.writeData(data);
			connection.getOutputStream().write(data.getAsArray());
		}
		
		@Override
		public void close() {
			super.close();
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void waitForPacket(){
			while(!connection.isClosed() && isConnectionOpen()) {
				try {
					byte b = (byte) connection.getInputStream().read();
					//Do something with the bytes
					factory.feed(b);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			close(); //close when while exits (this means the remote partner closed the connection 
		}

		@Override
		public void connect(int timeout) throws ConnectionNotConnectedException, ConnectionAlreadyConnectedException {
			if(connection.isConnected())
				throw new ConnectionAlreadyConnectedException("Remote connection is already connected", this);
			try {
				connection.connect(getRemoteTargetId().getConnectionAddress(), timeout);
				open();
			} catch (IOException e) {
				throw new ConnectionNotConnectedException("Could not make remote Connection", this);
			}
		}

		@Override
		public boolean isLocalConnection() {
			return false;
		}
		
	}
}
