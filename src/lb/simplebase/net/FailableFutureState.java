package lb.simplebase.net;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class FailableFutureState extends FutureState {
	
	protected FailableFutureState(boolean failed, Consumer<Object> asyncTask) {
		super(failed, asyncTask);
		ex = null;
		errorMessage = null;
		
		if(failed) {
			NetworkManager.NET_LOG.error("FutureState(" + getClass().getSimpleName() + ") failed quickly (No Reason).");
		}
	}
	
	protected FailableFutureState(boolean failed, Consumer<Object> asyncTask, String errmsg, Throwable exc) {
		super(failed, asyncTask);
		ex = exc;
		errorMessage = errmsg;
		
		if(failed) {
			NetworkManager.NET_LOG.error("FutureState(" + getClass().getSimpleName() + ") failed quickly:");
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

	public boolean isFailed() {
		return ex != null || errorMessage != null || isQuickFailed();
	}
	
	@Override
	protected void taskDoneHandler() {
		super.taskDoneHandler();
		
		if(ex != null || errorMessage != null) {
			NetworkManager.NET_LOG.error("FutureState(" + getClass().getSimpleName() + ") failed while executing:");
			if(ex == null) {
				NetworkManager.NET_LOG.error(errorMessage);
			} else {
				NetworkManager.NET_LOG.error(errorMessage == null ? ex.getMessage() : errorMessage, ex);
			}
		}
	}
	
	public class FailableAccessor {
		
		public void setErrorMessage(String message) {
			errorMessage = message;
		}
		
		public void setError(Throwable error) {
			ex = error;
		}

		public void setErrorAndMessage(Throwable error, String message) {
			ex = error;
			errorMessage = message;
		}

		public void setErrorAndMessage(Throwable error) {
			setErrorAndMessage(error, error.getMessage());
		}
		
	}
	
}
