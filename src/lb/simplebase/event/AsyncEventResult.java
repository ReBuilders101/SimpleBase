package lb.simplebase.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import lb.simplebase.action.AsyncAction;
import lb.simplebase.action.AsyncAction.DoneHandler;

public class AsyncEventResult extends DoneHandler {
 	
	protected AsyncEventResult(boolean wasPosted, Event object, CountDownLatch waiter, EventBus handlingBus) {
		super(() -> new ArrayList<>());
	}

	@Override
	public AsyncAction syncOrError() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public EventResult finishedResult() throws InterruptedException {
		
	}
	
}
