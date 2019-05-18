package lb.simplebase.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

public class AwaitableEventResult extends EventResult{

	protected AwaitableEventResult(boolean wasPosted, Event object, CountDownLatch waiter, EventBus handlingBus) {
		super(wasPosted, object, waiter, handlingBus);
	}


	private boolean completed;
	

	public boolean hasRun() {
		return completed;
	}
	
	public void awaitPriority() throws InterruptedException{
		if(hasRun()) return;
		
	}
	
	
	public void allowCompletion() {
		if(hasRun()) return;
		
	}
}
