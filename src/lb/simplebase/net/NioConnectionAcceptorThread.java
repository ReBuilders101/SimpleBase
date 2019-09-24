package lb.simplebase.net;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

class NioConnectionAcceptorThread extends Thread {

	private static final AtomicInteger threadIds = new AtomicInteger(0);
	
	private final NioNetworkManagerServer server;
	private final ServerSocketChannel channel;
	
	protected NioConnectionAcceptorThread(NioNetworkManagerServer server, ServerSocketChannel channel) {
		super("SocketAcceptor-"+threadIds.getAndIncrement());
		setDaemon(true);
		
		assert channel.isBlocking();
		this.channel = channel;
		this.server = server;
	}
	
	
	@Override
	public void run() {
		NetworkManager.NET_LOG.info("Started Connection Acceptor");
		while(channel.isOpen()) {
			if(Thread.interrupted()) {
				Thread.currentThread().interrupt();
				NetworkManager.NET_LOG.info("Connection Acceptor: Closing: Thread was interrupted");
				break;
			}
			try {
				SocketChannel newChannel = channel.accept();
				server.acceptIncomingUnconfirmedConnection(newChannel);
			} catch (ClosedSelectorException e) {
				NetworkManager.NET_LOG.error("Connection Acceptor: Closing: Selector is closed", e);
				return;
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Connection Acceptor: Closing: Selector IO error", e);
			}
		}
	}
}
