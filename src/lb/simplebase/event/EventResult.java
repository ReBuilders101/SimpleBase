package lb.simplebase.event;

import lb.simplebase.action.ResultAction;

/**
 * An EventResult stores information about an event after it has been posted.
 * This includes the event instance, whether it was successfully posted, whether all handlers have completed,
 * and methods to wait for all handlers to complete.
 * <p>
 * An EventResult is created whenever an event is posted and is retuned by the {@link EventBus#post(Event)} method
 */
public class EventResult implements ResultAction {
	
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
	public boolean isCanceled() {
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
	
	@SuppressWarnings("unchecked")
	public <T extends Event> T getEvent(Class<T> type) {
		return (T) currentObject;
	}
	
	public EventBus getEventBus() {
		return handlingBus;
	}
	
	@Override
	public boolean isFailed() {
		return !posted;
	}

	@Override
	public boolean isSuccess() {
		return posted;
	}
	
	protected static EventResult createSynchronous(final Event event, final EventBus bus) {
		if(!bus.isSynchronous()) return null;
		return new EventResult(true, event, bus);
	}
	
	protected static EventResult createFailed(final Event event, final EventBus bus) {
		return new EventResult(false, event, bus);
	}
}
