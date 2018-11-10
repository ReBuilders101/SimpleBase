package lb.simplebase.net.localimpl;

import lb.simplebase.function.EventHandlerMap;
import lb.simplebase.net.IPacket;
import lb.simplebase.net.IPacketReceiver;
import lb.simplebase.net.IPacketSender;
import lb.simplebase.net.ITargetIdentifier;

public final class NetworkRegistry {

	private static EventHandlerMap<ITargetIdentifier, IPacketReceiver> receivers = new EventHandlerMap<>();
	
	private NetworkRegistry() {}
	
	public static void registerReceiver(ITargetIdentifier id, IPacketReceiver receiver) {
		receivers.addHandler(id, receiver);
	}
	
	public static void unregisterReceiver(ITargetIdentifier id, IPacketReceiver receiver) {
		receivers.removeHandler(id, receiver);
	}
	
	public static void unregisterReceiver(IPacketReceiver receiver) {
		receivers.values().forEach((c) -> c.remove(receiver));
	}
	
	public static void unregisterReceivers(ITargetIdentifier id) {
		receivers.removeAllHandlers(id);
	}
	
	public static IPacketSender createPacketSender(ITargetIdentifier id) {
		return new RegistryPacketSender();
	}

	protected static void sendPacketTo(IPacket packet, ITargetIdentifier id) {
		receivers.forEachHandler(id, (h) -> h.processPacket(packet, id));
	}
}
