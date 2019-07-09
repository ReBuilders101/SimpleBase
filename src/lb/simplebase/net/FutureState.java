package lb.simplebase.net;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Implementations represent a {@link Future}, but instead of
 * returning a value, the have members that change until the future is completed
 */
public abstract class FutureState implements AsyncResult{

	private Future<Void> task;
	private Supplier<FutureState> thisSupply = () -> this;
	
	protected FutureState(boolean failed, Consumer<FutureState> asyncTask) {
		quickFailed = failed;
		if(failed) {
			this.task = CompletableFuture.completedFuture(null);
			started = true;
		} else {
			Callable<Void> newTask = () -> {
				asyncTask.accept(thisSupply.get());
				return null;
			};
			started = false;
			this.task = new FutureTask<>(newTask);
		}
	}
	
	private final boolean quickFailed;
	private volatile boolean started;
	
	/**
	 * Whether the operation represented by this {@link FutureState} failed quickly.
	 * This means that the operation was aborted before any asynchronous operation happened.
	 * This value will never change and if true, no values in this {@link FutureState} will be altered asynchronously.
	 * @return
	 */
	public final boolean isQuickFailed() {
		return quickFailed;
	}
	
	protected synchronized void run() {
		if(!started) { //It is a FutureTask
			started = true;
			((FutureTask<Void>) task).run();
		}
	}
	
	public boolean isDone() {
		return task.isDone();
	}
	
	public void sync() throws InterruptedException {
		try {
			task.get();
		} catch (ExecutionException e) {
			//Callable is created from a consumer, so exceptions should not be possible
			throw new RuntimeException(e);
		}
	}
}
