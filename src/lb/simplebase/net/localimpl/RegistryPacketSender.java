package lb.simplebase.net.localimpl;

import lb.simplebase.net.Packet;
import lb.simplebase.net.PacketSender;
import lb.simplebase.net.TargetIdentifier;

/**
 * Invisible to you.
 * Implementation of {@link PacketSender} that uses a {@link LocalNetworkRegistry} to send packets
 */
class RegistryPacketSender implements PacketSender{

	private TargetIdentifier id;
	private LocalNetworkRegistry registry;
	
	protected RegistryPacketSender(TargetIdentifier id, LocalNetworkRegistry registry) {
		this.id = id;
		this.registry = registry;
	}
	
	@Override
	public void sendPacketTo(Packet packet, TargetIdentifier target) {
		registry.sendPacketTo(packet, target, this);
	}

	@Override
	public TargetIdentifier getSenderID() {
		return id;
	}
	
}
