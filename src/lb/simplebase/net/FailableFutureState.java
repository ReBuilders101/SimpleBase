package lb.simplebase.net;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class FailableFutureState extends FutureState{
	
	protected FailableFutureState(boolean failed, Consumer<FutureState> asyncTask) {
		super(failed, asyncTask);
		ex = null;
		errorMessage = null;
		
		if(failed) {
			NetworkManager.NET_LOG.warn("FutureState(" + getClass().getSimpleName() + ") failed quickly.");
		}
	}
	
	protected FailableFutureState(boolean failed, Consumer<FutureState> asyncTask, String errmsg, Throwable exc) {
		super(failed, asyncTask);
		ex = exc;
		errorMessage = errmsg;
		
		if(failed) {
			NetworkManager.NET_LOG.warn("FutureState(" + getClass().getSimpleName() + ") failed quickly:");
			if(ex != null) {
				NetworkManager.NET_LOG.error(errorMessage == null ? ex.getMessage() : errorMessage, ex);
			} else if(errorMessage != null) {
				NetworkManager.NET_LOG.error(errorMessage);
			}
		}
	}

	protected volatile Throwable ex;
	
	public Optional<Throwable> getException() {
		return Optional.ofNullable(ex);
	}
	
	protected volatile String errorMessage;
	
	public Optional<String> getErrorMessage() {
		return Optional.ofNullable(errorMessage);
	}

	public boolean hasError() {
		return ex != null || errorMessage != null || isQuickFailed();
	}
	
	@Override
	protected void taskDoneHandler() {
		super.taskDoneHandler();
		NetworkManager.NET_LOG.warn("FutureState(" + getClass().getSimpleName() + ") failed while executing:");
		if(ex != null) {
			NetworkManager.NET_LOG.error(errorMessage == null ? ex.getMessage() : errorMessage, ex);
		} else if(errorMessage != null) {
			NetworkManager.NET_LOG.error(errorMessage);
		}
	}
	
}
