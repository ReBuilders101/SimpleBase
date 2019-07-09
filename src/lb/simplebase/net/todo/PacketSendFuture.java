package lb.simplebase.net.todo;

import lb.simplebase.net.FailableFutureState;
import lb.simplebase.net.NetworkConnection;
import lb.simplebase.net.Packet;

public class PacketSendFuture extends FailableFutureState{

	protected PacketSendFuture(boolean failed) {
		super(failed, (psf) -> ((PacketSendFuture) psf).wasSent = true);
		wasSent = false;
	}

	private boolean wasSent;
	private State currentState;
	
	
	public synchronized boolean wasPacketSentYet() {
		return wasSent;
	}
	
	
	public static PacketSendFuture quickFailed(String reason) {
		PacketSendFuture r = new PacketSendFuture(true);
		r.errorMessage = reason;
		return r;
	}
	
	public static PacketSendFuture quickFailed(Throwable reason) {
		PacketSendFuture r = new PacketSendFuture(true);
		r.errorMessage = reason.getMessage();
		r.ex = reason;
		return r;
	}
	
	public static PacketSendFuture forPacket(Packet data, NetworkConnection con) {
		if(data == null || con == null) return new PacketSendFuture(true);
		return new PacketSendFuture(false);
	}
	
	public static enum State {
		FAILED, WORKING, SUCCESS;
	}
}
