package lb.simplebase.net.done;

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

	private final Future<Void> task;
	private final Supplier<FutureState> thisSupply = () -> this;
	private volatile State state;
	
	protected FutureState(boolean failed, Consumer<FutureState> asyncTask) {
		quickFailed = failed;
		if(failed) {
			this.task = CompletableFuture.completedFuture(null);
			state = State.FINISHED;
		} else {
			Callable<Void> newTask = () -> {
				asyncTask.accept(thisSupply.get());
				taskDoneHandler();
				return null;
			};
			state = State.IDLE;
			this.task = new FutureTask<>(newTask);
		}
	}
	
	private final boolean quickFailed;
	
	/**
	 * Whether the operation represented by this {@link FutureState} failed quickly.
	 * This means that the operation was aborted before any asynchronous operation happened.
	 * This value will never change and if true, no values in this {@link FutureState} will be altered asynchronously.
	 * @return
	 */
	public final boolean isQuickFailed() {
		return quickFailed;
	}
	
	protected synchronized void taskDoneHandler() {
		state = State.FINISHED;
	}
	
	protected synchronized void run() {
		if(state == State.IDLE) { //It is a FutureTask
			state = State.WORKING;
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
	
	public State getState() {
		return state;
	}
	
	public static enum State {
		IDLE, WORKING, FINISHED;
	}
}
