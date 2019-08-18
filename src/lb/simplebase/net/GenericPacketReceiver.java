package lb.simplebase.net;

@FunctionalInterface
public interface GenericPacketReceiver<T> extends PacketReceiver {

	/**
	 * Implementations should handle the received packet, possibly depending on the source of the packet
	 * @param received The packet that should be processed by this {@link PacketReceiver}
	 * @param source The source that sent the packet
	 */
	public void processPacket(Packet received, GenericPacketContext<T> source);
	
	
	@SuppressWarnings("unchecked")
	@Override
	public default void processPacket(Packet received, PacketContext source) {
		try {
			processPacket(received, (GenericPacketContext<T>) source);
		} catch (ClassCastException e) {
			NetworkManager.NET_LOG.error("Generic Type reqired by GenericPacketReceiver did not match the custom object type", e);
		}
	}
	
}
