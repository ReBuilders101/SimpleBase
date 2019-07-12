package lb.simplebase.net;

import java.util.function.Consumer;

public class ServerStateFuture extends FailableFutureState{

	private volatile ServerState currentState;
	private final ServerState oldState;
	
	private ServerStateFuture(boolean failed, ServerState oldState, Consumer<Accessor> task) {
		super(failed, (f) -> task.accept((Accessor) f));
		this.currentState = oldState;
		this.oldState = oldState;
	}
	
	private ServerStateFuture(String failMessage, ServerState oldState) {
		super(true, null, failMessage, null);
		this.errorMessage = failMessage;
		this.currentState = oldState;
		this.oldState = oldState;
	}
	
	public ServerState getOldState() {
		return oldState;
	}
	
	public ServerState getCurrentState() {
		return currentState;
	}
	
	public boolean ensureState(ServerState state) throws InterruptedException {
		sync();
		return currentState == state;
	}
	
	protected static ServerStateFuture quickFailed(String message, ServerState unchangedState) {
		return new ServerStateFuture(message, unchangedState);
	}
	
	@Override
	protected synchronized ServerStateFuture run() {
		return (ServerStateFuture) super.run();
	}

	protected static ServerStateFuture quickDone(ServerState unchangedState) {
		return (ServerStateFuture) new ServerStateFuture(false, unchangedState, (s) -> s.setServerState(unchangedState)).runInSync();
	}
	
	protected static ServerStateFuture create(ServerState oldState, Consumer<Accessor> task) {
		return new ServerStateFuture(false, oldState, task);
	}

	public class Accessor extends FailableAccessor {
		
		public void setServerState(ServerState newState) {
			currentState = newState;
		}
		
	}
	
	@Override
	protected Object getAccessor() {
		return new Accessor();
	}

	@Override
	public boolean isSuccess() {
		return !isFailed();
	}
}
