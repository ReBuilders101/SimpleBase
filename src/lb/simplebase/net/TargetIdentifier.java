package lb.simplebase.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * All objects implementing this interface can be used to identify a network target.
 * Because {@link TargetIdentifier} is used as a key in {@link HashMap}s, all implementatioons should
 * implement the {@link Object#hashCode()} method.
 * @see NetworkTargetIdentifier
 * @see LocalTargetIdentifier
 *  */
public interface TargetIdentifier {
	
	/**
	 * Tries to connect a socket to this target.
	 * If this target does not support network connections ({@link #isLocalOnly()}),
	 * an empty {@link Optional} is returned and the supplier will not be called.
	 * If the target supports network connections, it will be connected to this target.
	 * If the socket is already connected, a {@link SocketException} is thrown
	 * When this method returns with an exception, the connection state of the socket is undefined.
	 * @param socket The socket that should be connected
	 * @return The connected socket, if successful
	 */
	public <T extends Socket> Optional<T> connectSocket(Supplier<T> socket, int timeout) throws IOException, SocketTimeoutException, SocketException;
	public <T extends DatagramSocket> Optional<T> connectDatagram(Supplier<T> socket, int timeout) throws IOException, SocketTimeoutException, SocketException;
	public <T extends ServerSocket> Optional<T> bindSocket(Supplier<T> socket) throws IOException, SocketException;
	public <T extends DatagramSocket> Optional<T> bindDatagram(Supplier<T> socket) throws IOException, SocketException;
	
	public String createConnectionInformation(boolean name, boolean ipData);
	
	/**
	 * Every {@link TargetIdentifier} holds a Sring ID that should be unique for every target in the program.
	 * @return The id of this {@link TargetIdentifier}
	 */
	public String getId();
	
	/**
	 * If <code>true</code>, this {@link TargetIdentifier} can only be used for network-style communication
	 * inside one program, but not to create a network connection.<br>
	 * If <code>true</code>, {@link #getConnectionAddress()} may return null or an invalid address.
	 * @return If this is a local target
	 */
	public boolean isLocalOnly();
	
	
	public static TargetIdentifier createLocal(String name) {
		return new LocalTargetIdentifier(name);
	}
	
	public static TargetIdentifier createNetwork(String name, String address, int port) {
		try {
			return new NetworkTargetIdentifier(name, address, port);
		} catch (UnknownHostException e) {
			NetworkManager.NET_LOG.error("Error while creating Network Target Identifier:", e);
			return null;
		}
	}
	
	public static TargetIdentifier createNetwork(String name, InetAddress address, int port) {
		return new NetworkTargetIdentifier(name, address, port);
	}
	
	public static TargetIdentifier createNetworkServer(String name, int port) {
		try {
			return new NetworkTargetIdentifier(name, InetAddress.getLocalHost(), port);
		} catch (UnknownHostException e) {
			NetworkManager.NET_LOG.error("Error while creating Network Target Identifier:", e);
			return null;
		}
	}
	
	public static TargetIdentifier createNetwork(String name, InetSocketAddress address) {
		return new NetworkTargetIdentifier(name, address);
	}
	
	@Deprecated
	public static Optional<InetSocketAddress> tryGetAddress(TargetIdentifier ti) {
		if(ti instanceof NetworkTargetIdentifier) {
			return Optional.of(((NetworkTargetIdentifier) ti).getAddress());
		} else {
			return Optional.empty();
		}
	}
	
	@Deprecated
	public static String nameOrAddress(TargetIdentifier ti) {
		return ti.createConnectionInformation(true, true);
	}
}
