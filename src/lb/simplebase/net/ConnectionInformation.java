package lb.simplebase.net;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Holds information about an attempted connection while the 
 * {@link TargetIdentifier} and {@link AbstractNetworkConnection} have not been created yet.
 */
@Deprecated
public interface ConnectionInformation {

	/**
	 * The {@link InetAddress} of the network destination that attempted the connection.
	 * @return The {@link InetAddress} that attempted the connection
	 */
	public InetAddress getAddress();
	
	/**
	 * Creates a {@link ConnectionInformation} object based on the state of a {@link Socket}
	 * @param socket The socket
	 * @return The connection information object
	 */
	public static ConnectionInformation create(Socket socket) {
		return new ConnectionInformation() {
			
			@Override
			public InetAddress getAddress() {
				return socket.getInetAddress();
			}
		};
	}
}
