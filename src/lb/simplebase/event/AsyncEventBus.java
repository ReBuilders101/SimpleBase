package lb.simplebase.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import lb.simplebase.util.NamedThreadFactory;

/**
 * A concurrent implementation of {@link EventBus} that executes handlers
 * on a different thread than the post method was called on.
 */
public class AsyncEventBus extends EventBus {
	
	private static final Collection<ExecutorService> services = new ArrayList<>();
	private static final ThreadFactory executorFactory = new NamedThreadFactory("AsyncEventBus-Executor-");
	
	public static void shotdownExecutors() {
		services.forEach((e) -> e.shutdown());
	}
	
	private final ExecutorService taskRunner;
	
	protected AsyncEventBus(final ExecutorService service) {
		super();
		services.add(service);
		taskRunner = service;
	}
	
	public synchronized AsyncEventResult postAsync(Event event) {
		if(event == null || !isActive() || isHandlerThread()) return AsyncEventResult.createFailed(event, this);; //Can't post an event from an event handler (at least for single-thread busses) 
		final Class<? extends Event> eventClass = event.getClass();
		//Find the key. We cant use e.getClass() as a key because we need the exact WeakReference instance in the map / key set
		if(!ensureKeyExists(eventClass, false, null)) return AsyncEventResult.createFailed(event, this);
		//Next, get all Handlers
		final HandlerList handlerSet = getHandlersMap().get(eventClass);
		if(handlerSet == null || handlerSet.isEmpty()) return AsyncEventResult.createFailed(event, this);; //Just to be safe
		return postAsyncImpl(handlerSet, event);
	}
	
	private synchronized AsyncEventResult postAsyncImpl(HandlerList handlerSet, Event event) {
		final CountDownLatch completionRelease = new CountDownLatch(1);
		taskRunner.execute(() -> {
			isHandlingEvents.set(true);//Set inside lambda, so the worker thread is blocked from posting
			try {	//Protection against bad sync (should not be necessary)
				for(EventHandlerImpl handler : handlerSet) { //Now iterate over the handlers from local set
					if(handler == null) continue; //HashSet allows a null value
					handler.checkAndPostEvent(event, this, true);	//This is in a separate method so we can have an async implemetation in a subclass
				}
			} catch(ConcurrentModificationException ex) {
				System.err.println("Tried post an event on a HandlerSet that was being modified at the time"); //Should not happen
			} finally {
				isHandlingEvents.set(false); //Event handling is done, either throung normal code path or through exception, so make sure it is reset
				completionRelease.countDown();	//Make sure eventResult is completed
			}
		});
		return AsyncEventResult.createAsync(event, completionRelease, this);
	}
	
	/**
	 * Creates a new event bus that calls handlers on a single thread. The handler thread
	 * may change, but there will always only be one handler running at a time.
	 * @return The new asynchronous {@link EventBus}
	 */
	public static AsyncEventBus createSingleThread() {
		return new AsyncEventBus(Executors.newSingleThreadExecutor(executorFactory));
	}
	
	@Override
	public boolean isSynchronous() {
		return false;
	}
}
