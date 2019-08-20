package lb.simplebase.net;

import java.net.Socket;
import java.net.SocketException;

//just prevents code duplication
public class SocketConfiguration {

	protected Socket socket;
	
	protected SocketConfiguration(Socket socket) {
		this.socket = socket;
	}
	
	/**
	 * @see Socket#setKeepAlive(boolean)
	 */
	public boolean setKeepAlive(boolean value) {
		if(socket == null) return false;
		try {
			socket.setKeepAlive(value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'keepAlive' to " + value, e);
			return false;
		}
	}
	
	/**
	 * @see Socket#setReuseAddress(boolean)
	 */
	public boolean setReuseAddress(boolean value) {
		if(socket == null) return false;
		try {
			socket.setReuseAddress(value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'reuseAddress' to " + value, e);
			return false;
		}
	}
	
	/**
	 * @see Socket#setOOBInline(boolean)
	 */
	public boolean setOOBInline(boolean value) {
		if(socket == null) return false;
		try {
			socket.setOOBInline(value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'OOBInline' to " + value, e);
			return false;
		}
	}
	
	/**
	 * @see Socket#setReceiveBufferSize(int)
	 */
	public boolean setReceiveBufferSize(int value) {
		if(socket == null) return false;
		try {
			socket.setReceiveBufferSize(value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'receiveBufferSize' to " + value, e);
			return false;
		} catch (IllegalArgumentException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'receiveBufferSize' to invalid value " + value, e);
			return false;
		}
	}
	
	/**
	 * @see Socket#setSendBufferSize(int)
	 */
	public boolean setSendBufferSize(int value) {
		if(socket == null) return false;
		try {
			socket.setSendBufferSize(value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'sendBufferSize' to " + value, e);
			return false;
		} catch (IllegalArgumentException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'sendBufferSize' to invalid value " + value, e);
			return false;
		}
	}
	
	/**
	 * @see Socket#setSoLinger(boolean, int)
	 */
	public boolean setSoLinger(boolean enabled, int value) {
		if(socket == null) return false;
		try {
			socket.setSoLinger(enabled, value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'SoLinger' to " + value, e);
			return false;
		} catch (IllegalArgumentException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'SoLinger' to invalid value " + value, e);
			return false;
		}
	}
	
	/**
	 * @see Socket#setSoTimeout(int)
	 */
	public boolean setSoTimeout(int value) {
		if(socket == null) return false;
		try {
			socket.setSoTimeout(value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'SoTimeout' to " + value, e);
			return false;
		}
	}
	
	/**
	 * @see Socket#setTcpNoDelay(boolean)
	 */
	public boolean setTcpNoDelay(boolean value) {
		if(socket == null) return false;
		try {
			socket.setTcpNoDelay(value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'TcpNoDelay' to " + value, e);
			return false;
		}
	}
	
	/**
	 * @see Socket#setTrafficClass(int)
	 */
	public boolean setTrafficClass(int value) {
		if(socket == null) return false;
		try {
			socket.setTrafficClass(value);
			return true;
		} catch (SocketException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'trafficClass' to " + value, e);
			return false;
		} catch (IllegalArgumentException e) {
			NetworkManager.NET_LOG.warn("Could not set socket option 'trafficClass' to invalid value " + value, e);
			return false;
		}
	}
	
}
