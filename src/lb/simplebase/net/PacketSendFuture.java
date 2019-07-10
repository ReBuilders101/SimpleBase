package lb.simplebase.net;

import java.util.function.Consumer;

public class PacketSendFuture extends FailableFutureState{

	protected PacketSendFuture(boolean failed, Consumer<PacketSendFuture> action) {
		super(failed, (fs) -> action.accept(((PacketSendFuture) fs)));
		wasSent = false;
	}
	
	protected PacketSendFuture(String message) {
		super(true, null, message, null);
		this.errorMessage = message;
		wasSent = false;
	}

	protected volatile boolean wasSent;
	
	public boolean wasPacketSentYet() {
		return wasSent;
	}
	
	public boolean ensurePacketSent() throws InterruptedException{
		sync();
		return wasPacketSentYet();
	}
	
	@Override
	public synchronized PacketSendFuture run() {
		return (PacketSendFuture) super.run();
	}

	public static PacketSendFuture quickFailed(String reason) {
		return new PacketSendFuture(reason);
	}
	
	public static PacketSendFuture quickDone() {
		return (PacketSendFuture) new PacketSendFuture(false, (f) -> f.wasSent = true).runInSync();
	}
	
	public static PacketSendFuture create(Consumer<PacketSendFuture> task) {
		return new PacketSendFuture(false, task);
	}
}
