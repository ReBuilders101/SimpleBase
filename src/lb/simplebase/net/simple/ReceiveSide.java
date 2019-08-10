package lb.simplebase.net.simple;

import lb.simplebase.net.Packet;
import lb.simplebase.net.TargetIdentifier;

abstract class ReceiveSide {

	protected final void receive0(Packet packet, TargetIdentifier source) {
		if(packet instanceof StringMessagePacket) {
			final StringMessagePacket smp = (StringMessagePacket) packet;
			receive(smp.getObject());
		} else {
			receiveUnknown(packet);
		}
	}
	
	protected abstract void receive(String message);
	protected void receiveUnknown(Packet packet) { System.err.println("Received unknown packet type"); } //Optional override, for other packet types
	
	
}
