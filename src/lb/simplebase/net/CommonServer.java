package lb.simplebase.net;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import lb.simplebase.event.EventResult;
import lb.simplebase.util.ReflectedMethod;

/**
 * Implements common behavior and features of a {@link NetworkManagerServer}.<br>
 * Should be used when a custom implementation of that interface is required.
 */
public abstract class CommonServer extends NetworkManager implements NetworkManagerServer, LocalConnectionServer {
	
	protected final Set<AbstractNetworkConnection> clientList;
	protected final ReadWriteLock clientListLock;
	
	protected final InboundPacketThreadHandler handler;
	protected final PacketDistributor toAllHandlers;
	
	protected volatile ServerState state;
	
	
	protected CommonServer(TargetIdentifier localId, int threads) {
		super(localId);
		
		this.state = ServerState.INITIALIZED;
		
		this.clientList = new HashSet<>();
		this.clientListLock = new ReentrantReadWriteLock(true);
		
		this.toAllHandlers = new PacketDistributor();
		this.handler = new InboundPacketThreadHandler(toAllHandlers, threads);
	}
	
	
	@Override
	protected void notifyConnectionClosed(AbstractNetworkConnection connection, ClosedConnectionEvent.Cause cause) {
		super.notifyConnectionClosed(connection, cause);
		try {
			clientListLock.writeLock().lock();
			clientList.remove(connection);
		} finally {
			clientListLock.writeLock().unlock();
		}
	}
	
	/**
	 * Completes a in-application local connection with this server.<br>
	 * Normally not called by application code.
	 * @param connection The local view of the connection
	 * @return The remote view of the connection
	 */
	@Override
	public LocalNetworkConnection attemptLocalConnection(LocalNetworkConnection connection) {
		final EventResult result = bus.post(new ConfigureConnectionEvent(connection.getLocalTargetId(), this));
		final ConfigureConnectionEvent handledEvent = (ConfigureConnectionEvent) ReflectedMethod.wrapException(() -> result.getHandledEvent(), result.getCurrentEvent());
		LocalNetworkConnection con = new LocalNetworkConnection(getLocalID(), connection.getLocalTargetId(), this, connection, true, handledEvent.getCustomObject());
		try {
			clientListLock.writeLock().lock();
			clientList.add(con);
		} finally {
			clientListLock.writeLock().unlock();
		}
		NetworkManager.NET_LOG.info("Server Manager: Accepted local connection (" + connection.getLocalTargetId() +")");
		return con;
	}
	
	/**
	 * Sends a packet to one client.<br>
	 * Sending is done on a different thread. To ensure that sending is complete, call
	 * {@link PacketSendFuture#sync()} or {@link PacketSendFuture#ensurePacketSent()}.
	 * @param packet The {@link Packet} that should be sent
	 * @param client The {@link TargetIdentifier} that this packet should be sent to
	 * @return A {@link PacketSendFuture} containing information about sending progress, success and errors
	 */
	@Override
	public synchronized PacketSendFuture sendPacketToClient(Packet packet, TargetIdentifier client) {
		AbstractNetworkConnection con = getCurrentClient(client);
		if(con == null) return PacketSendFuture.quickFailed("Target ID is not a client on this server");
		if(!con.isConnectionOpen()) return PacketSendFuture.quickFailed("Connection to client is not open");
		return con.sendPacketToTarget(packet);
	}

	/**
	 * Creates a set of all remote {@link TargetIdentifier}s of the active connections.
	 * @return All clients connected to the server
	 */
	@Override
	public Set<TargetIdentifier> getCurrentClients() {
		try {
			clientListLock.readLock().lock();
			final Set<TargetIdentifier> set = clientList.stream().map((anc) -> anc.getRemoteTargetId()).collect(Collectors.toSet());
			return Collections.unmodifiableSet(set);
		} finally {
			clientListLock.readLock().unlock();
		}
	}

	protected AbstractNetworkConnection getCurrentClient(TargetIdentifier client) {
		try {
			clientListLock.readLock().lock();
			for(AbstractNetworkConnection con : clientList) {
				if(con.getRemoteTargetId().equals(client)) return con;
			}
			return null;
		} finally {
			clientListLock.readLock().unlock();
		}
	}
	
	/**
	 * Checks whether this server has a connection to the client.
	 * @param The remote {@link TargetIdentifier} of the client to search for
	 */
	@Override
	public boolean isCurrentClient(TargetIdentifier client) {
		return getCurrentClient(client) != null;
	}

	/**
	 * Closes the connection to a client and removes it from the client list. To ensure that the connection is closed, call
	 * {@link ConnectionStateFuture#sync()}.
	 * @param client The {@link TargetIdentifier} of the client to remove
	 * @return A {@link ConnectionStateFuture} containing information about progress, success and errors
	 */
	@Override
	public ConnectionStateFuture disconnectClient(TargetIdentifier client) {
		AbstractNetworkConnection con = getCurrentClient(client);
		if(con == null) {
			return ConnectionStateFuture.quickFailed("No client with this Id was found", ConnectionState.UNCONNECTED); //Maybe closed is better?
		} else {
			NetworkManager.NET_LOG.info("Server Manager: Disconnecting client (" + con.getRemoteTargetId() + ")");
			return con.close();
		}
	}

	/**
	 * Returns the number of client connections that this server has active. 
	 * @return The amount of clients
	 */
	@Override
	public int getCurrentClientCount() {
		try {
			clientListLock.readLock().lock();
			return clientList.size();
		}finally {
			clientListLock.readLock().unlock();
		}
	}
	
	/**
	 * The state of the server
	 * @return The state of the server
	 */
	@Override
	public ServerState getServerState() {
		return state;
	}
	
	/**
	 * Called when a connection of this server receives a packet.
	 * Normally not called by application code, but can be used to simulate a received packet.
	 * @param received The packet that was received by this connection
	 * @param source The remote {@link TargetIdentifier} of the connection that received the packet
	 */
	@Override
	public void processPacket(Packet received, PacketContext source) {
		handler.accept(received, source);
	}

	/**
	 * Adds a {@link PacketReceiver} that will be called when a packet is received by the network manager.
	 * @param handler The new {@link PacketReceiver}
	 */
	@Override
	public void addIncomingPacketHandler(PacketReceiver handler) {
		toAllHandlers.addPacketReceiver(handler);
	}
}
