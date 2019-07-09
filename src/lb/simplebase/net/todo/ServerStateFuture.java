package lb.simplebase.net.todo;

import java.util.function.Consumer;

import lb.simplebase.net.FailableFutureState;
import lb.simplebase.net.FutureState;

public class ServerStateFuture extends FailableFutureState{

	protected ServerStateFuture(boolean failed) {
		super(failed, null);
		// TODO Auto-generated constructor stub
	}
}
