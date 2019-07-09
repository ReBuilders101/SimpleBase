package lb.simplebase.net;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import lb.simplebase.net.done.AbstractNetworkConnection;

//Instead of binding Sockets, bind connections here
/**
 * Acts as a place to request a connection from a local server, as an analogy to a remote servers {@link ServerSocket}. 
 */
final class LocalServerManager {
	
	private static final Map<TargetIdentifier, NetworkManagerServer> servers = new HashMap<>();
	
	protected static void addServer(NetworkManagerServer server) {
		servers.put(server.getID(), server);
	}
	
	protected static void removeServer(NetworkManagerServer server) {
		servers.remove(server.getID());
	}
	
	/**
	 * 
	 * @param connection A {@link AbstractNetworkConnection} to the server that should be connected to
	 * @return A {@link AbstractNetworkConnection} to the server, from the server's perspective
	 * @throws ConnectionStateException When the server was not found or when the server refused the connection
	 */
	protected static LocalNetworkConnection getLocalConnectionServer(LocalNetworkConnection connection) throws ConnectionStateException{
		TargetIdentifier key = connection.getRemoteTargetId();
		NetworkManagerServer server = servers.get(key);
		if(server != null) {
			return server.attemptLocalConnection(connection);
		} else {
			throw new ConnectionStateException("Server for local connection was not found", connection, ConnectionState.UNCONNECTED);
		}
	}
	
	protected static boolean canMakeConnectionTo(TargetIdentifier server) {
		return servers.get(server) != null && servers.get(server).canAcceptNewConnection();
	}
	
	/**
	 * Blocking for timout ms or until a connection  was found
	 * @param connection
	 * @param timeout Timeout in ms
	 * @return
	 * @throws ConnectionStateException
	 * @see {@link #getLocalConnectionServer(LocalNetworkConnection)}
	 */
	protected static LocalNetworkConnection waitForLocalConnectionServer(LocalNetworkConnection connection, int timeout) throws ConnectionStateException{
		final long timeoutTime = System.currentTimeMillis() + timeout;
		final TargetIdentifier target = connection.getRemoteTargetId();
		while(System.currentTimeMillis() < timeoutTime) {
			if(canMakeConnectionTo(target)) {
				return getLocalConnectionServer(connection);
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new ConnectionStateException("Thread was interrupted while waiting for connection", e, connection, ConnectionState.UNCONNECTED);
				}
			}
		}
		throw new ConnectionStateException("A connection to the requested local server could not be made", connection, ConnectionState.UNCONNECTED);
	}
}
