package lb.simplebase.net;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;

import lb.simplebase.action.AsyncResult;
import lb.simplebase.util.NamedThreadFactory;
import lb.simplebase.action.AsyncAction.DoneHandler;

/**
 * Implementations represent a {@link Future}, but instead of
 * returning a value, the have members that change until the future is completed
 */
public final class AsyncNetTask extends DoneHandler implements AsyncResult {

	private final Future<Void> task;
	private volatile State state;
	
	private Exception error;
	private String errorMessage;
	private final Accessor accessor;
	
	private static final ExecutorService futureExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("FutureStateProcessing-"));
	
	public class Accessor {
		public synchronized void setErrorAndMessage(Exception e, String message) {
			error = e;
			if(message == null && error != null) {
				errorMessage = e.getMessage();
			} else {
				errorMessage = message;
			}
		}
	}
	
	/**
	 * If <code>true</code>, tasks will run in another thread.
	 * If <code>false</code>, tasks will run in the thread that call {@link #run()} and will be done when the method returns.
	 */
	protected static volatile boolean RUN_ASYNC = true;
	
	public static AsyncResult submitTask(Consumer<Accessor> task) {
		return createTask(task).run();
	}
	
	protected static AsyncNetTask createTask(Consumer<Accessor> task) {
		return new AsyncNetTask(false, task, null, null, false);
	}
	
	protected static AsyncNetTask createFailed(Exception ex, String message) {
		return new AsyncNetTask(true, null, ex, message, true);
	}
	
	private AsyncNetTask(boolean failed, Consumer<Accessor> asyncTask, Exception ex, String message, boolean log) {
		super(() -> new ArrayList<>());
		this.accessor = new Accessor();
		if(failed) {
			this.task = CompletableFuture.completedFuture(null);
			this.state = State.FINISHED;
			this.error = ex;
			this.errorMessage = message;
			if(log) {
				if(error == null) {
					NetworkManager.NET_LOG.error(errorMessage);
				} else {
					NetworkManager.NET_LOG.error(errorMessage == null ? error.getMessage() : errorMessage, error);
				}
			}
		} else {
			Callable<Void> newTask = () -> {
				try {
					asyncTask.accept(accessor);
				} finally { //Make sure that taskDoneHandler is called when an uncaught exception is thrown
					taskDoneHandler();
				}
				return null;
			};
			this.state = State.IDLE;
			this.task = new FutureTask<>(newTask);
		}
	}
	
	public Exception getError() {
		return error;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	protected void taskDoneHandler() {
		state = State.FINISHED;
		if(isFailed()) NetworkManager.NET_LOG.error(errorMessage, error);
		runDoneHandlers();
	}
	
	@Override
	public boolean isFailed() {
		return task.isCancelled() || error != null || errorMessage != null;
	}

	@Override
	public boolean isSuccess() {
		return task.isDone() && !isFailed();
	}

	protected synchronized AsyncNetTask runInSync() {
		if(state == State.IDLE) { //If task is CompletedFuture, state will be finished
			((FutureTask<Void>) task).run();
		}
		return this;
	}
	
	protected synchronized void setState(State state) {
		this.state = state;
	}
	
	protected synchronized AsyncNetTask run() {
		if(RUN_ASYNC) {
			if(state == State.IDLE) { //It is a FutureTask (otherwise, it's already done)
				state = State.WORKING;
				try {
					futureExecutor.execute((FutureTask<Void>) task);
				} catch (RejectedExecutionException e) {
					NetworkManager.NET_LOG.warn("Rejected FutureState execution, Service might be shut down already - Task not executed", e);
				}
			}
			return this;
		} else {
			return runInSync();
		}
	}
	
	public boolean isDone() {
		return task.isDone();
	}
	
	public AsyncResult syncOrError() throws InterruptedException {
		if(state == State.FINISHED) return this;
		try {
			task.get();
		} catch (ExecutionException e) {
			accessor.setErrorAndMessage(e, null);
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

}
