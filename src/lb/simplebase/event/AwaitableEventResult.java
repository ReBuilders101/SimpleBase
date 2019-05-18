package lb.simplebase.event;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import lb.simplebase.event.EventHandlerImpl.EventHandlerAwaitable;

public class AwaitableEventResult extends EventResult{

	private EventHandlerAwaitable syncHandler;
	
	protected AwaitableEventResult(boolean wasPosted, Event object, CountDownLatch completionWaiter, EventHandlerAwaitable syncHandler, EventBus handlingBus) {
		super(wasPosted, object, completionWaiter, handlingBus);
		this.syncHandler = syncHandler;
	}

	private boolean completed;
	

	public boolean hasRun() {
		return completed;
	}
	
	public EventPriority getAwaitPriority() {
		if(!wasPostedSuccessfully()) return null;
		return syncHandler.getPriority() instanceof EventPriority ? (EventPriority) syncHandler.getPriority() : null;
	}
	
	public void awaitPriority() throws InterruptedException{
		if(cannotUseBarrier()) return;
		try {
			syncHandler.getWaiter().await();
		} catch (BrokenBarrierException e) {
			syncHandler.breakBarrier();
			throw new InterruptedException("Barrier Broken: " + e.getMessage());
		}
	}
	
	public boolean isCanceled() {
		return getCurrentEvent().isCanceled();
	}
	
	public void allowCompletion() throws InterruptedException {
		if(cannotUseBarrier()) return;
		try {
			syncHandler.getWaiter().await();
		} catch (BrokenBarrierException e) {
			syncHandler.breakBarrier();
			throw new InterruptedException("Barrier Broken: " + e.getMessage());
		}
		completed = true;
	}
	
	/**
	 * Can be used like this:<br><code>if(result.isCanceled) return allowCompletion(returnValue);</code>
	 * @param t
	 * @return
	 * @throws InterruptedException 
	 */
	public <T> T allowCompletion(T t) throws InterruptedException {
		allowCompletion();
		return t;
	}
	
	/**
	 * Can be used like this:<br><code>if(result.isCanceled) return allowCompletion(returnValue, null, false);</code>
	 * <br><code>if(result.isCanceled) return allowCompletion(returnValue, exceptionalReturnValue, true);</code>
	 * @param t
	 * @param ex
	 * @param different
	 * @return
	 */
	public <T> T allowCompletion(T t, T ex, boolean different) {
		try {
			allowCompletion();
		} catch (InterruptedException e) {
			if(different) return ex;
		}
		return t;
	}
	
	private boolean cannotUseBarrier() {
		return hasRun() || !wasPostedSuccessfully();
	}
	
	public static AwaitableEventResult createAwaitable(Event object, CountDownLatch completionWaiter, EventHandlerAwaitable syncHandler, EventBus handlingBus) {
		return new AwaitableEventResult(true, object, completionWaiter, syncHandler, handlingBus);
	}
	
	public static AwaitableEventResult createFailed(Event object, EventBus handlingBus) {
		return new AwaitableEventResult(false, object, null, null, handlingBus);
	}
}
