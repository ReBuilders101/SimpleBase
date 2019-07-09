package lb.simplebase.net;

import java.util.function.Consumer;

import lb.simplebase.net.done.FailableFutureState;
import lb.simplebase.net.done.FutureState;

public class ServerStateFuture extends FailableFutureState{

	protected ServerStateFuture(boolean failed) {
		super(failed, null);
		// TODO Auto-generated constructor stub
	}
}
