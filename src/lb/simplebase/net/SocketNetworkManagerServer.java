package lb.simplebase.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import lb.simplebase.event.EventResult;
import lb.simplebase.util.ReflectedMethod;

class SocketNetworkManagerServer extends CommonServer {

	
	protected SocketNetworkManagerServer(TargetIdentifier localId, ServerSocket socket, int threads) {
		super(localId, threads);
		serverSocket = socket;
		acceptor = new ConnectionAcceptorThread(serverSocket, this);
	}
	
	private final ServerSocket serverSocket;
	private final ConnectionAcceptorThread acceptor;
	
	protected void acceptIncomingUnconfirmedConnection(Socket newConnectionSocket) {
		NetworkManager.NET_LOG.info("Server Manager: Remote connection attempted (" + newConnectionSocket.getRemoteSocketAddress() + ")");
		
		//Post the event
		final EventResult result = bus.post(new AttemptedConnectionEvent(newConnectionSocket.getInetAddress(), this));
		if(ReflectedMethod.wrapException(() -> result.wasCanceled(), true)) { //TODO move the method somewhere else
			NetworkManager.NET_LOG.info("Server Manager: Remote connection rejected (" + newConnectionSocket.getRemoteSocketAddress() + ")");
			try {
				newConnectionSocket.close();
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Server Manager: Could not close socket of rejected connection", e);
			}
		} else {
			TargetIdentifier remote = RemoteIDGenerator.generateID((InetSocketAddress) newConnectionSocket.getRemoteSocketAddress());
			final EventResult result2 = bus.post(new ConfigureConnectionEvent(newConnectionSocket, remote, this));
			final ConfigureConnectionEvent handledEvent = (ConfigureConnectionEvent) ReflectedMethod.wrapException(() -> result2.getHandledEvent(), result2.getCurrentEvent());
			AbstractNetworkConnection newCon = new RemoteNetworkConnection(getLocalID(), remote, this, newConnectionSocket, true, handledEvent.getCustomObject());
			try {
				clientListLock.writeLock().lock();
				clientList.add(newCon);
				NetworkManager.NET_LOG.info("Server Manager: Remote connection accepted successfully (" + remote + ")");
			} finally {
				clientListLock.writeLock().unlock();
			}
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
					con.close().runInSync();
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
