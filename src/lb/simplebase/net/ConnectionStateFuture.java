package lb.simplebase.net;

import java.util.function.Consumer;

public class ConnectionStateFuture extends FailableFutureState {

	protected ConnectionStateFuture(boolean failed, Consumer<ConnectionStateFuture> asyncTask, ConnectionState oldState) {
		super(failed, (fs) -> asyncTask.accept((ConnectionStateFuture) fs));
		this.oldState = oldState;
		this.currentState = oldState;
	}
	
	protected ConnectionStateFuture(String failMessage, ConnectionState oldState) {
		super(true, null, failMessage, null);
		errorMessage = failMessage;
		this.oldState = oldState;
		this.currentState = oldState;
	}
	
	protected volatile ConnectionState currentState;
	protected final ConnectionState oldState;
	
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
	
	protected static ConnectionStateFuture create(ConnectionState state, Consumer<ConnectionStateFuture> task) {
		return new ConnectionStateFuture(false, task, state);
	}
	
	//If the connection succeeded, but no async action was necessary (local connections)
	protected static ConnectionStateFuture quickDone(ConnectionState oldState, ConnectionState newState) {
		ConnectionStateFuture csf = new ConnectionStateFuture(false, (c) -> c.currentState = newState, oldState);
		return (ConnectionStateFuture) csf.runInSync();
	}
}
