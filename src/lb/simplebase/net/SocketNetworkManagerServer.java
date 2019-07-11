package lb.simplebase.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class SocketNetworkManagerServer extends NetworkManager implements NetworkManagerServer, LocalConnectionServer{

	
	protected SocketNetworkManagerServer(ServerConfiguration config, TargetIdentifier localId) throws IOException {
		super(localId);
		clientList = new HashSet<>();
		this.config = config;
		clientListLock = new ReentrantReadWriteLock(true);
		
		serverSocket = new ServerSocket();
		acceptor = new ConnectionAcceptorThread(serverSocket, this);
		toAllHandlers = new PacketDistributor();
		handler = new InboundPacketThreadHandler(toAllHandlers, config.getHandlerThreadCount());
		state = ServerState.INITIALIZED;
		localOnly = localId.isLocalOnly();
	}

	//Sync any method with clientlist
	private Set<AbstractNetworkConnection> clientList;
	private ServerConfiguration config;
	private ReadWriteLock clientListLock;
	
	private ServerSocket serverSocket;
	private ConnectionAcceptorThread acceptor;
	
	private InboundPacketThreadHandler handler;
	private PacketDistributor toAllHandlers;
	
	private ServerState state;
	private final boolean localOnly;
	
	@Override
	public ServerConfiguration getConfiguration() {
		return config;
	}
	
	public void acceptIncomingUnconfirmedConnection(Socket newConnectionSocket) {
		try {
			NetworkManager.NET_LOG.info("Server Manager: Remote connection attempted (" + newConnectionSocket.getRemoteSocketAddress() + ")");
			clientListLock.writeLock().lock();
			if(config.canAcceptConnection(this, ConnectionInformation.create(newConnectionSocket))) {
				TargetIdentifier remote = RemoteIDGenerator.generateID((InetSocketAddress) newConnectionSocket.getRemoteSocketAddress());
				
				try {
					newConnectionSocket.setSoTimeout(config.getTimeout());
					newConnectionSocket.setTcpNoDelay(config.getNoDelay());
					newConnectionSocket.setKeepAlive(config.getKeepAlive());
				} catch (SocketException e) {
					e.printStackTrace();
				}
				AbstractNetworkConnection newConn = new RemoteNetworkConnection(getLocalID(), remote, this, newConnectionSocket);
				NetworkManager.NET_LOG.info("Server Manager: Remote connection accepted successfully (" + remote + ")");
				clientList.add(newConn);
			}
		}finally {
			clientListLock.writeLock().unlock();
		}
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
	public ServerStateFuture startServer() {
		if(state == ServerState.INITIALIZED) {
			NetworkManager.NET_LOG.info("Server Manager: Starting server...");
			return ServerStateFuture.create(state, (f) -> {
				LocalConnectionManager.addServer(this);
				try {
					if(!localOnly) serverSocket.bind(getLocalID().getConnectionAddress());
				} catch (IOException e) {
					f.ex = e;
					f.errorMessage = e.getMessage();
					return;
				}
				if(!localOnly) acceptor.start();
				state = ServerState.STARTED;
				f.currentState = state;
				NetworkManager.NET_LOG.info("Server Manager: Server start complete.");
			}).run();
		} else {
			return ServerStateFuture.quickFailed("Server has already been started", state);
		}
	}

	@Override
	public ServerStateFuture stopServer() {
		if(state == ServerState.STOPPED) {
			return ServerStateFuture.quickDone(ServerState.STOPPED);
		} else {
			NetworkManager.NET_LOG.info("Server Manager: Stopping server...");
			return ServerStateFuture.create(getServerState(), (f) -> {
				LocalConnectionManager.removeServer(this);
				//Then kick everyone
				NetworkManager.NET_LOG.info("Server Manager: Disconnecting all clients");
				for(AbstractNetworkConnection con : clientList) {
					con.close();
				}
				handler.shutdownExecutor();
				try {
					serverSocket.close();
				} catch (IOException e) {
					f.ex = e;
					f.errorMessage = e.getMessage();
					return;
				}
				state = ServerState.STOPPED;
				f.currentState = state;
				NetworkManager.NET_LOG.info("Server Manager: Server stop complete.");
			}).run();
		}
	}

	@Override
	public ServerState getServerState() {
		return state;
	}

	@Override
	public void notifyConnectionClosed(AbstractNetworkConnection connection) {
		clientList.remove(connection);
	}

	@Override
	public void processPacket(Packet received, TargetIdentifier source) {
		handler.accept(received, source);
	}

	@Override
	public void addIncomingPacketHandler(PacketReceiver handler) {
		toAllHandlers.addPacketReceiver(handler);
	}

	@Override
	protected void shutdown() {
		stopServer();
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

}
