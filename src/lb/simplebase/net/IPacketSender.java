package lb.simplebase.net;

public interface IPacketSender {
	
	public void sendPacketTo(IPacket packet, ITargetIdentifier id);
	
}
