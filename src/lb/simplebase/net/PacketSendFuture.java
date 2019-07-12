package lb.simplebase.net;

import java.util.function.Consumer;

public class PacketSendFuture extends FailableFutureState{

	protected PacketSendFuture(boolean failed, Consumer<Accessor> action) {
		super(failed, (fs) -> action.accept(((Accessor) fs)));
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
	protected synchronized PacketSendFuture run() {
		return (PacketSendFuture) super.run();
	}

	protected static PacketSendFuture quickFailed(String reason) {
		return new PacketSendFuture(reason);
	}
	
	protected static PacketSendFuture quickDone() {
		return (PacketSendFuture) new PacketSendFuture(false, (f) -> f.setPacketSent(true)).runInSync();
	}
	
	protected static PacketSendFuture create(Consumer<Accessor> task) {
		return new PacketSendFuture(false, task);
	}

	public class Accessor extends FailableAccessor {
		
		public void setPacketSent(boolean sent) {
			wasSent = sent;
		}
		
	}
	
	@Override
	protected Object getAccessor() {
		return new Accessor();
	}

	@Override
	public boolean isSuccess() {
		return wasSent;
	}

	@Override
	public boolean isFailed() {
		return super.isFailed() || !wasSent;
	}
}
