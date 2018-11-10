package lb.simplebase.net.localimpl;

import lb.simplebase.net.IPacket;
import lb.simplebase.net.IPacketSender;
import lb.simplebase.net.ITargetIdentifier;

//Not public
class RegistryPacketSender implements IPacketSender{

	@Override
	public void sendPacketTo(IPacket packet, ITargetIdentifier id) {
		NetworkRegistry.sendPacketTo(packet, id);
	}
	
}
