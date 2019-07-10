package lb.simplebase.net;

public interface NetworkManagerCommon {
	
	public void addIncomingPacketHandler(PacketReceiver handler);
	public TargetIdentifier getLocalID();
	
}
