package lb.simplebase.net;

import lb.simplebase.event.EventBusRegistry;

public interface NetworkManagerCommon extends PacketIdMappingContainer{
	
	/**
	 * Adds a {@link PacketReceiver} that will be called when a packet is received by the network manager.
	 * @param handler The new {@link PacketReceiver}
	 */
	public void addIncomingPacketHandler(PacketReceiver handler);
	
	public TargetIdentifier getLocalID();
	
	public EventBusRegistry getEventBus();
}
