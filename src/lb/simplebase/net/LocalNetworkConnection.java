package lb.simplebase.net;

import java.util.concurrent.TimeoutException;

import lb.simplebase.net.ClosedConnectionEvent.Cause;

public class LocalNetworkConnection extends NetworkConnection{

	private LocalNetworkConnection partner = null;
	
	
	public LocalNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, boolean isServer, Object payload) {
		super(source, target, packetHandler, ConnectionState.UNCONNECTED, isServer, payload);
	}
	
	protected LocalNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, LocalNetworkConnection setPartner, boolean isServer, Object payload) {
		this(source, target, packetHandler, isServer, payload);
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

	protected NetworkConnection getPartner() {
		return partner;
	}

	@Override
	public boolean isLocalConnection() {
		return true;
	}

	@Override
	public ConnectionStateFuture close() {
		ConnectionStateFuture superFuture = super.close(); //Uses EXPECTED reason
		if(partner != null) //If it was even connected
			partner.closeNoNotify(); //Close partner too, but he should not close his partner (this) to avoid infinite recursion
		return ConnectionStateFuture.quickDone(superFuture.getOldState(), getState());
	}
	
	/**
	 * Close connection without notifying Partner
	 */
	protected void closeNoNotify() {
		super.closeWithReason(Cause.REMOTE); //NOT this.close() !!! //Closed by peer
	}
	
}