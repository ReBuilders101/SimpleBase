package lb.simplebase.net;

import lb.simplebase.net.done.AbstractNetworkConnection;

public class LocalNetworkConnection extends AbstractNetworkConnection{

	private LocalNetworkConnection partner = null;
	
	public LocalNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler) {
		super(source, target, packetHandler, ConnectionState.UNCONNECTED);
	}
	
	protected LocalNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, boolean connect) 
			throws ConnectionStateException {
		this(source, target, packetHandler);
		if(connect) connect();
	}
	
	protected LocalNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, LocalNetworkConnection setPartner) {
		this(source, target, packetHandler);
		partner = setPartner;
		setConnectionState(ConnectionState.OPEN); //The connection is open when the partner exists
	}
	
	@Override
	public void sendPacketToTarget(Packet packet) throws ConnectionStateException {
		if(getState() == ConnectionState.OPEN) {
			partner.handleReceivedPacket(packet); //Partner handles packet
		} else {
			throw new ConnectionStateException("The NetworkConnection was not open when a Packet was supposed to be sent", this, ConnectionState.OPEN);
		}
	}

	@Override
	public void connect(int timeout) throws ConnectionStateException {
		if(getState() == ConnectionState.UNCONNECTED) {
			partner = LocalServerManager.waitForLocalConnectionServer(this, timeout);
			setConnectionState(ConnectionState.OPEN);
		} else {
			throw new ConnectionStateException("The NetworkConnection was already connected", this, ConnectionState.UNCONNECTED);
		}
	}

	protected AbstractNetworkConnection getPartner() {
		return partner;
	}

	@Override
	public boolean isLocalConnection() {
		return true;
	}

	@Override
	public void close() {
		super.close();
		if(partner != null) //If it was even connected
			partner.closeNoNotify(); //Close partner too, but he should not close his partner (this) to avoid infinite recursion
	}
	
	/**
	 * Close connection without notifying Partner
	 */
	protected void closeNoNotify() {
		super.close();
	}
	
}