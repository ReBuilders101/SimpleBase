package lb.simplebase.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

	public boolean wasCanceled() throws InterruptedException {
		waitForHandlers();
		return currentObject.isCanceled();
	}
	
	public boolean wasCanceled(long timeout, TimeUnit unit) throws InterruptedException {
		waitForHandlers(timeout, unit);
		return currentObject.isCanceled();
	}
	
	public Event getCurrentEvent() {
		return currentObject;
	}
	
	public Event getHandledEvent() throws InterruptedException {
		waitForHandlers();
		return currentObject;
	}
	
	public Event getHandledEvent(long timeout, TimeUnit unit) throws InterruptedException {
		waitForHandlers(timeout, unit);
		return currentObject;
	}
	
	public void waitForHandlers() throws InterruptedException{
		if(isHandlingCompleted()) return;
		waiter.await();//Wait for completion
	}
	
	public void waitForHandlers(long timeout, TimeUnit unit) throws InterruptedException{
		if(isHandlingCompleted()) return;
		waiter.await(timeout, unit); //Wait for completion
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
