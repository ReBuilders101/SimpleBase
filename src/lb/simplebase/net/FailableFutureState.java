package lb.simplebase.net;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class FailableFutureState extends FutureState{

	protected FailableFutureState(boolean failed, Consumer<FutureState> asyncTask) {
		super(failed, asyncTask);
	}

	protected volatile Throwable ex;
	
	public Optional<Throwable> getException() {
		return Optional.ofNullable(ex);
	}
	
	protected volatile String errorMessage;
	
	public Optional<String> getErrorMessage() {
		return Optional.ofNullable(errorMessage);
	}
}
