package lb.simplebase.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ExecutorUtils {

	private static final AtomicInteger hookCounter = new AtomicInteger();
	
	private ExecutorUtils() {}
	
	public static void autoShutdown(final ExecutorService service) {
		autoShutdown(service, 30, TimeUnit.SECONDS);
		Executors.newSingleThreadExecutor();
	}

	public static void autoShutdown(final ExecutorService service, final long time, final TimeUnit unit) {
		Thread hook = new Thread(() -> {
			service.shutdown();
			try {
				service.awaitTermination(time, unit);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}, "Executor-Shutdown-Hook-" + hookCounter.getAndIncrement());
		Runtime.getRuntime().addShutdownHook(hook);
	}
	
	
	
}
