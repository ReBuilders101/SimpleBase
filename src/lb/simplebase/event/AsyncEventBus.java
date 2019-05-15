package lb.simplebase.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

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
	
	
	
	public static AsyncEventBus createSingleThread() {
		return new AsyncEventBus(Executors.newSingleThreadExecutor(executorFactory));
	}
	
	public static AsyncEventBus createThreadPool() {
		return new AsyncEventBus(Executors.newCachedThreadPool(executorFactory));
	}
	
	public static AsyncEventBus createCustom(Function<ThreadFactory, ExecutorService> serviceCreator) {
		return new AsyncEventBus(serviceCreator.apply(executorFactory));
	}
}
