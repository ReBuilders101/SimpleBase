package lb.simplebase.event;

import java.util.ConcurrentModificationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import lb.simplebase.event.EventHandlerImpl.EventHandlerAwaitable;

/**
 * A concurrent implementation of {@link EventBus} that executes handlers
 * on a different thread than the post method was called on.
 */
public class AsyncEventBus extends EventBus {
	
	private static final AtomicInteger threadId = new AtomicInteger();
	
	private static final ThreadFactory executorFactory = new ThreadFactory() {
		
		@Override
		public Thread newThread(Runnable var1) {
			Thread t = Executors.defaultThreadFactory().newThread(var1);
			t.setName("AsyncEventBus-Executor-" + threadId.getAndIncrement());
			return t;
		}
	};
	
	private final ExecutorService taskRunner;
	
	protected AsyncEventBus(ExecutorService service) {
		super();
		taskRunner = service;
	}
	
	public synchronized AwaitableEventResult postAwaitable(Event event) {
		if(event == null || !isActive() || isHandlerThread()) return AwaitableEventResult.createFailed(event, this);; //Can't post an event from an event handler (at least for single-thread busses) 
		final Class<? extends Event> eventClass = event.getClass();
		//Find the key. We cant use e.getClass() as a key because we need the exact WeakReference instance in the map / key set
		final HandlerList handlerSet;
//		synchronized (handlersMap) {//Make sure the handlers map is not changed while traversing keys and finding the handlers set for this event
			if(!ensureKeyExists(eventClass, false, null)) return AwaitableEventResult.createFailed(event, this);
			//Next, get all Handlers
			handlerSet = getHandlersMap().get(eventClass);
//		}//Syncronisation not needed anymore. We now have a local reference to the handler list.
		if(handlerSet == null || handlerSet.isEmpty()) return AwaitableEventResult.createFailed(event, this);; //Just to be safe
//		synchronized (handlerSet) { //Because HashSet's iterator is fail-fast, we have to prevent concurrent modification here too
			return postAwaitableImpl(handlerSet, event);
//		}//End sync on set, iterator is done
	}
	
	private AwaitableEventResult postAwaitableImpl(final HandlerList handlerSet, final Event event) {
		final CountDownLatch completionRelease = new CountDownLatch(1);
		final EventHandlerAwaitable syncHandler = handlerSet.getOrCreateWaitHandler(true, event.getClass());
		taskRunner.execute(() -> {
			isHandlingEvents.set(true);//Set inside lambda, so the worker thread is blocked from posting
			try {
				try {	//Protection against bad sync (should not be necessary)
					for(EventHandlerImpl handler : handlerSet) { //Now iterate over the handlers
						if(handler == null) continue; //HashSet allows a null value
						handler.checkAndPostEvent(event, this, true);	//This is in a separate method so we can have an async implemetation in a subclass
					}
				} catch(ConcurrentModificationException ex) {}	//TODO log warning
			} finally {
				isHandlingEvents.set(false); //Event handling is done, either throung normal code path or through exception, so make sure it is reset
				completionRelease.countDown();	//Make sure eventResult is completed
			}
		});
		return AwaitableEventResult.createAwaitable(event, completionRelease, syncHandler, this);
	}
	
	@Override
	protected EventResult postImpl(final Iterable<EventHandlerImpl> handlerSet, final Event event) {
		final CountDownLatch completionRelease = new CountDownLatch(1);
		taskRunner.execute(() -> {
			isHandlingEvents.set(true);//Set inside lambda, so the worker thread is blocked from posting
			try {
				try {	//Protection against bad sync (should not be necessary)
					for(EventHandlerImpl handler : handlerSet) { //Now iterate over the handlers
						if(handler == null) continue; //HashSet allows a null value
						handler.checkAndPostEvent(event, this, false);	//This is in a separate method so we can have an async implemetation in a subclass
					}
				} catch(ConcurrentModificationException ex) {}	//TODO log warning
			} finally {
				isHandlingEvents.set(false); //Event handling is done, either throung normal code path or through exception, so make sure it is reset
				completionRelease.countDown();	//Make sure eventResult is completed
			}
		});
		return EventResult.createAsynchronous(event, this, completionRelease);
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
