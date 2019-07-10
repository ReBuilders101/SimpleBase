package lb.simplebase.log;

public class CurrentThreadNameFormat implements LogMessageFormat{

	private LogMessageFormat delegate = LogMessageFormat.getDefault();
	
	@Override
	public LogMessage create(String loggerName, LogLevel level, String message) {
		return delegate.create(loggerName, level, wrap(message));
	}

	@Override
	public LogMessage create(String loggerName, LogLevel level, String format, Object[] objects) {
		return delegate.create(loggerName, level, wrap(format), objects);
	}

	@Override
	public LogMessage create(String loggerName, LogLevel level, Throwable t, String message) {
		return delegate.create(loggerName, level, t, wrap(message));
	}

	@Override
	public LogMessage create(String loggerName, LogLevel level, Throwable t, String format, Object[] objects) {
		return delegate.create(loggerName, level, t, wrap(format), objects);
	}

	private static String wrap(String message) {
		return "[" + Thread.currentThread().getName() + "] " + message;
	}
	
}
