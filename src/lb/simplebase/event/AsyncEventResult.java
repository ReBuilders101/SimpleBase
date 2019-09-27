package lb.simplebase.event;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import lb.simplebase.action.AsyncAction.DoneHandler;
import lb.simplebase.action.ResultAction;

public class AsyncEventResult extends DoneHandler implements ResultAction {
 	
	private final boolean posted;
	private final Event event;
	private final AsyncEventBus bus;
	
	private final CountDownLatch completionWaiter;
	
	protected AsyncEventResult(boolean wasPosted, Event object, CountDownLatch waiter, AsyncEventBus handlingBus) {
		super(() -> new ArrayList<>());
		this.posted = wasPosted;
		this.event = object;
		this.bus = handlingBus;
		this.completionWaiter = waiter;
	}

	@Override
	public AsyncEventResult syncOrError() throws InterruptedException {
		if(posted) {
			completionWaiter.await();
		}
		return this;
	}

	@Override
	public boolean isDone() {
		return posted ? completionWaiter.getCount() == 0 : true;
	}
	
	@Override //Override to set type
	public AsyncEventResult sync() {
		return (AsyncEventResult) super.sync();
	}
	
	public boolean isCanceled() {
		return event.isCanceled();
	}
	
	public Event getEvent() {
		return event;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Event> T getEvent(Class<T> type) {
		return (T) event;
	}
	
	public EventBus getEventBus() {
		return bus;
	}
	
	public EventResult finishedResult() throws InterruptedException {
		syncOrError();
		return new EventResult(true, event, bus);
	}
	
	@Override
	public boolean isFailed() {
		return !posted;
	}

	@Override
	public boolean isSuccess() {
		return posted;
	}
	
	public static AsyncEventResult createFailed(Event event, AsyncEventBus bus) {
		return new AsyncEventResult(false, event, null, bus);
	}
	
	public static AsyncEventResult createAsync(Event object, CountDownLatch waiter, AsyncEventBus handlingBus) {
		return new AsyncEventResult(true, object, waiter, handlingBus);
	}
	
}
