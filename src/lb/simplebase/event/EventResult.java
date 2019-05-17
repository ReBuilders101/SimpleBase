package lb.simplebase.event;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lb.simplebase.util.DelegateFuture;

public class EventResult {

	private final Future<Event> processedObject;
	private final Event currentObject;
	private final EventBus handlingBus;
	private final boolean posted;

	
	protected EventResult(final boolean wasPosted, final Event object, final Future<Event> processedObject, final EventBus handlingBus) {
		this.processedObject = processedObject;
		this.handlingBus = handlingBus;
		this.currentObject = object;
		this.posted = wasPosted;
	}

	//WASCANCELED ACCESS
	
	public Future<Boolean> wasCanceled() {
		return new DelegateFuture<>(processedObject, (e) -> e.isCanceled());
	}
	
	public boolean waitForWasCanceled() throws InterruptedException {
		if(isHandlingCompleted()) return  currentObject.isCanceled();
		try {
			return wasCanceled().get();
		} catch (ExecutionException e) {
			return currentObject.isCanceled();
		}
	}
	
	//EVENT ACCESS
	
	public Event getCurrentEvent() {
		return currentObject;
	}
	
	public Future<Event> getHandledEvent() {
		return processedObject;
	}
	
	public Event waitForHandledEvent() throws InterruptedException {
		if(isHandlingCompleted()) return currentObject;
		try {
			return processedObject.get();
		} catch (ExecutionException e) {
			return currentObject;
		}
	}
	
	//GENERAL WAIT
	
	public void waitForHandlers() throws InterruptedException{
		if(isHandlingCompleted()) return;
		try { 
			processedObject.get();
		} catch (ExecutionException e) {
			//If execution fails, just return, because execution is over anyways
		}
	}
	
	//STATE / NOT ASYNC
	
	public boolean isHandlingCompleted() {
		return processedObject.isDone();
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
		return new EventResult(true, event, CompletableFuture.completedFuture(event), bus);
	}
	
	public static EventResult createFailed(final Event event, final EventBus bus) {
		return new EventResult(false, event, CompletableFuture.completedFuture(event), bus);
	}
	
}
