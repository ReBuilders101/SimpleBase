package lb.simplebase.action;

/**
 * Result of a task that can be executed on another thread
 */
public interface AsyncResult extends ResultAction, FailableAction, AsyncAction {

	@Override
	public AsyncResult syncOrError() throws InterruptedException;

	@Override
	public default AsyncResult sync() {
		try {
			return syncOrError();
		} catch (InterruptedException e) {
			return this; //ignore
		}
	}
	
}
