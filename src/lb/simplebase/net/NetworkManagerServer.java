package lb.simplebase.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import lb.simplebase.net.todo.PacketSendFuture;

/**
 * A {@link NetworkManager} that reperesents the server side of the application. It supports multiple {@link NetworkConnection}s to clients.
 * All {@link Packet}s that are received are processed with an {@link ExecutorService}.
 */
@ServerSide
public class NetworkManagerServer extends NetworkManager{
	
	private static volatile int ID = 0;
	private static final long CAN_ACCEPT_TEST_DELAY = 1L;
	
	private volatile int nextConnectionID = 0;
	
	private final boolean isLocalOnly;
	private final int id;
	private boolean closed;
	
	private final Map<TargetIdentifier, NetworkConnection> connections;
	private final int connectionLimit;
	
	private final ServerSocket server;
	private final Thread socketConnectionAcceptor;
	private final Consumer<NetworkConnection> conHand;
	
	/**
	 * Creates a new {@link NetworkManagerServer}. The created instance will not contain any connections, but it will immediately start listening
	 * for incoming connections (and accept them).
	 * If the <i>localId</i> is a local-only implementation ({@link TargetIdentifier#isLocalOnly()}), no server socket for network connections will be created.
	 * @param threadReceiver The {@link PacketReceiver} that will receive incoming {@link Packet}s from all clients on a separate {@link Thread}
	 * @param localId The {@link TargetIdentifier} of the network target represented by this {@link NetworkManagerServer}
	 * @param connectionLimit The maximum amount of connections that this {@link NetworkManagerServer} can have
	 * @param newConnectionHandler A callback that is called when a new connection has been created.
	 * @param singleThread Whether the packets should only be processed on one thread at a time. See {@link PacketThreadReceiver#hasSingleThread()}.
	 * @throws IOException When the {@link ServerSocket} used to listen for connections could not be created
	 */
	public NetworkManagerServer(PacketReceiver threadReceiver, TargetIdentifier localId, int connectionLimit, Consumer<NetworkConnection> newConnectionHandler, boolean singleThread) throws IOException {
		super(threadReceiver, localId, singleThread);
		this.connectionLimit = connectionLimit;
		this.connections = Collections.synchronizedMap(new HashMap<>()); //Sync map, because it is accessed from two threads 
		
		this.isLocalOnly = localId.isLocalOnly();
		
		this.id = ID++;
		this.closed = false;
		this.conHand = newConnectionHandler;
		
		if(!localId.isLocalOnly()) { //Open server if not local
			this.server = new ServerSocket(localId.getConnectionAddress().getPort()); //Bind to port
			socketConnectionAcceptor = new Thread(this::acceptServerConnections);
			socketConnectionAcceptor.setDaemon(true);
			socketConnectionAcceptor.setName("NetworkManagerServer-" + id + "-SocketAcceptor");
			socketConnectionAcceptor.start();
		} else {
			this.server = null;
			this.socketConnectionAcceptor = null;
		}
		
		//Register for local connections
		LocalServerManager.addServer(this);
	}
	
	/**
	 * Creates a new {@link NetworkManagerServer}. The created instance will not contain any connections, but it will immediately start listening
	 * for incoming connections (and accept them).
	 * If the <i>localId</i> is a local-only implementation ({@link TargetIdentifier#isLocalOnly()}), no server socket for network connections will be created.
	 * <br>This {@link NetworkManagerServer} will have unlimited connections, no handler for new connections, and will only use a single processing thread.
	 * @param threadReceiver The {@link PacketReceiver} that will receive incoming {@link Packet}s from all clients on a separate {@link Thread}
	 * @param localId The {@link TargetIdentifier} of the network target represented by this {@link NetworkManagerServer}
	 * @throws IOException When the {@link ServerSocket} used to listen for connections could not be created
	 */
	public NetworkManagerServer(PacketReceiver threadReceiver, TargetIdentifier localId) throws IOException {
		this(threadReceiver, localId, Integer.MAX_VALUE, (c) -> {}, true);
	}
	
	/**
	 * Sends a packet to the specified target. Acts exactly like {@link #sendPacketToClient(Packet, TargetIdentifier)}, 
	 * but lacks the return value that indicates success or failure to send the {@link Packet}. Because of this, using 
	 * {@link #sendPacketToClient(Packet, TargetIdentifier)} should be preferred over this method.
	 * @param packet The packet that should be sent
	 * @param id The {@link TargetIdentifier} of the target
	 * @return 
	 */
	@Override
	public PacketSendFuture sendPacketTo(Packet packet, TargetIdentifier id) {
		sendPacketToClient(packet, id);
		return null;
	}
	
	/**
	 * Sends the {@link Packet} to the specified client. If the {@link TargetIdentifier} does not identify one of this {@link NetworkManagerServer}'s
	 * clients, or if the {@link NetworkConnection} to that client is closed, or if an exception occurred while sending the {@link Packet}'s
	 * data, this method returns <code>false</code>. Otherwise, if the {@link Packet} was sent successfully, <code>true</code> is returned.
	 * @param packet The packet that should be sent
	 * @param clientId The {@link TargetIdentifier} of the target, which should be one of the clients connected to this {@link NetworkManagerServer}
	 * @return Whether the packet was sent successfully
	 */
	public boolean sendPacketToClient(Packet packet, TargetIdentifier clientId) {
		NetworkConnection connection = connections.get(clientId);
		if(connection == null || !connection.isConnectionOpen()) return false;
		try {
			connection.sendPacketToTarget(packet);
		} catch (ConnectionStateException e) {
			return false;
		}
		return true;
	}

	/**
	 * Sends the {@link Packet} to all clients. If an exception occurred while sending the {@link Packet}'s
	 * data, this method returns <code>false</code>. Otherwise, if the {@link Packet} was sent successfully to <b>all</b> clients, <code>true</code> is returned.
	 * @param packet The packet that should be sent
	 * @return Whether all packets were sent successfully
	 */
	public boolean sendPacketsToAllClients(Packet packet) {
		boolean allSuccessful = true;
		synchronized(connections) {
			for(NetworkConnection connection : connections.values()) {
				if(connection.isConnectionOpen()) {
					try {
						connection.sendPacketToTarget(packet);
					} catch (ConnectionStateException e) {
						allSuccessful = false;
					}
				}
			}
		}
		return allSuccessful;
	}
	
	/**
	 * Tests whether this {@link NetworkManagerServer} has a connection to the target identifed by the {@link TargetIdentifier}.
	 * If a connection exists, this does not mean that {@link Packet}s can be sent through the connection, because the existing connection
	 * may not be open.
	 * @param target The target of the connection that should be tested for
	 * @return If this {@link NetworkManagerServer} has a connection to the target
	 * @see #hasOpenConnectionTo(TargetIdentifier)
	 */
	public boolean hasConnectionTo(TargetIdentifier target) {
		return connections.get(target) != null;
	}
	
	/**
	 * Tests whether this {@link NetworkManagerServer} has a connection to the target identifed by the {@link TargetIdentifier}, 
	 * and whether the connection is open, which means that {@link Packet}s can be sent through the connection.
	 * @param target The target of the connection that should be tested for
	 * @return If this {@link NetworkManagerServer} has a connection to the target
	 * @see #hasOpenConnectionTo(TargetIdentifier)
	 */
	public boolean hasOpenConnectionTo(TargetIdentifier target) {
		return connections.get(target) != null && connections.get(target).isConnectionOpen();
	}
	
	/**
	 * Closes the connection to this network target.
	 * @param target The {@link TargetIdentifier} of the target to which the connection should be closed
	 * @return Whether the connection was closed successfully
	 */
	public boolean closeConnectionTo(TargetIdentifier target) {
		NetworkConnection con = connections.get(target);
		if(con == null) return false;
		con.close();
		return true;
	}
	
	/**
	 * Whether this {@link NetworkManagerServer} generally accepts new connections, without calculating whether a
	 * new connection would exceed the connection limit. To test for this as well, see {@link #canAcceptNewConnection()}.
	 * @return Whether this {@link NetworkConnection} accepts new connections at all
	 */
	public boolean isAcceptingConnections() {
		return !closed;
	}
	
	/**
	 * Sets the value that determines whether new connections can be accepted at all.
	 * @param value The new value for the variable
	 * @see #isAcceptingConnections()
	 */
	public void setAcceptsConnections(boolean value) {
		closed = !value;
	}
	
	/**
	 * The amount of connections to this {@link NetworkManagerServer}.
	 * This method is used to determine if the current amount of connections exceeds the connection limit.
	 * @return The amount of connections
	 */
	public int getConnectionCount() {
		return connections.size();
	}
	
	/**
	 * The amount of connections to this {@link NetworkManagerServer} that are open,
	 * that is where {@link NetworkConnection#isConnectionOpen()} returns <code>true</code>.
	 * To determine if the amount of connections exceeds the connection limit, {@link #getConnectionCount()} is used instead.
	 * @return The amount of open connections
	 */
	public int getOpenConnectionCount() {
		return (int) connections.values().stream().filter((c) -> c.isConnectionOpen()).count();
	}
	
	/**
	 * The maximum amount of connections that this {@link NetworkManagerServer} can have.
	 * @return The connection limit
	 */
	public int getConnectionCountLimit() {
		return connectionLimit;
	}
	
	/**
	 * An unmodifiable {@link Set} of all clients that this network manager has a connection to
	 * @return A {@link Set} of all clients that this {@link NetworkManagerServer} is connected to
	 */
	public Set<TargetIdentifier> getClients() {
		return Collections.unmodifiableSet(connections.keySet());
	}
	
	/**
	 * Whether this server will accept an attempt to make a new connection right now,
	 * using the value of {@link #isAcceptingConnections()} and testing whether a new 
	 * connection would exceed the connection limit.
	 * @return Whether a new connection can be made to this server
	 */
	public boolean canAcceptNewConnection() {
		return isAcceptingConnections() && getConnectionCount() < getConnectionCountLimit();
	}
	
	/**
	 * If the remote partner closed the connection, notify that the connection should be removed
	 */
	@Override
	protected void notifyConnectionClosed(NetworkConnection connection) {
		connections.remove(connection.getRemoteTargetId());
	}
	
	/**
	 * Create partner connection for local connection,
	 * and adds it to the own connection list
	 */
	protected LocalNetworkConnection attemptLocalConnection(LocalNetworkConnection connection) throws ConnectionStateException{
		synchronized (connections) {
			if(canAcceptNewConnection()) {
				LocalNetworkConnection connection2 = new LocalNetworkConnection(getSenderID(), connection.getLocalTargetId(), this, connection);
				connections.put(connection2.getRemoteTargetId(), connection2); //The connection on this side will be added
				conHand.accept(connection2); //Send the new local connection to the handler
				return connection2;
			} else {
				throw new ConnectionStateException("Attempted new local connection to server, but server could not accept connection", connection, ConnectionState.UNCONNECTED);
			}
		}
	}
	
	/**
	 * Cenerate a TargetId for new connections
	 */
	private TargetIdentifier generateNewTargetId(Socket socket) {
		return new TargetIdentifier.NetworkTargetIdentifier("ServerToClient-" + nextConnectionID++, (InetSocketAddress) socket.getRemoteSocketAddress());
	}
	
	/**
	 * The connection acceptor thread runnable
	 */
	private void acceptServerConnections() {
		while(true) { //Do this forever, until the thread is terminated
			if(canAcceptNewConnection()) { //This is still true after waiting, because nobody except this thread accepts new connections
				try {
					Socket newConnection = server.accept(); //Wait for a new connection
					//after accepting, sync on the list
					synchronized(connections) {
						TargetIdentifier remoteId = generateNewTargetId(newConnection);
						NetworkConnection connection = new RemoteNetworkConnection(getSenderID(), remoteId,
								this, newConnection);
						if(canAcceptNewConnection()) {
							connections.put(remoteId, connection);
							conHand.accept(connection); //Notify the new handler
						} else { //If not possible, refuse connection immediately
							connection.close();
						}
					}
				} catch (IOException e) {
					//Do nothing, will just try again later
				}
				
			} else {
				try {
					Thread.sleep(CAN_ACCEPT_TEST_DELAY); //try if status of canAcceptServerConnection changed after waiting 
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
	}

	/**
	 * Sets {@link #isAcceptingConnections()} to <code>false</code> and closes all existing connections.
	 * However, new connections can still be made if {@link #setAcceptsConnections(boolean)} is called with <code>true</code> again. 
	 */
	@Override
	public void close() {
		synchronized (connections) { //Sync on map before iterating
			for(NetworkConnection connection : connections.values()) {
				connection.close();
			}
		}
		setAcceptsConnections(false);
	}
	
	/**
	 * Whether this server is local-only, so no {@link ServerSocket} is running and only local connnections are possible
	 * @return Whether this server is local-only
	 */
	public boolean isLocalOnly() {
		return isLocalOnly;
	}
	
	/**
	 * Removes the {@link NetworkManagerServer} from the list of local servers.
	 * After this server was removed, no more local connections can be made.
	 */
	public void removeLocalServer() {
		LocalServerManager.removeServer(this);
	}
	
	/**
	 * Closes all client connections, removes this server from the local
	 * connection list and closes the {@link ServerSocket}. No more connections are possible to this server from this point.
	 * @return Whether the {@link ServerSocket} could be closed successfully
	 */
	public boolean shutdown() {
		close();
		removeLocalServer();
		if(server != null) {
			try {
				server.close();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	
}
