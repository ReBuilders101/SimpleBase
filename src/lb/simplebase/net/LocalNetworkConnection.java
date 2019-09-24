package lb.simplebase.net;

import java.util.concurrent.TimeoutException;

import lb.simplebase.action.AsyncResult;
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
	public AsyncResult sendPacketToTarget(Packet packet) {
		if(getState() == ConnectionState.OPEN && partner != null) {
			return AsyncNetTask.submitTask((f) -> {
				LocalConnectionManager.submitLocalPacketTask(() -> partner.handleReceivedPacket(packet));
			});
		} else {
			return AsyncNetTask.createFailed(null, "Connection is not open");
		}
	}

	@Override
	public void connect(int timeout) {
		if(getState() == ConnectionState.UNCONNECTED) {
				try {
					partner = LocalConnectionManager.waitForLocalConnectionServer(this, timeout);
				} catch (TimeoutException e) {
					NetworkManager.NET_LOG.warn("The timeout expired before a local connection could be made", e);
					return;
				} catch (InterruptedException e) {
					NetworkManager.NET_LOG.warn("The thread was interrupted while waiting for the connection", e);
					return;
				}
				setConnectionState(ConnectionState.OPEN);
			
		} else {
			NetworkManager.NET_LOG.warn("Connection is already " + (getState() == ConnectionState.CLOSED ? "closed" : "connected")
					+ " and cannot be connected again");
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
	public void close() {
		super.close(); //Uses EXPECTED reason
		if(partner != null) //If it was even connected
			partner.closeNoNotify(); //Close partner too, but he should not close his partner (this) to avoid infinite recursion
	}
	
	/**
	 * Close connection without notifying Partner
	 */
	protected void closeNoNotify() {
		super.closeWithReason(Cause.REMOTE); //NOT this.close() !!! //Closed by peer
	}
	
}