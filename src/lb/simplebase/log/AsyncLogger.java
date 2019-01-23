package lb.simplebase.log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncLogger extends Logger{

	private final ExecutorService logService; 
	
	protected AsyncLogger(String name, OutputChannel channel, LogLevel level, LogMessageFormat format) {
		super(name, channel, level, format);
		logService = Executors.newSingleThreadExecutor();
	}

	@Override
	protected void logImpl(LogMessage message) {
		logService.execute(() -> getChannel().appendMessage(message));
	}
	
}
