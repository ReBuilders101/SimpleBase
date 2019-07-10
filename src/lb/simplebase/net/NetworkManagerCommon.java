package lb.simplebase.net;

public interface NetworkManagerCommon extends PacketIdMappingContainer{
	
	public void addIncomingPacketHandler(PacketReceiver handler);
	public TargetIdentifier getLocalID();
	
}
