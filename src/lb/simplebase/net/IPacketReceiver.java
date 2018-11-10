package lb.simplebase.net;

public interface IPacketReceiver {
	
	public void processPacket(IPacket received, ITargetIdentifier source);
	
}
