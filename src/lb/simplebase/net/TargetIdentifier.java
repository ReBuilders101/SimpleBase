package lb.simplebase.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
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
	public <T> Optional<T> connectSocket(Supplier<SocketActions<T>> socket, int timeout) throws IOException, SocketTimeoutException, SocketException;
	public <T> Optional<T> bindSocket(Supplier<SocketActions<T>> socket) throws IOException, SocketException;
	
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
		return createLocal(name, TargetIdentifier::indexedName);
	}
	
	public static TargetIdentifier createLocal(String name, Function<String, IntFunction<String>> mapper) {
		return TargetIdentifierNameCache.createImpl(LocalTargetIdentifier::new, name, mapper.apply(name));
	}
	
	public static TargetIdentifier createNetwork(String name, String address, int port) {
		return createNetwork(name, address, port, TargetIdentifier::indexedName);
	}
	
	public static TargetIdentifier createNetwork(String name, String address, int port, Function<String, IntFunction<String>> mapper) {
		return TargetIdentifierNameCache.createImpl((name0) -> {
			try {
				return new NetworkTargetIdentifier(name0, address, port);
			} catch (UnknownHostException e) {
				NetworkManager.NET_LOG.error("Error while creating Network Target Identifier:", e);
				return null;
			}
		}, name, mapper.apply(name));
	}
	
	public static TargetIdentifier createNetwork(String name, InetAddress address, int port) {
		return createNetwork(name, address, port, TargetIdentifier::indexedName);
	}
	
	public static TargetIdentifier createNetwork(String name, InetAddress address, int port, Function<String, IntFunction<String>> mapper) {
		return TargetIdentifierNameCache.createImpl((name0) -> new NetworkTargetIdentifier(name0, address, port), name, mapper.apply(name));
	}
	
	public static TargetIdentifier createNetworkServer(String name, int port) {
		return createNetworkServer(name, port, TargetIdentifier::indexedName);
	}
	
	public static TargetIdentifier createNetworkServer(String name, int port, Function<String, IntFunction<String>> mapper) {
		return TargetIdentifierNameCache.createImpl((name0) -> {
			try {
				return new NetworkTargetIdentifier(name0, InetAddress.getLocalHost(), port);
			} catch (UnknownHostException e) {
				NetworkManager.NET_LOG.error("Error while creating Network Target Identifier:", e);
				return null;
			}
		}, name, mapper.apply(name));
	}
	
	public static TargetIdentifier createNetwork(String name, InetSocketAddress address) {
		return createNetwork(name, address, TargetIdentifier::indexedName);
	}
	
	public static TargetIdentifier createNetwork(String name, InetSocketAddress address, Function<String, IntFunction<String>> mapper) {
		return TargetIdentifierNameCache.createImpl((name0) -> new NetworkTargetIdentifier(name0, address), name, mapper.apply(name));
	}
	
	public static IntFunction<String> indexedName(String baseName) {
		return (i) -> baseName + i;
	}
	
	public static boolean isNameUsed(String name) {
		return TargetIdentifierNameCache.getCache().contains(name);
	}
	
	public static Object getNameCacheLock() {
		return TargetIdentifierNameCache.getLock();
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
