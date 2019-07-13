package lb.simplebase.net;

import java.util.concurrent.TimeoutException;

public class LocalNetworkConnection extends AbstractNetworkConnection{

	private LocalNetworkConnection partner = null;
	
	
	public LocalNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler) {
		super(source, target, packetHandler, ConnectionState.UNCONNECTED);
	}
	
	protected LocalNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, LocalNetworkConnection setPartner) {
		this(source, target, packetHandler);
		partner = setPartner;
		setConnectionState(ConnectionState.OPEN); //The connection is open when the partner exists
	}
	
	@Override
	public PacketSendFuture sendPacketToTarget(Packet packet) {
		if(getState() == ConnectionState.OPEN && partner != null) {
			return PacketSendFuture.create((f) -> {
				LocalConnectionManager.submitLocalPacketTask(() -> partner.handleReceivedPacket(packet));
				f.setPacketSent(true);
			}).run();
		} else {
			return PacketSendFuture.quickFailed("Connection is not open");
		}
	}

	@Override
	public ConnectionStateFuture connect(int timeout) {
		if(getState() == ConnectionState.UNCONNECTED) {
			return ConnectionStateFuture.create(getState(), (f) -> {
				try {
					partner = LocalConnectionManager.waitForLocalConnectionServer(this, timeout);
				} catch (TimeoutException e) {
					f.setErrorAndMessage(e, "The timeout expired before a local connection could be made");
					return;
				} catch (InterruptedException e) {
					f.setErrorAndMessage(e, "The thread was interrupted while waiting for the connection");
					return;
				}
				setConnectionState(ConnectionState.OPEN);
				f.setCurrentState(getState());
			}).run();
			
		} else {
			return ConnectionStateFuture.quickFailed("Connection is already " + (getState() == ConnectionState.CLOSED ? "closed" : "connected")
					+ " and cannot be connected again", getState());
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
	public ConnectionStateFuture close() {
		ConnectionStateFuture superFuture = super.close();
		if(partner != null) //If it was even connected
			partner.closeNoNotify(); //Close partner too, but he should not close his partner (this) to avoid infinite recursion
		return ConnectionStateFuture.quickDone(superFuture.getOldState(), getState());
	}
	
	/**
	 * Close connection without notifying Partner
	 */
	protected void closeNoNotify() {
		super.close(); //NOT this.close() !!!
	}
	
}