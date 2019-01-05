package lb.simplebase.net;

public interface PacketIdMapping {
	
	public Packet getNewInstance();
	public int getPacketId();
	public Class<? extends Packet> getPacketClass();
	
}
