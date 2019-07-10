package lb.simplebase.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionAcceptorThread extends Thread{

	private static final AtomicInteger threadIds = new AtomicInteger(0);
	
	private ServerSocket socket;
	private SocketNetworkManagerServer server;
	
	public ConnectionAcceptorThread(ServerSocket socket, SocketNetworkManagerServer server) {
		super("SocketAcceptor-"+threadIds.getAndIncrement());
		setDaemon(true);
		this.socket = socket;
		this.server = server;
	}

	@Override
	public void run() {
		NetworkManager.NET_LOG.info("Started Connection Acceptor");
		while(!socket.isClosed()) {
			if(Thread.interrupted()) {
				Thread.currentThread().interrupt();
				NetworkManager.NET_LOG.info("Connection Acceptor: Closing: Thread was interrupted");
				return;
			}
			if(!socket.isBound()) { //Just wait a bit
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) { //Except when we should not wait
					Thread.currentThread().interrupt();
					NetworkManager.NET_LOG.info("Connection Acceptor: Closing: Thread was interrupted (wait for binding)");
					return;
				}
				continue;
			}
			try {
				Socket newSocket = socket.accept();
				server.acceptIncomingUnconfirmedConnection(newSocket);
			} catch (SocketException e) {
//				e.printStackTrace();
				NetworkManager.NET_LOG.info("Connection Acceptor: Closing: ServerSocket was closed");
				return; //When another thread calls close
			} catch (IOException e) {
				e.printStackTrace();
				NetworkManager.NET_LOG.error("Connection Acceptor: Closing: ServerSocket IO error", e);
				return;
			}
		}
	}
	
}
