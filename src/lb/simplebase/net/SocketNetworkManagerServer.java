package lb.simplebase.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import lb.simplebase.event.EventResult;
import lb.simplebase.util.ExceptionUtils;

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
		if(ExceptionUtils.wrapException(() -> result.wasCanceled(), true)) {
			NetworkManager.NET_LOG.info("Server Manager: Remote connection rejected (" + newConnectionSocket.getRemoteSocketAddress() + ")");
			try {
				newConnectionSocket.close();
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Server Manager: Could not close socket of rejected connection", e);
			}
		} else {
			TargetIdentifier remote = RemoteIDGenerator.generateID((InetSocketAddress) newConnectionSocket.getRemoteSocketAddress());
			final EventResult result2 = bus.post(new ConfigureConnectionEvent(newConnectionSocket, remote, this));
			final ConfigureConnectionEvent handledEvent = (ConfigureConnectionEvent) ExceptionUtils.wrapException(() -> result2.getHandledEvent(), result2.getCurrentEvent());
			NetworkConnection newCon = new RemoteNetworkConnection(getLocalID(), remote, this, newConnectionSocket, true, handledEvent.getCustomObject());
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
	public void startServer() {
		if(state == ServerState.INITIALIZED) {
			NetworkManager.NET_LOG.info("Server Manager: Starting server...");
			LocalConnectionManager.addServer(this);
			try {
				serverSocket.bind(getLocalID().getConnectionAddress());
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Server Manager: Error while binding socket", e);
				return;
			}
			acceptor.start();
			state = ServerState.STARTED;
			NetworkManager.NET_LOG.info("Server Manager: Server start complete.");
		} else {
			NetworkManager.NET_LOG.warn("Server Manager: Server has already been started");
		}
	}

	@Override
	public void stopServer() {
		if(state == ServerState.STOPPED) {
			NetworkManager.NET_LOG.info("Server Manager: Server already stopped");
			return;
		} else {
			NetworkManager.NET_LOG.info("Server Manager: Stopping server...");
			LocalConnectionManager.removeServer(this);
			//Then kick everyone
			NetworkManager.NET_LOG.info("Server Manager: Disconnecting all clients");
			for(NetworkConnection con : clientList) {
				NetworkManager.NET_LOG.debug("Closing client connection: " + con.getRemoteTargetId());
				con.close();
			}
			handler.shutdownExecutor();
			try {
				serverSocket.close();
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Server Manager: Error while closing Socket", e);
				return;
			}
			state = ServerState.STOPPED;
			NetworkManager.NET_LOG.info("Server Manager: Server stop complete.");
		}
	}

	@Override
	protected void shutdown() {
		stopServer();
	}

}
