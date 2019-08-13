package lb.simplebase.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

class SocketNetworkManagerServer extends CommonServer {

	
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
				onNewConnection(remote);
				clientList.add(newConn);
			} else {
				NetworkManager.NET_LOG.info("Server Manager: Remote connection rejected (" + newConnectionSocket.getRemoteSocketAddress() + ")");
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
					NetworkManager.NET_LOG.debug("Closing client connection: " + con.getRemoteTargetId());
					con.close().trySync();
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
	protected void shutdown() {
		stopServer();
	}

}
