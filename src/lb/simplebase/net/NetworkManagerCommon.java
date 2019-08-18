package lb.simplebase.net;

public interface NetworkManagerCommon extends PacketIdMappingContainer{
	
	/**
	 * Adds a {@link PacketReceiver} that will be called when a packet is received by the network manager.
	 * @param handler The new {@link PacketReceiver}
	 */
	public void addIncomingPacketHandler(PacketReceiver handler);
	
	/**
	 * Adds a {@link PacketReceiver} that will be called when a packet is received by the network manager.
	 * @param handler The new {@link PacketReceiver}
	 */
	public default void addIncomingPacketHandler(GenericPacketReceiver<?> handler) {
		addIncomingPacketHandler(handler);
	}
	
	public TargetIdentifier getLocalID();
	
}
