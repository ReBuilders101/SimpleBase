package lb.simplebase.net;

import java.util.function.Consumer;

public class ConnectionStateFuture extends FailableFutureState {

	private ConnectionStateFuture(boolean failed, Consumer<Accessor> asyncTask, ConnectionState oldState) {
		super(failed, (fs) -> asyncTask.accept((Accessor) fs));
		this.oldState = oldState;
		this.currentState = oldState;
	}
	
	private ConnectionStateFuture(String failMessage, ConnectionState oldState) {
		super(true, null, failMessage, null);
		errorMessage = failMessage;
		this.oldState = oldState;
		this.currentState = oldState;
	}
	
	private volatile ConnectionState currentState;
	private final ConnectionState oldState;
	
	public ConnectionState getOldState() {
		return oldState;
	}
	
	@Override
	protected synchronized ConnectionStateFuture run() {
		return (ConnectionStateFuture) super.run();
	}

	public ConnectionState getCurrentState() {
		return currentState;
	}
	
	public boolean ensureState(ConnectionState state) throws InterruptedException {
		sync();
		return getCurrentState() == state;
	}
	
	protected static ConnectionStateFuture quickFailed(String message, ConnectionState unchangedState) {
		return new ConnectionStateFuture(message, unchangedState);
	}
	
	protected static ConnectionStateFuture create(ConnectionState state, Consumer<Accessor> task) {
		return new ConnectionStateFuture(false, task, state);
	}
	
	//If the connection succeeded, but no async action was necessary (local connections)
	protected static ConnectionStateFuture quickDone(ConnectionState oldState, ConnectionState newState) {
		ConnectionStateFuture csf = new ConnectionStateFuture(false, (c) -> c.setCurrentState(newState), oldState);
		return (ConnectionStateFuture) csf.runInSync();
	}
	
	public class Accessor extends FailableAccessor{
		
		public void setCurrentState(ConnectionState state) {
			currentState = state;
		}
		
	}

	@Override
	protected Object getAccessor() {
		return new Accessor();
	}
}
