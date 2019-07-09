package lb.simplebase.net.done;

import java.io.PrintStream;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class FailableFutureState extends FutureState{

	public static boolean SHOULD_LOG_DEFAULT = false;
	public static PrintStream LOG_DEFAULT_STREAM = System.err;
	
	protected FailableFutureState(boolean failed, Consumer<FutureState> asyncTask) {
		super(failed, asyncTask);
		ex = null;
		errorMessage = null;
		shouldLog = SHOULD_LOG_DEFAULT;
	}

	protected volatile Throwable ex;
	
	public Optional<Throwable> getException() {
		return Optional.ofNullable(ex);
	}
	
	protected volatile String errorMessage;
	
	public Optional<String> getErrorMessage() {
		return Optional.ofNullable(errorMessage);
	}
	
	protected boolean shouldLog;
	
	public void log() {
		shouldLog = true;
	}

	@Override
	protected void taskDoneHandler() {
		super.taskDoneHandler();
		if(shouldLog) {
			if(errorMessage != null) LOG_DEFAULT_STREAM.println(errorMessage);
			if(ex != null) ex.printStackTrace(LOG_DEFAULT_STREAM);
		}
	}
	
}
