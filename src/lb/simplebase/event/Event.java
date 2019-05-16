package lb.simplebase.event;

/**
 * An event object is a data container that can be posted to an {@link EventBus}
 * and can be received and processed by handlers.<br>
 * Every event type should be a subclass of this class and may add additional methods or fields
 * that can be used by the event handlers.
 */
public abstract class Event {
	
	private boolean isCanceled;
	private final boolean canCancel;
	
	/**
	 * Creates a new Event.
	 * @param canCancel Whether this event will be canceled when {@link #tryCancel()} is called
	 */
	protected Event(boolean canCancel) {
		this.canCancel = canCancel;
		this.isCanceled = false;
	}
	
	/**
	 * Cancels this event if {@link #canCancel()} is <code>true</code>.
	 * @return Whether the event was canceled successfully
	 */
	public boolean tryCancel() {
		isCanceled = canCancel;	//If you can cancel, isCancelled will be true now, otherwise it will be false
		return canCancel;	//If you can cancel, this will be successful
	}
	
	/**
	 * Whether this event has been canceled. A canceled event
	 * will only be passed to event handlers that have explicitly requested to receive canceled events.
	 * @return Whether this event has been canceled.
	 */
	public boolean isCanceled() {
		return isCanceled;
	}
	
	/**
	 * Whether this event can be canceled. If <code>false</code>, 
	 * all calls to {@link #tryCancel()} will return <code>false</code>
	 * and the event will not be canceled.
	 * @return Whether this event can be canceled
	 */
	public boolean canCancel() {
		return canCancel;
	}
	
}
