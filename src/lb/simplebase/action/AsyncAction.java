package lb.simplebase.action;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

public interface AsyncAction {

	/**
	 * Waits for the asynchrounous task to complete.
	 * This method will block the calling thread until the task completes, fails, or the calling thread is interrupted.
	 * @return This {@link AsyncResult}, for method chaining
	 * @throws InterruptedException When the calling thread is interrupted while waiting
	 * @see #trySync()
	 */
	public AsyncAction syncOrError() throws InterruptedException;
	
	/**
	 * Waits for the asynchrounous task to complete.
	 * This method will block the calling thread until the task completes, fails, or the calling thread is interrupted.
	 * When the thread is interrupted, this method will return without throwing an {@link InterruptedException}.
	 * @return This {@link AsyncResult}, for method chaining
	 * @see #sync()
	 */	public default AsyncAction sync() {
		try {
			return syncOrError();
		} catch (InterruptedException e) {
			return this; //ignore
		}
	}
	
	/**
	 * Whether the asynchrounous task is done. The task is done when it has completed successfully or when it failed.
	 * @return Whether the task is done
	 */
	public boolean isDone();

	public void addDoneHandler(Runnable handler);
	
	public static abstract class DoneHandler implements AsyncAction {
		
		private Collection<Runnable> doneHandlers;
		private final Supplier<? extends Collection<Runnable>> doneHandlersCreator;
		
		protected DoneHandler(Supplier<? extends Collection<Runnable>> collectionFactory) {
			this.doneHandlersCreator = collectionFactory;
			this.doneHandlers = null;
		}
		
		@Override
		public void addDoneHandler(Runnable handler) {
			if(isDone()) {
				handler.run();
			} else {
				if(doneHandlers == null) doneHandlers = doneHandlersCreator.get();
				Objects.requireNonNull(doneHandlers, "The handler collection supplier must not return null");
				doneHandlers.add(handler);
			}
		}
		
		protected void runDoneHandlers() {
			if(doneHandlers == null) return;
			doneHandlers.forEach((r) -> r.run()); 
		}
		
	}
	
}
