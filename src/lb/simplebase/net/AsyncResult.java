package lb.simplebase.net;

public interface AsyncResult extends Result{

	public AsyncResult sync() throws InterruptedException;
	
	public default AsyncResult trySync() {
		try {
			return sync();
		} catch (InterruptedException e) {
			return this; //ignore
		}
	}
	
	public boolean isDone();
	
}
