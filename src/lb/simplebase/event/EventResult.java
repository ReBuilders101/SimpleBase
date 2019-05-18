package lb.simplebase.event;

import java.util.concurrent.CountDownLatch;

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
	
	public Event getCurrentEvent() {
		return currentObject;
	}
	
	public Event getHandledEvent() throws InterruptedException {
		waitForHandlers();
		return currentObject;
	}
	
	public void waitForHandlers() throws InterruptedException{
		if(isHandlingCompleted()) return;
		waiter.await();//Wait for completion
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
	
}
