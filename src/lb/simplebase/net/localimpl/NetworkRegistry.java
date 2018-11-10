package lb.simplebase.net.localimpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lb.simplebase.net.IPacket;
import lb.simplebase.net.IPacketReceiver;
import lb.simplebase.net.IPacketSender;
import lb.simplebase.net.ITargetIdentifier;

public final class NetworkRegistry {

	private static Map<ITargetIdentifier, Set<IPacketReceiver>> receivers = new HashMap<>();
	
	private NetworkRegistry() {}
	
	public static void registerReceiver(ITargetIdentifier id, IPacketReceiver receiver) {
		if(id == null || receiver == null) return;
		Set<IPacketReceiver> recs = receivers.get(id);
		if(recs == null) {
			recs = new HashSet<>();
			receivers.put(id, recs);
		}
		recs.add(receiver);
	}
	
	public static void unregisterReceiver(ITargetIdentifier id, IPacketReceiver receiver) {
		if(id == null || receiver == null) return;
		Set<IPacketReceiver> recs = receivers.get(id);
		if(recs == null) return;
		recs.remove(receiver);
	}
	
	public static void unregisterReceiver(IPacketReceiver receiver) {
		receivers.values().forEach((c) -> c.remove(receiver));
	}
	
	public static void unregisterReceivers(ITargetIdentifier id) {
		receivers.remove(id);
	}
	
	public static IPacketSender createPacketSender(ITargetIdentifier id) {
		return new RegistryPacketSender();
	}

	protected static void sendPacketTo(IPacket packet, ITargetIdentifier id) {
		Set<IPacketReceiver> recs = receivers.get(id);
		if(recs == null) return;
		recs.forEach((r) -> r.processPacket(packet, id));
	}
}
