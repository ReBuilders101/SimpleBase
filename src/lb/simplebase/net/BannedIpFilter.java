package lb.simplebase.net;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiPredicate;

public class BannedIpFilter implements BiPredicate<NetworkManagerServer, ConnectionInformation>, Iterable<InetAddress>{

	private final Set<InetAddress> bannedIps;
	
	public BannedIpFilter(Iterable<InetAddress> bannedIps) {
		this.bannedIps = new HashSet<>();
		for(InetAddress address : bannedIps) {
			this.bannedIps.add(address);
		}
	}
	
	public void addBannedIp(InetAddress address) {
		bannedIps.add(address);
	}
	
	public void removeBannedIp(InetAddress address) {
		bannedIps.remove(address);
	}
	
	@Override
	public Iterator<InetAddress> iterator() {
		return bannedIps.iterator();
	}

	@Override
	public boolean test(NetworkManagerServer var1, ConnectionInformation var2) {
		return !bannedIps.contains(var2.getAddress());
	}

}
