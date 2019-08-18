package lb.simplebase.net;

/**
 * Result of a task that can be executed on another thread
 */
public interface AsyncResult extends Result{

	/**
	 * Waits for the asynchrounous task to complete.
	 * This method will block the calling thread until the task completes, fails, or the calling thread is interrupted.
	 * @return This {@link AsyncResult}, for method chaining
	 * @throws InterruptedException When the calling thread is interrupted while waiting
	 * @see #trySync()
	 */
	public AsyncResult sync() throws InterruptedException;
	
	/**
	 * Waits for the asynchrounous task to complete.
	 * This method will block the calling thread until the task completes, fails, or the calling thread is interrupted.
	 * When the thread is interrupted, this method will return without throwing an {@link InterruptedException}.
	 * @return This {@link AsyncResult}, for method chaining
	 * @see #sync()
	 */	public default AsyncResult trySync() {
		try {
			return sync();
		} catch (InterruptedException e) {
			return this; //ignore
		}
	}
	
	/**
	 * Whether the asynchrounous task is done. The task is done when it has completed successfully or when it failed.
	 * @return Whether the task is done
	 */
	public boolean isDone();
	
}
