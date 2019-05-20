package lb.simplebase.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An EventResult stores information about an event after it has been posted.
 * This includes the event instance, whether it was successfully posted, whether all handlers have completed,
 * and methods to wait for all handlers to complete.
 * <p>
 * An EventResult is created whenever an event is posted and is retuned by the {@link EventBus#post(Event)} method
 */
public class EventResult {

//	private final Future<Event> processedObject;
	private final CountDownLatch waiter;
	
	private final Event currentObject;
	private final EventBus handlingBus;
	private final boolean posted;

	
	protected EventResult(final boolean wasPosted, final Event object, final CountDownLatch waiter, final EventBus handlingBus) {
		this.waiter = waiter;
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
	public boolean wasCanceled() throws InterruptedException {
		waitForHandlers();
		return currentObject.isCanceled();
	}
	
	/**
	 * The Event object that was posted and is described by this EventResult, in its current state.
	 * If the handlers run asynchronously, this instance can be altered by a handler at any time, as
	 * handlers may still be running when this method returns.
	 * @return The posted event instance
	 */
	public Event getCurrentEvent() {
		return currentObject;
	}
	
	/**
	 * The event object that was posted and is described by this EventResult, after all handlers have run.
	 * If necessaray, this method will block the calling thread until all event handlers have completed,
	 * or until this thread is interrupted.
	 * @return The posted event instance after it was handled by all handlers
	 * @throws InterruptedException When the thread is interrupted while waiting for the handlers
	 */
	public Event getHandledEvent() throws InterruptedException {
		waitForHandlers();
		return currentObject;
	}
	
	public Event getHandledEvent(long timeout, TimeUnit unit) throws InterruptedException {
		return waitForHandlers(timeout, unit) ? currentObject : null; //Null if time is up
	}
	
	public void waitForHandlers() throws InterruptedException{
		if(getEventBus().isHandlingEvents.get()) throw new UnsupportedOperationException("Cannot wait for Handlers to complete while handler is still running on the same thread");
		if(isHandlingCompleted()) return;
		waiter.await();//Wait for completion
	}
	
	public boolean waitForHandlers(long timeout, TimeUnit unit) throws InterruptedException{
		if(getEventBus().isHandlingEvents.get()) throw new UnsupportedOperationException("Cannot wait for Handlers to complete while handler is still running on the same thread");
		if(isHandlingCompleted()) return true;
		return waiter.await(timeout, unit); //Wait for completion
	}
	
	public boolean isHandlingCompleted() {
		if(waiter == null) return true;
		return waiter.getCount() == 0L;
	}
	
	public EventBus getEventBus() {
		return handlingBus;
	}
	
	public boolean wasPostedSuccessfully() {
		return posted;
	}
	
	public boolean isHandledSynchronous() {
		return handlingBus.isSynchronous();
	}
	
	public Future<Event> asFuture() {
		return new Future<Event>() {

			@Override
			public boolean cancel(boolean var1) {
				return false;
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return isHandlingCompleted();
			}

			@Override
			public Event get() throws InterruptedException, ExecutionException {
				try {
					return getHandledEvent();
				} catch(InterruptedException e) {
					throw e;	//InterruptedException should not be wrapped in ExecutionException
				} catch(Throwable t) {
					throw new ExecutionException(t);
				}
			}

			@Override
			public Event get(long var1, TimeUnit var3)
					throws InterruptedException, ExecutionException, TimeoutException {
				try {
					return getHandledEvent(var1, var3);
				} catch(InterruptedException e) {
					throw e;	//InterruptedException should not be wrapped in ExecutionException
				} catch(Throwable t) {
					throw new ExecutionException(t);
				}
			}
			
		};
	}
	
	
	
	public static EventResult createSynchronous(final Event event, final EventBus bus) {
		if(!bus.isSynchronous()) return null;
		return new EventResult(true, event, null, bus);
	}
	
	public static EventResult createAsynchronous(final Event event, final EventBus bus, CountDownLatch completeWaiter) {
		if(bus.isSynchronous()) return null;
		return new EventResult(true, event, completeWaiter, bus);
	}
	
	public static EventResult createFailed(final Event event, final EventBus bus) {
		return new EventResult(false, event, null, bus);
	}

	//Future methods
	
	
	
}
