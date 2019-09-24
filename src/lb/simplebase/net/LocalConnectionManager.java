package lb.simplebase.net;

import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import lb.simplebase.util.NamedThreadFactory;

//Instead of binding Sockets, bind connections here
/**
 * Acts as a place to request a connection from a local server, as an analogy to a remote servers {@link ServerSocket}. 
 */
public final class LocalConnectionManager {
	
	private static final int SERVER_CHECK_INTERVAL = 50;
	
	private static final ExecutorService localPacketOutputThread = Executors.newCachedThreadPool(new NamedThreadFactory("LocalPacketProcessing-"));
	private static final Map<TargetIdentifier, LocalConnectionServer> servers = Collections.synchronizedMap(new HashMap<>());
	
	public static void addServer(LocalConnectionServer server) {
		servers.put(server.getLocalID(), server);
	}
	
	public static void removeServer(NetworkManagerServer server) {
		servers.remove(server.getLocalID());
	}
	
	public static void submitLocalPacketTask(Runnable task) {
		localPacketOutputThread.execute(task);
	}

	public static void shutdownExecutor() {
		localPacketOutputThread.shutdown();
	}
	
	protected static Map<TargetIdentifier, LocalConnectionServer> getServers() {
		return servers;
	}
	
	/**
	 * 
	 * @param connection A {@link NetworkConnection} to the server that should be connected to
	 * @return A {@link NetworkConnection} to the server, from the server's perspective
	 * @throws ConnectionStateException When the server was not found or when the server refused the connection
	 */
	protected static LocalNetworkConnection getLocalConnectionServer(LocalNetworkConnection connection) {
		TargetIdentifier key = connection.getRemoteTargetId();
		LocalConnectionServer server = servers.get(key);
		if(server != null) {
			return server.attemptLocalConnection(connection);
		} else {
			return null;
		}
	}
	
	protected static boolean canMakeConnectionTo(TargetIdentifier server) {
		return servers.get(server) != null;
	}
	
	/**
	 * Blocking for timout ms or until a connection  was found
	 * @param connection
	 * @param timeout Timeout in ms
	 * @return
	 * @throws ConnectionStateException
	 * @see {@link #getLocalConnectionServer(LocalNetworkConnection)}
	 */
	public static LocalNetworkConnection waitForLocalConnectionServer(LocalNetworkConnection connection, int timeout) throws TimeoutException, InterruptedException{
		final long timeoutTime = System.currentTimeMillis() + timeout;
		while(System.currentTimeMillis() < timeoutTime) {
			if(Thread.interrupted()) { //Handle interrupts
				Thread.currentThread().interrupt();
				throw new InterruptedException("Thread was had interrupted status set while waiting for server");
			}
			LocalNetworkConnection con = getLocalConnectionServer(connection);
			if(con != null) return con;
			Thread.sleep(SERVER_CHECK_INTERVAL); //Let others do work, we pause 
		}
		throw new TimeoutException("The timeout time (" + timeout + "ms) expired before a server was found"); 
	}
}
