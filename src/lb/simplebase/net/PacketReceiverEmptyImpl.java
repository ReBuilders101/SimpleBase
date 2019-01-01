package lb.simplebase.net;

final class PacketReceiverEmptyImpl implements PacketReceiver{
	@Override
	public void processUnhandledPacket(Packet received, TargetIdentifier source) {}
}
