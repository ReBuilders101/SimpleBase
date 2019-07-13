package lb.simplebase.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

class SocketNetworkManagerServer extends CommonServer implements LocalConnectionServer{

	
	protected SocketNetworkManagerServer(ServerConfiguration config, TargetIdentifier localId) throws IOException {
		super(localId, config);
		
		serverSocket = new ServerSocket();
		acceptor = new ConnectionAcceptorThread(serverSocket, this);
	}
	
	private ServerSocket serverSocket;
	private ConnectionAcceptorThread acceptor;
	
	protected void acceptIncomingUnconfirmedConnection(Socket newConnectionSocket) {
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
	public ServerStateFuture startServer() {
		if(state == ServerState.INITIALIZED) {
			NetworkManager.NET_LOG.info("Server Manager: Starting server...");
			return ServerStateFuture.create(state, (f) -> {
				LocalConnectionManager.addServer(this);
				try {
					serverSocket.bind(getLocalID().getConnectionAddress());
				} catch (IOException e) {
					f.setErrorAndMessage(e);
					return;
				}
				acceptor.start();
				state = ServerState.STARTED;
				f.setServerState(state);
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
					f.setErrorAndMessage(e);
					return;
				}
				state = ServerState.STOPPED;
				f.setServerState(state);
				NetworkManager.NET_LOG.info("Server Manager: Server stop complete.");
			}).run();
		}
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
