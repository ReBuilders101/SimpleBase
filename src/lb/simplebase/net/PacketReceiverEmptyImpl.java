package lb.simplebase.net;

/**
 * You shouldn't even be able to see this
 * @see PacketReceiver#createEmptyReceiver()
 */
final class PacketReceiverEmptyImpl implements PacketReceiver{
	@Override
	public void processPacket(Packet received, TargetIdentifier source) {}
}
