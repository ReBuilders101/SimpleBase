package lb.simplebase.net;

@FunctionalInterface
public interface TypedPacketReceiver<T extends Packet> extends PacketReceiver {

	public void processPacketTyped(T packet, PacketContext context);
	
	@Override
	@SuppressWarnings("unchecked")
	public default void processPacket(Packet received, PacketContext source) {
		processPacketTyped((T) received, source);
	}
	
}
