package lb.simplebase.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import lb.simplebase.event.EventResult;

class SocketNetworkManagerServer extends CommonServer {

	protected SocketNetworkManagerServer(TargetIdentifier localId, ServerSocket socket, int threads, boolean udpDiscovery, byte[] sequence) throws SocketException {
		super(localId, threads);
		serverSocket = socket;
		acceptor = new ConnectionAcceptorThread(serverSocket, this);
		allowDatagramDiscovery = udpDiscovery;
		
		if(udpDiscovery) {
			receiverSocket = new DatagramSocket(null); //unbound
			
			//normalize sequence
			byte[] seq = new byte[7];
			System.arraycopy(sequence, 0, seq, 0, Math.min(7, sequence.length));
			echoThread = new LanServerDiscoveryEchoThread(this, seq);
		} else {
			receiverSocket = null;
			echoThread = null;
		}
	}

	private final ServerSocket serverSocket;
	private final ConnectionAcceptorThread acceptor;
	
	private final boolean allowDatagramDiscovery;
	private final DatagramSocket receiverSocket;
	private final LanServerDiscoveryEchoThread echoThread;
	
	protected DatagramSocket getDatagramSocket() {
		return receiverSocket;
	}
	
	@Override
	public boolean allowDatagramDiscovery() {
		return allowDatagramDiscovery;
	}
	
	protected byte attemptUdpConnection(InetAddress source) {
		NetworkManager.NET_LOG.info("Server Manager: UDP connection check (" + source + ")");

		//Post the event
		final EventResult result = bus.post(new AttemptedConnectionEvent(source, this));
		if(result.isCanceled()) {
			NetworkManager.NET_LOG.info("Server Manager: UDP connection check rejected (" + source + ")");
			return LanServerDiscovery.DENIED;
		} else {
			NetworkManager.NET_LOG.info("Server Manager: UDP connection check accepted (" + source + ")");
			return LanServerDiscovery.SUCCESS;
		}
	}
	
	protected void acceptIncomingUnconfirmedConnection(Socket newConnectionSocket) {
		NetworkManager.NET_LOG.info("Server Manager: Remote connection attempted (" + newConnectionSocket.getRemoteSocketAddress() + ")");

		//Post the event
		final EventResult result = bus.post(new AttemptedConnectionEvent(newConnectionSocket.getInetAddress(), this));
		if(result.isCanceled()) {
			NetworkManager.NET_LOG.info("Server Manager: Remote connection rejected (" + newConnectionSocket.getRemoteSocketAddress() + ")");
			try {
				newConnectionSocket.close();
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Server Manager: Could not close socket of rejected connection", e);
			}
		} else {
			TargetIdentifier remote = RemoteIDGenerator.generateID((InetSocketAddress) newConnectionSocket.getRemoteSocketAddress());
			final EventResult result2 = bus.post(new ConfigureConnectionEvent(newConnectionSocket, remote, this));
			final ConfigureConnectionEvent handledEvent = result2.getEvent(ConfigureConnectionEvent.class);
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
				getLocalID().bindSocket(() -> serverSocket);
//				serverSocket.bind(getLocalID().getConnectionAddress());
				if(allowDatagramDiscovery) getLocalID().bindDatagram(() -> receiverSocket);
//					receiverSocket.bind(getLocalID().getConnectionAddress());
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Server Manager: Error while binding socket", e);
				return;
			}
			acceptor.start();
			if(allowDatagramDiscovery) echoThread.start();
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
				if(allowDatagramDiscovery) receiverSocket.close();
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
