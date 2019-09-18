package lb.simplebase.event;

import java.util.ConcurrentModificationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import lb.simplebase.event.EventHandlerImpl.EventHandlerAwaitable;
import lb.simplebase.event.HandlerList.HandlerListAwaitable;

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
	
	@Deprecated //Needs rework
	public synchronized AwaitableEventResult postAwaitable(Event event, EventPriority priority) {
		if(event == null || !isActive() || isHandlerThread()) return AwaitableEventResult.createFailed(event, this);; //Can't post an event from an event handler (at least for single-thread busses) 
		final Class<? extends Event> eventClass = event.getClass();
		//Find the key. We cant use e.getClass() as a key because we need the exact WeakReference instance in the map / key set
		if(!ensureKeyExists(eventClass, false, null)) return AwaitableEventResult.createFailed(event, this);
		//Next, get all Handlers
		final HandlerList handlerSet = getHandlersMap().get(eventClass);
		if(handlerSet == null || handlerSet.isEmpty()) return AwaitableEventResult.createFailed(event, this);; //Just to be safe
		return postAwaitableImpl(handlerSet, event, priority);
	}
	
	private AwaitableEventResult postAwaitableImpl(final HandlerList handlerSet, final Event event, EventPriority priority) {
		final CountDownLatch completionRelease = new CountDownLatch(1);
		final HandlerListAwaitable localSet = handlerSet.awaitable(event.getClass(), priority);
		final EventHandlerAwaitable syncHandler = localSet.getWaiter();
		syncHandler.init(); //setup cyclicBarrier
		taskRunner.execute(() -> {
			isHandlingEvents.set(true);//Set inside lambda, so the worker thread is blocked from posting
			try {	//Protection against bad sync (should not be necessary)
				for(EventHandlerImpl handler : localSet) { //Now iterate over the handlers from local set
					if(handler == null) continue; //HashSet allows a null value
					handler.checkAndPostEvent(event, this, true);	//This is in a separate method so we can have an async implemetation in a subclass
				}
			} catch(ConcurrentModificationException ex) {
				//TODO log warning
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
