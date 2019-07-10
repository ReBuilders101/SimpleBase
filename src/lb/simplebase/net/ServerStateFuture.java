package lb.simplebase.net;

import java.util.function.Consumer;

public class ServerStateFuture extends FailableFutureState{

	protected volatile ServerState currentState;
	protected final ServerState oldState;
	
	protected ServerStateFuture(boolean failed, ServerState oldState, Consumer<ServerStateFuture> task) {
		super(failed, (f) -> task.accept((ServerStateFuture) f));
		this.currentState = oldState;
		this.oldState = oldState;
	}
	
	protected ServerStateFuture(String failMessage, ServerState oldState) {
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
	
	public static ServerStateFuture quickFailed(String message, ServerState unchangedState) {
		return new ServerStateFuture(message, unchangedState);
	}
	
	@Override
	public synchronized ServerStateFuture run() {
		return (ServerStateFuture) super.run();
	}

	public static ServerStateFuture quickDone(ServerState unchangedState) {
		return (ServerStateFuture) new ServerStateFuture(false, unchangedState, (s) -> s.currentState = unchangedState).runInSync();
	}
	
	public static ServerStateFuture create(ServerState oldState, Consumer<ServerStateFuture> task) {
		return new ServerStateFuture(false, oldState, task);
	}
}
