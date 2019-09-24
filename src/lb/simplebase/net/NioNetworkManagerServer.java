package lb.simplebase.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import lb.simplebase.event.EventResult;
import lb.simplebase.util.ExceptionUtils;

public class NioNetworkManagerServer extends CommonServer {

	private final ServerSocketChannel channel;
	private final NioConnectionAcceptorThread acceptor;
	private final Selector dataReceiverSelector;
	
	protected NioNetworkManagerServer(TargetIdentifier localId, ServerSocketChannel channel, int threads) throws IOException {
		super(localId, threads);
		this.channel = channel;
		
		this.acceptor = new NioConnectionAcceptorThread(this, channel);
		this.dataReceiverSelector = Selector.open();
	}

	protected void acceptIncomingUnconfirmedConnection(SocketChannel socketChannel) { //channel will be in blocking mode
		NetworkManager.NET_LOG.info("Server Manager: Remote connection attempted (" + socketChannel.socket().getRemoteSocketAddress() + ")");

		//Post the event
		final EventResult result = bus.post(new AttemptedConnectionEvent(socketChannel.socket().getInetAddress(), this));
		if(ExceptionUtils.wrapException(() -> result.wasCanceled(), true)) {
			NetworkManager.NET_LOG.info("Server Manager: Remote connection rejected (" + socketChannel.socket().getRemoteSocketAddress() + ")");
			try {
				socketChannel.close();
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Server Manager: Could not close socket of rejected connection", e);
			}
		} else {
			TargetIdentifier remote = RemoteIDGenerator.generateID((InetSocketAddress) socketChannel.socket().getRemoteSocketAddress());
			final EventResult result2 = bus.post(new ConfigureConnectionEvent(socketChannel.socket(), remote, this));
			final ConfigureConnectionEvent handledEvent = (ConfigureConnectionEvent) ExceptionUtils.wrapException(() -> result2.getHandledEvent(), result2.getCurrentEvent());
			NetworkConnection newCon = new NioNetworkConnection(getLocalID(), remote, this, socketChannel, true, handledEvent.getCustomObject());
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
			try {
				channel.bind(getLocalID().getConnectionAddress());
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
				channel.close();
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Server Manager: Error while closing Channel or Selector", e);
				return;
			}
			state = ServerState.STOPPED;
			NetworkManager.NET_LOG.info("Server Manager: Server stop complete.");
		}
	}
	
	protected SelectionKey registerConnectionChannel(NioNetworkConnection createdConnection) {
		try {
			SelectionKey key = createdConnection.getChannel().register(dataReceiverSelector, SelectionKey.OP_READ);
			key.attach(new NioPacketFactory(this, (p) -> this.processPacket(p, createdConnection.getContext()), createdConnection));
			return key;
		} catch (ClosedChannelException e) {
			NetworkManager.NET_LOG.error("[Server Manager]: Tried to regsiter a closed channel. Channel data will not be read by server", e);
			return null;
		}
	}

	@Override
	protected void shutdown() {
		stopServer();
	}

}
