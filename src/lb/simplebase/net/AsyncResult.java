package lb.simplebase.net;

public interface AsyncResult extends Result{

	public void sync() throws InterruptedException;
	
}
