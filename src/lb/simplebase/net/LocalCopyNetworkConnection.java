package lb.simplebase.net;

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
			final ByteBuffer data = new ByteBuffer(); //Data storage for copying
			packet.writeData(data);
			final Packet packet2 = getPacketFactory().createNewInstance(packet.getClass()); //New instance
			packet2.readData(data);
			getPartner().handleReceivedPacket(packet2); //Local partner handles copied packet
		} else {
			throw new ConnectionStateException("The NetworkConnection was not open when a Packet was supposed to be sent", this, ConnectionState.OPEN);
		}
	}
	
}