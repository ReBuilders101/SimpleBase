package lb.simplebase.net.localimpl;

import lb.simplebase.net.IPacket;
import lb.simplebase.net.IPacketSender;
import lb.simplebase.net.ITargetIdentifier;

//Not public
class RegistryPacketSender implements IPacketSender{

	private ITargetIdentifier id;
	
	protected RegistryPacketSender(ITargetIdentifier id) {
		this.id = id;
	}
	
	@Override
	public void sendPacketTo(IPacket packet, ITargetIdentifier target) {
		NetworkRegistry.sendPacketTo(packet, target, id);
	}
	
}
