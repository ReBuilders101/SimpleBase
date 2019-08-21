package lb.simplebase.net;

import java.net.InetSocketAddress;
import java.net.Socket;

import lb.simplebase.net.ClosedConnectionEvent.Cause;

/**
 * An {@link NetworkConnection} represents the connection between two network targets, seen from one side.<br>
 * It contains the {@link TargetIdentifier} of the connection partner and the own {@link TargetIdentifier}.
 * It provides methods to send packets to the remote partner and accepts received {@link Packet}s, which are sent
 * to a connected {@link NetworkManager} after being constructed by a {@link PacketFactory}.<p>
 * Every connection has a basic lifecycle: First the {@link NetworkConnection} object is created with all information
 * necessary to open the connection. Then the connection is opened by calling the {@link #connect()} method.
 * Now data can be sent through the connection. The connection remains open until either the {@link #close()} method is called,
 * or the connection is closed by the remote partner. After the connection has been closed, no more data can be sent through the connection.
 */
public abstract class NetworkConnection {
	
	private final TargetIdentifier local; //is this even necessary?
	private final TargetIdentifier remote;
	//No receiver, because receivers do not depend on specific connections (entityupdate from any client etc)
	private final NetworkManager packetHandler; //This is the Networkmanager
	protected volatile ConnectionState state; //Threadsafe for socket listener
//	private final PacketFactory factory;
	
	private final PacketContext context;
	
	protected NetworkConnection(TargetIdentifier local, TargetIdentifier remote, NetworkManager packetHandler, ConnectionState initialState, boolean isServer, Object payload) {
		this.local = local;
		this.remote = remote;
		this.packetHandler = packetHandler;
		this.state = initialState;
		
		this.context = new GenericPacketContext<>(isServer, packetHandler, this, payload);
//		this.factory = new PacketFactory(packetHandler, this);
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
	 * If the connection is not open, this means that either the connection has been closed or that it has not yet been made.<br>
	 * Equal to testing {@link #getState()}<code> == </code>{@link ConnectionState#OPEN}
	 * @return Whether the connection is open
	 */
	public synchronized boolean isConnectionOpen() {
		return state == ConnectionState.OPEN;
	}
	
	/**
	 * Closes this {@link NetworkConnection}. After closing, no {@link Packet}s can be sent or received.
	 * The connected {@link NetworkManager} will be notified when a connection is closed, and
	 * in case of a {@link NetworkManagerServer}, this connection will be removed from the list of active connections.<br>
	 * The {@link ConnectionState} will be changed to {@link ConnectionState#CLOSED}.
	 */
	public ConnectionStateFuture close() {
		return closeWithReason(Cause.EXPECTED);
	}
	
	protected synchronized ConnectionStateFuture closeWithReason(ClosedConnectionEvent.Cause cause) {
		ConnectionState oldState = state;
		state = ConnectionState.CLOSED;
		packetHandler.notifyConnectionClosed(this, cause);
		return ConnectionStateFuture.quickDone(oldState, state);
	}
	
	/**
	 * Set the connection state
	 */
	protected synchronized void setConnectionState(ConnectionState state) {
		this.state = state;
	}
	
	/**
	 * Called by the packet factory when a packet is completed. The packet will be
	 * processed by the {@link NetworkManager} that was used in the constructor
	 * and is available with {@link #getNetworkManager()}.<br>
	 * Normally not called by application code, but can be used to simulate a received packet.
	 * @param received The packet that was received by this connection
	 */
	public void handleReceivedPacket(Packet received) {
		packetHandler.accept(received, context);
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
	 */
	public abstract ConnectionStateFuture connect(int timeout);
	
	/**
	 * Sends the {@link Packet} to the connected network target. 
	 * @param packet The {@link Packet} containing the data that should be sent
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
	 * The current {@link ConnectionState} of this {@link NetworkConnection}.
	 * Some actions can throw {@link ConnectionStateException}s if the current state does not match the required state.
	 * @return The current {@link ConnectionState} of this {@link NetworkConnection}
	 */
	public synchronized ConnectionState getState() {
		return state;
	}

	/**
	 * The {@link NetworkManager} that this connection belongs to. This manager will receive incoming packets
	 * from this connection and will be used to look up {@link PacketIdMapping}s.
	 * @return The {@link NetworkManager} for this connection
	 */
	public NetworkManager getNetworkManager() {
		return packetHandler;
	}
	
	protected PacketContext getContext() {
		return context;
	}
	
}
