package lb.simplebase.event;

public abstract class Event {
	
	private boolean isCanceled;
	private final boolean canCancel;
	
	protected Event(boolean canCancel) {
		this.canCancel = canCancel;
		this.isCanceled = false;
	}
	
	public boolean tryCancel() {
		isCanceled = canCancel;	//If you can cancel, isCancelled will be true now, otherwise it will be false
		return canCancel;	//If you can cancel, this will be successful
	}
	
	public boolean isCancelled() {
		return isCanceled;
	}
	
	public boolean canCancel() {
		return canCancel;
	}
	
}
