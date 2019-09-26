package lb.simplebase.event;

/**
 * An EventResult stores information about an event after it has been posted.
 * This includes the event instance, whether it was successfully posted, whether all handlers have completed,
 * and methods to wait for all handlers to complete.
 * <p>
 * An EventResult is created whenever an event is posted and is retuned by the {@link EventBus#post(Event)} method
 */
public class EventResult {
	
	private final Event currentObject;
	private final EventBus handlingBus;
	private final boolean posted;

	
	protected EventResult(final boolean wasPosted, final Event object, final EventBus handlingBus) {
		this.handlingBus = handlingBus;
		this.currentObject = object;
		this.posted = wasPosted;
	}

	/**
	 * Waits for all handlers to complete and then checks whether the {@link Event#isCanceled()} flag has
	 * been set by any event handler.
	 * <p>
	 * Blocks the calling thread until all event handlers have completed, or until this thread is interrupted.
	 * @return If the event was canceled by any handlers
	 * @throws InterruptedException When the thread is interrupted while waiting for the handlers
	 */
	public boolean wasCanceled() {
		return currentObject.isCanceled();
	}
	
	/**
	 * The Event object that was posted and is described by this EventResult, in its current state.
	 * If the handlers run asynchronously, this instance can be altered by a handler at any time, as
	 * handlers may still be running when this method returns.
	 * @return The posted event instance
	 */
	public Event getEvent() {
		return currentObject;
	}
	
//	
//	public void waitForHandlers() throws InterruptedException{
//		if(getEventBus().isHandlingEvents.get()) throw new UnsupportedOperationException("Cannot wait for Handlers to complete while handler is still running on the same thread");
//		if(isHandlingCompleted()) return;
//		waiter.await();//Wait for completion
//	}
//	
//	public boolean waitForHandlers(long timeout, TimeUnit unit) throws InterruptedException{
//		if(getEventBus().isHandlingEvents.get()) throw new UnsupportedOperationException("Cannot wait for Handlers to complete while handler is still running on the same thread");
//		if(isHandlingCompleted()) return true;
//		return waiter.await(timeout, unit); //Wait for completion
//	}
//	
//	public boolean isHandlingCompleted() {
//		if(waiter == null) return true;
//		return waiter.getCount() == 0L;
//	}
	
	public EventBus getEventBus() {
		return handlingBus;
	}
	
	public boolean wasPostedSuccessfully() {
		return posted;
	}
	
	public boolean isHandledSynchronous() {
		return true;
	}
	
	public static EventResult createSynchronous(final Event event, final EventBus bus) {
		if(!bus.isSynchronous()) return null;
		return new EventResult(true, event, bus);
	}
	
	public static EventResult createFailed(final Event event, final EventBus bus) {
		return new EventResult(false, event, bus);
	}

	//Future methods
	
	
	
}
