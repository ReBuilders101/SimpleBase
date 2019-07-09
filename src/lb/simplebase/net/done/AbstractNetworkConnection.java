package lb.simplebase.net.done;

import java.net.InetSocketAddress;
import java.net.Socket;

import lb.simplebase.net.ConnectionStateFuture;
import lb.simplebase.net.LocalNetworkConnection;
import lb.simplebase.net.NetworkManagerServer;
import lb.simplebase.net.PacketSendFuture;
import lb.simplebase.net.RemoteNetworkConnection;

/**
 * A {@link AbstractNetworkConnection} represents the connection between two network targets, seen from one side.<br>
 * It contains the {@link TargetIdentifier} of the connection partner and the own {@link TargetIdentifier}.
 * It provides methods to send packets to the remote partner and accepts received {@link Packet}s, which are sent
 * to a connected {@link NetworkManager} after being constructed by a {@link PacketFactory}.<p>
 * Every connection has a basic lifecycle: First the {@link AbstractNetworkConnection} object is created with all information
 * necessary to open the connection. Then the connection is opened by calling the {@link #connect()} method.
 * Now data can be sent through the connection. The connection remains open until either the {@link #close()} method is called,
 * or the connection is closed by the remote partner. After the connection has been closed, no more data can be sent through the connection.
 */
public abstract class AbstractNetworkConnection {
	
	private final TargetIdentifier local; //is this even necessary?
	private final TargetIdentifier remote;
	//No receiver, because receivers do not depend on specific connections (entityupdate from any client etc)
	private final NetworkManager packetHandler; //This is the Networkmanager
	private volatile ConnectionState state; //Threadsafe for socket listener
	private final PacketFactory factory;
	
	protected AbstractNetworkConnection(TargetIdentifier local, TargetIdentifier remote, NetworkManager packetHandler) {
		this.local = local;
		this.remote = remote;
		this.packetHandler = packetHandler;
		this.state = ConnectionState.UNCONNECTED;
		this.factory = new PacketFactory(packetHandler, this);
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
	 * The {@link TargetIdentifier} of the network target that is sending and receiving packets throgh this {@link AbstractNetworkConnection}.
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
	 * If the connection is not open, this means that either the connection has been closed or that it has not yet been made.<br>
	 * Equal to testing {@link #getState()}<code> == </code>{@link ConnectionState#OPEN}
	 * @return Whether the connection is open
	 */
	public synchronized boolean isConnectionOpen() {
		return state == ConnectionState.OPEN;
	}
	
	/**
	 * Closes this {@link AbstractNetworkConnection}. After closing, no {@link Packet}s can be sent or received.
	 * The connected {@link NetworkManager} will be notified when a connection is closed, and
	 * in case of a {@link NetworkManagerServer}, this connection will be removed from the list of active connections.<br>
	 * The {@link ConnectionState} will be changed to {@link ConnectionState#CLOSED}.
	 */
	public final synchronized ConnectionStateFuture close() {
		state = ConnectionState.CLOSED;
		packetHandler.notifyConnectionClosed(this);
		return closeImpl();
	}
	
	protected abstract ConnectionStateFuture closeImpl();
	
	/**
	 * Set the connection state
	 */
	protected synchronized void setConnectionState(ConnectionState state) {
		this.state = state;
	}
	
	/**
	 * packet from the socket listener thread
	 */
	protected void handleReceivedPacket(Packet received) {
		packetHandler.accept(received, local);
	}
	
	/**
	 * Tries to make a network connection to the remote target, using the connection information
	 * that this instance was created with. This method uses a timeout vaule of 30 seconds.
	 * @throws ConnectionStateException When the connection could not be made
	 * @see #connect(int)
	 */
	public ConnectionStateFuture connect() {
		return connect(30000); //Default timeout 30s = 30,000 ms
	}
	
	/**
	 * Tries to make a network connection to the remote target, using the connection information
	 * that this instance was created with.
	 * @param timeout The maximal timeout in milliseconds
	 * @throws ConnectionStateException When the connection could not be made
	 */
	public abstract ConnectionStateFuture connect(int timeout);
	
	/**
	 * Sends the {@link Packet} to the connected network target. 
	 * @param packet The {@link Packet} containing the data that should be sent
	 * @throws ConnectionStateException When the {@link Packet} could not be sent
	 */
	public abstract PacketSendFuture sendPacketToTarget(Packet packet);
	
	/**
	 * A local connection is a connection between two network targets that exist within the same program.
	 * To increase speed, {@link Packet} sent through a local connection are not sent to the network. For 
	 * this reason, the {@link TargetIdentifier} returned by {@link #getRemoteTargetId()} does not contain a valid
	 * {@link InetSocketAddress} if this method returns <code>true</code>. 
	 * @return Whether this connection is between two network targets in the same program
	 */
	public abstract boolean isLocalConnection();
	
	/**
	 * The current {@link ConnectionState} of this {@link AbstractNetworkConnection}.
	 * Some actions can throw {@link ConnectionStateException}s if the current state does not match the required state.
	 * @return The current {@link ConnectionState} of this {@link AbstractNetworkConnection}
	 */
	public synchronized ConnectionState getState() {
		return state;
	}

	/**
	 * The {@link PacketFactory} that is used to create Packets from this connection.
	 * @return The {@link PacketFactory} that is used to create Packets from this connection
	 */
	public PacketFactory getPacketFactory() {
		return factory;
	}
	
	///////////////////////////////////////THE STATIC METHODS BEGIN HERE/////////////////////////////////////////////////////////////////////
	
	//Called from the Networkmanager
 	public static AbstractNetworkConnection createConnection(TargetIdentifier local, TargetIdentifier remote, NetworkManager manager) {
		if(remote.isLocalOnly()) {
			return new LocalNetworkConnection(local, remote, manager);
		} else {
			return new RemoteNetworkConnection(local, remote, manager);
		}
 	}
}
