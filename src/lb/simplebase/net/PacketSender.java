package lb.simplebase.net;

public interface PacketSender {
	
	public void sendPacketTo(Packet packet, TargetIdentifier id);
	public TargetIdentifier getSenderID();
	
}
