package lb.simplebase.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * All objects implementing this interface can be used to identify a network target.
 * Because {@link TargetIdentifier} is used as a key in {@link HashMap}s, all implementatioons should
 * implement the {@link Object#hashCode()} method.
 * @see NetworkTargetIdentifier
 * @see LocalTargetIdentifier
 *  */
public interface TargetIdentifier {
	
	/**
	 * An {@link InetSocketAddress} that con be used to open a {@link Socket}-based connection to this
	 * network target. If {@link #isLocalOnly()} returns <code>true</code>, this {@link TargetIdentifier} cannot be
	 * used for network traffic and may return <code>null</code>. Otherwise, a valid {@link InetAddress} must be returned.
	 * @return The connection address for a {@link Socket} connection
	 */
	public InetSocketAddress getConnectionAddress();
	
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
	
	public boolean matches(String address, int port);
	public boolean matches(InetAddress address, int port);
	public boolean matches(InetSocketAddress address);
	
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
	
	public static String nameOrAddress(TargetIdentifier ti) {
		if(ti instanceof LocalTargetIdentifier) {
			return ti.getId();
		} else {
			return ti.getId() + "@" + ti.getConnectionAddress();
		}
	}
}
