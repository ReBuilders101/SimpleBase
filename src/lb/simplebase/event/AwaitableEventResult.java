package lb.simplebase.event;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import lb.simplebase.event.EventHandlerImpl.EventHandlerAwaitable;

@Deprecated
public class AwaitableEventResult extends EventResult{

	private EventHandlerAwaitable syncHandler;
	
	protected AwaitableEventResult(boolean wasPosted, Event object, CountDownLatch completionWaiter, EventHandlerAwaitable syncHandler, EventBus handlingBus) {
		super(wasPosted, object, completionWaiter, handlingBus);
		this.syncHandler = syncHandler;
	}

	private boolean completed;
	private boolean hadTurn;

	public boolean hasRun() {
		return completed;
	}
	
	public EventPriority getAwaitPriority() {
		if(!wasPostedSuccessfully()) return null;
		return syncHandler.getPriority() instanceof EventPriority ? (EventPriority) syncHandler.getPriority() : null;
	}
	
	public void awaitPriority() throws InterruptedException{
		if(cannotUseBarrier() || hadTurn) return;
		try {
			syncHandler.getWaiter().await();
		} catch (BrokenBarrierException e) {
			syncHandler.breakBarrier();
			completed = true;
			hadTurn = true;
			throw new InterruptedException("Barrier Broken: " + e.getMessage()); //throws exits method -> handling flag will not be set
		}
		//Here the main thread handler begins
		hadTurn = true;
		getEventBus().isHandlingEvents.set(true);
	}
	
	public boolean isCanceled() {
		return getCurrentEvent().isCanceled();
	}
	
	public void skipAll() throws InterruptedException {
		awaitPriority();
		allowCompletion();
	}
	
	public void allowCompletion() throws InterruptedException {
		if(cannotUseBarrier()) return;
		if(!hadTurn) return; //The turn must have been used by calling awaitPriority() before
		try {
			syncHandler.getWaiter().await();
		} catch (BrokenBarrierException e) {
			syncHandler.breakBarrier();
			throw new InterruptedException("Barrier Broken: " + e.getMessage());
		} finally {
			//Either handling is complete or broken. Reset flag and make this object unusable anyways
			completed = true;
			getEventBus().isHandlingEvents.set(false);
		}
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
		return hasRun() || !wasPostedSuccessfully() || syncHandler.isBroken();
	}
	
	public static AwaitableEventResult createAwaitable(Event object, CountDownLatch completionWaiter, EventHandlerAwaitable syncHandler, EventBus handlingBus) {
		return new AwaitableEventResult(true, object, completionWaiter, syncHandler, handlingBus);
	}
	
	public static AwaitableEventResult createFailed(Event object, EventBus handlingBus) {
		return new AwaitableEventResult(false, object, null, null, handlingBus);
	}

	@Override
	protected void finalize() throws Throwable {
		skipAll();	//Make sure that the handler thread may continue, for example if a method returns without allowCompletion() being called
		super.finalize();
	}
}
