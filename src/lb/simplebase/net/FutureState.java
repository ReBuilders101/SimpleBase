package lb.simplebase.net;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;

import lb.simplebase.util.NamedThreadFactory;

/**
 * Implementations represent a {@link Future}, but instead of
 * returning a value, the have members that change until the future is completed
 */
public abstract class FutureState implements AsyncResult {

	private final Future<Void> task;
	private volatile State state;
	
	private static final ExecutorService futureExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("FutureStateProcessing-"));
	
	protected FutureState(boolean failed, Consumer<Object> asyncTask) {
		quickFailed = failed;
		if(failed) {
			this.task = CompletableFuture.completedFuture(null);
			state = State.FINISHED;
		} else {
			Callable<Void> newTask = () -> {
				asyncTask.accept(getAccessor());
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
	
	protected synchronized FutureState runInSync() {
		((FutureTask<Void>) task).run();
		return this;
	}
	
	protected synchronized void setState(State state) {
		this.state = state;
	}
	
	protected synchronized FutureState run() {
		if(state == State.IDLE) { //It is a FutureTask
			state = State.WORKING;
			try {
				futureExecutor.execute((FutureTask<Void>) task);
			} catch (RejectedExecutionException e) {
				NetworkManager.NET_LOG.warn("Rejected FutureState execution, Service might be shut down already - Task not executed", e);
			}
		}
		return this;
	}
	
	public boolean isDone() {
		return task.isDone();
	}
	
	public FutureState sync() throws InterruptedException {
		if(state == State.FINISHED) return this;
		try {
			run();
			task.get();
		} catch (ExecutionException e) {
			//Callable is created from a consumer, so exceptions should not be possible
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public State getState() {
		return state;
	}
	
	public static enum State {
		IDLE, WORKING, FINISHED;
	}
	
	public static void shutdownExecutor() {
		futureExecutor.shutdown();
	}
	
	protected abstract Object getAccessor();
}
