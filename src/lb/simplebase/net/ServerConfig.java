package lb.simplebase.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class ServerConfig {

	private final ServerSocket socket;
	private int threads;
	
	protected ServerConfig(ServerSocket soc) throws IOException {
		socket = soc;
		threads = 0; //Zero means unlimited threads
	}
	
	/**
	 * @see ServerSocket#setReceiveBufferSize(int)
	 */
	public boolean setReceiveBufferSize(int value) {
		if(socket == null) return false;
		try {
			socket.setReceiveBufferSize(value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set server socket option 'receiveBufferSize' to " + value, e);
			return false;
		} catch (IllegalArgumentException e) {
			NetworkManager.NET_LOG.warn("Could not set server socket option 'receiveBufferSize' to invalid value " + value, e);
			return false;
		}
	}
	
	/**
	 * @see ServerSocket#setReuseAddress(boolean)
	 */
	public boolean setReuseAddress(boolean value) {
		if(socket == null) return false;
		try {
			socket.setReuseAddress(value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set server socket option 'reuseAddress' to " + value, e);
			return false;
		}
	}
	
	/**
	 * @see ServerSocket#setSoTimeout(int)
	 */
	public boolean setSoTimeout(int value) {
		if(socket == null) return false;
		try {
			socket.setSoTimeout(value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set server socket option 'SoTimeout' to " + value, e);
			return false;
		}
	}
	
	public ServerConfig setProcessingThreadCount(int count) {
		threads = count;
		return this;
	}
	
	/**
	 * Internal use only
	 * @return
	 */
	public ServerSocket configuredSocket() {
		return socket;
	}
	
	protected int getThreadCount() {
		return threads;
	}
}
