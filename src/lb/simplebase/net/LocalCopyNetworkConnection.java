package lb.simplebase.net;

import lb.simplebase.io.ByteArrayBuffer;
import lb.simplebase.net.done.ConnectionState;
import lb.simplebase.net.done.NetworkManager;
import lb.simplebase.net.done.Packet;
import lb.simplebase.net.done.PacketIdMapping;
import lb.simplebase.net.done.PacketMappingNotFoundException;
import lb.simplebase.net.done.TargetIdentifier;

/**
 * @deprecated Only for testing
 */
@Deprecated class LocalCopyNetworkConnection extends LocalNetworkConnection {

	protected LocalCopyNetworkConnection(TargetIdentifier source, TargetIdentifier target,
			NetworkManager packetHandler, boolean connect)
			throws ConnectionStateException {
		super(source, target, packetHandler, connect);
	}

	protected LocalCopyNetworkConnection(TargetIdentifier source, TargetIdentifier target,
			NetworkManager packetHandler) {
		super(source, target, packetHandler);
	}
	
	@Override
	public void sendPacketToTarget(Packet packet) throws ConnectionStateException {
		if(getState() == ConnectionState.OPEN){
			final ByteArrayBuffer data = new ByteArrayBuffer(); //Data storage for copying
			packet.writeData(data);
			final PacketIdMapping mapping = getPacketFactory().getMappingContainer().getMappingFor(packet.getClass()); //get Mapping to create instance
			if(mapping == null)
				throw new ConnectionStateException("No packet id mapping was found to create a new packet instance",
						new PacketMappingNotFoundException("Mapping not found for this Packet", packet), this, ConnectionState.OPEN);
			final Packet packet2 = mapping.getNewInstance(); //New instance
			packet2.readData(data);
			getPartner().handleReceivedPacket(packet2); //Local partner handles copied packet
		} else {
			throw new ConnectionStateException("The NetworkConnection was not open when a Packet was supposed to be sent", this, ConnectionState.OPEN);
		}
	}
	
}