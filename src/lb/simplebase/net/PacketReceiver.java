package lb.simplebase.net;

public interface PacketReceiver {
	
	public default void processPacket(Packet received, TargetIdentifier source) {
		processUnhandledPacket(received, source);
	}
	
	public void processUnhandledPacket(Packet received, TargetIdentifier source);
	
	public static PacketReceiver createEmptyReceiver() {
		return new PacketReceiverEmptyImpl();
	}
}
