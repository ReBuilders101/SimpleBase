package lb.simplebase.net;

public interface AsyncResult extends Result{

	public void sync() throws InterruptedException;
	
	public default void trySync() {
		try {
			sync();
		} catch (InterruptedException e) {
			return; //ignore
		}
	}
	
}
