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
		while(ConnectionState.fromSocket(socket).canSendData()) {
			if(Thread.interrupted()) {
				Thread.currentThread().interrupt();
				break;
			}
			try {
				Socket newSocket = socket.accept();
				server.acceptIncomingUnconfirmedConnection(newSocket);
			} catch (SocketException e) {
				return; //When another thread calls close
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	
}
