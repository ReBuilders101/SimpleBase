package lb.simplebase.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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
	
	@Override
	protected void postEvent(EventHandlerImpl handler, Event event) {
		taskRunner.execute(() -> {
			isHandlingEvents.set(true);//Set inside lambda, so the worker thread is blocked from posting
			try {
				handler.checkAndPostEvent(event);
			} finally {
				isHandlingEvents.set(false);
			}
		});
	}
	
	
	/**
	 * Creates a new event bus that calls handlers on a single thread. The handler thread
	 * may change, but there will always only be one handler running at a time.
	 * @return The new asynchronous {@link EventBus}
	 */
	public static EventBus createSingleThread() {
		return new AsyncEventBus(Executors.newSingleThreadExecutor(executorFactory));
	}
	
	/**
	 * Returns a new event bus that calls handlers on multiple different threads in a thread pool.
	 * @deprecated Should not be used, because it is not guaranteed to run the handlers in order of their priority.
	 * @return The new asynchronous {@link EventBus}
	 */
	@Deprecated
	public static EventBus createThreadPool() {
		return new AsyncEventBus(Executors.newCachedThreadPool(executorFactory));
	}

	@Override
	public boolean isSynchronous() {
		return false;
	}
}
