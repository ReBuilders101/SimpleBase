package lb.simplebase.net;

import java.net.InetAddress;
import java.net.Socket;

public interface ConnectionInformation {

	public InetAddress getAddress();
	
	public static ConnectionInformation create(Socket socket) {
		return new ConnectionInformation() {
			
			@Override
			public InetAddress getAddress() {
				return socket.getInetAddress();
			}
		};
	}
}
