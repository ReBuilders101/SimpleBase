package lb.simplebase.net;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A simple filter that can be used as a listener for the {@link AttemptedConnectionEvent} to prevent certain ips, represented by {@link InetAddress}es,
 * form making a connection to the server. 
 */
public class BannedIpFilter implements Consumer<AttemptedConnectionEvent>, Iterable<InetAddress>{

	private final Set<InetAddress> bannedIps;
	
	/**
	 * Creates a new {@link BannedIpFilter} with a list of banned addresses.
	 * @param bannedIps A list of {@link InetAddress}es that should be in the filter
	 */
	public BannedIpFilter(Iterable<InetAddress> bannedIps) {
		this.bannedIps = new HashSet<>();
		for(InetAddress address : bannedIps) {
			this.bannedIps.add(address);
		}
	}
	
	/**
	 * Creates a new {@link BannedIpFilter} with an empty filter.
	 */
	public BannedIpFilter() {
		this.bannedIps = new HashSet<>();
	}
	
	/**
	 * Adds a new address to the banned ip filter.
	 * @param address The new {@link InetAddress}
	 */
	public void addBannedIp(InetAddress address) {
		bannedIps.add(address);
	}
	
	/**
	 * Removes an existing ip address from the filter.<br>
	 * Finding a matching address in the filter is done through the {@link InetAddress#equals(Object)} method,
	 * it behaves like the {@link Collection#remove(Object)} method.
	 * @param address The address to remove
	 */
	public void removeBannedIp(InetAddress address) {
		bannedIps.remove(address);
	}
	
	/**
	 * Creates an iterator over all {@link InetAddress}es on the filter.
	 * @return The iterator of the backing set
	 */
	@Override
	public Iterator<InetAddress> iterator() {
		return bannedIps.iterator();
	}

	@Override
	public void accept(AttemptedConnectionEvent var1) {
		if(bannedIps.contains(var1.getRemoteConnectionAddress())) var1.tryCancel();
	}

}
