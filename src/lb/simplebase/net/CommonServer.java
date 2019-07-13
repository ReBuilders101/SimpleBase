package lb.simplebase.net;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public abstract class CommonServer extends NetworkManager implements NetworkManagerServer, LocalConnectionServer {

	protected final ServerConfiguration config;
	
	protected final Set<AbstractNetworkConnection> clientList;
	protected final ReadWriteLock clientListLock;
	
	protected final InboundPacketThreadHandler handler;
	protected final PacketDistributor toAllHandlers;
	
	protected volatile ServerState state;
	
	
	protected CommonServer(TargetIdentifier localId, ServerConfiguration config) {
		super(localId);
		
		this.config = config;
		this.state = ServerState.INITIALIZED;
		
		this.clientList = new HashSet<>();
		this.clientListLock = new ReentrantReadWriteLock(true);
		
		this.toAllHandlers = new PacketDistributor();
		this.handler = new InboundPacketThreadHandler(toAllHandlers, config.getHandlerThreadCount());
	}
	
	@Override
	protected void notifyConnectionClosed(AbstractNetworkConnection connection) {
		try {
			clientListLock.writeLock().lock();
			clientList.remove(connection);
		} finally {
			clientListLock.writeLock().unlock();
		}
	}
	
	@Override
	public LocalNetworkConnection attemptLocalConnection(LocalNetworkConnection connection) {
		LocalNetworkConnection con = new LocalNetworkConnection(getLocalID(), connection.getLocalTargetId(), this, connection);
		try {
			clientListLock.writeLock().lock();
			clientList.add(con);
		} finally {
			clientListLock.writeLock().unlock();
		}
		NetworkManager.NET_LOG.info("Server Manager: Accepted local connection (" + connection.getLocalTargetId() +")");
		return con;
	}
	
	@Override
	public ServerConfiguration getConfiguration() {
		return config;
	}
	
	@Override
	public synchronized PacketSendFuture sendPacketToClient(Packet packet, TargetIdentifier client) {
		AbstractNetworkConnection con = getCurrentClient(client);
		if(con == null) return PacketSendFuture.quickFailed("Target ID is not a client on this server");
		if(!con.isConnectionOpen()) return PacketSendFuture.quickFailed("Connection to client is not open");
		return con.sendPacketToTarget(packet);
	}

	@Override
	public Set<TargetIdentifier> getCurrentClients() {
		return Collections.unmodifiableSet(clientList.stream().map((anc) -> anc.getRemoteTargetId()).collect(Collectors.toSet()));
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
	
	@Override
	public boolean isCurrentClient(TargetIdentifier client) {
		return getCurrentClient(client) != null;
	}

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

	@Override
	public int getCurrentClientCount() {
		try {
			clientListLock.readLock().lock();
			return clientList.size();
		}finally {
			clientListLock.readLock().unlock();
		}
	}
	
	@Override
	public ServerState getServerState() {
		return state;
	}
	
	@Override
	public void processPacket(Packet received, TargetIdentifier source) {
		handler.accept(received, source);
	}

	@Override
	public void addIncomingPacketHandler(PacketReceiver handler) {
		toAllHandlers.addPacketReceiver(handler);
	}
}
