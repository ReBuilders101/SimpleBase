package lb.simplebase.log;

public final class LogHelper {

	private LogHelper() {}
	
	private static LogLevel         defaultLevel =   LogLevel.getDefault();
	private static OutputChannel    defaultChannel = OutputChannel.getDefault();
	private static LogMessageFormat defaultFormat =  LogMessageFormat.getDefault();
	
	//Calls methods instead of using the fields above because fields are mutable while method call result is not.
	private static final Logger defaultLogger = new Logger("Default", OutputChannel.getDefault(), LogLevel.getDefault(), LogMessageFormat.getDefault());
	
	public static LogLevel getDefaultLogLevel() {
		return defaultLevel;
	}
	
	public static void setDefaultLogLevel(LogLevel level) {
		defaultLevel = level;
	}
	
	public static OutputChannel getDefaultOutputChannel() {
		return defaultChannel;
	}
	
	public static void setDefaultOutputChannel(OutputChannel channel) {
		defaultChannel = channel;
	}
	
	public static LogMessageFormat getDefaultLogMessageFormat() {
		return defaultFormat;
	}
	
	public static void setDefaultLogMessageFormat(LogMessageFormat format) {
		defaultFormat = format;
	}
	
	public static Logger getDefaultLogger() {
		return defaultLogger;
	}
	
	public static Logger create(String name, OutputChannel channel) {
		return create(name, channel, getDefaultLogLevel(), getDefaultLogMessageFormat());
	}
	
	public static Logger create(Class<?> clazz, OutputChannel channel) {
		return create(clazz, channel, getDefaultLogLevel(), getDefaultLogMessageFormat());
	}
	
	public static Logger create(String name) {
		return create(name, getDefaultOutputChannel(), getDefaultLogLevel(), getDefaultLogMessageFormat());
	}
	
	public static Logger create(Class<?> clazz) {
		return create(clazz, getDefaultOutputChannel(), getDefaultLogLevel(), getDefaultLogMessageFormat());
	}
	
	public static Logger create(String name, LogLevel level) {
		return create(name, getDefaultOutputChannel(), level, getDefaultLogMessageFormat());
	}
	
	public static Logger create(Class<?> clazz, LogLevel level) {
		return create(clazz, getDefaultOutputChannel(), level, getDefaultLogMessageFormat());
	}
	
	public static Logger create(String name, OutputChannel channel, LogMessageFormat format) {
		return create(name, channel, getDefaultLogLevel(), format);
	}
	
	public static Logger create(Class<?> clazz, OutputChannel channel, LogMessageFormat format) {
		return create(clazz, channel, getDefaultLogLevel(), format);
	}
	
	public static Logger create(String name, LogMessageFormat format) {
		return create(name, getDefaultOutputChannel(), getDefaultLogLevel(), format);
	}
	
	public static Logger create(Class<?> clazz, LogMessageFormat format) {
		return create(clazz, getDefaultOutputChannel(), getDefaultLogLevel(), format);
	}
	
	public static Logger create(String name, LogLevel level, LogMessageFormat format) {
		return create(name, getDefaultOutputChannel(), level, format);
	}
	
	public static Logger create(Class<?> clazz, LogLevel level, LogMessageFormat format) {
		return create(clazz, getDefaultOutputChannel(), level, format);
	}
	
	public static Logger create(String name, OutputChannel channel, LogLevel level) {
		return create(name, channel, level, getDefaultLogMessageFormat());
	}
	
	public static Logger create(Class<?> clazz, OutputChannel channel, LogLevel level) {
		return create(clazz, channel, level, getDefaultLogMessageFormat());
	}
	
	public static Logger create(String name, OutputChannel channel, LogLevel level, LogMessageFormat format) {
		return new Logger(name, channel, level, format);
	}
	
	public static Logger create(Class<?> clazz, OutputChannel channel, LogLevel level, LogMessageFormat format) {
		return create(clazz.getSimpleName(), channel, level, format);
	}
	
	public static Logger createAsync(String name, OutputChannel channel) {
		return createAsync(name, channel, getDefaultLogLevel(), getDefaultLogMessageFormat());
	}
	
	public static Logger createAsync(Class<?> clazz, OutputChannel channel) {
		return createAsync(clazz, channel, getDefaultLogLevel(), getDefaultLogMessageFormat());
	}
	
	public static Logger createAsync(String name) {
		return createAsync(name, getDefaultOutputChannel(), getDefaultLogLevel(), getDefaultLogMessageFormat());
	}
	
	public static Logger createAsync(Class<?> clazz) {
		return createAsync(clazz, getDefaultOutputChannel(), getDefaultLogLevel(), getDefaultLogMessageFormat());
	}
	
	public static Logger createAsync(String name, LogLevel level) {
		return createAsync(name, getDefaultOutputChannel(), level, getDefaultLogMessageFormat());
	}
	
	public static Logger createAsync(Class<?> clazz, LogLevel level) {
		return createAsync(clazz, getDefaultOutputChannel(), level, getDefaultLogMessageFormat());
	}
	
	public static Logger createAsync(String name, OutputChannel channel, LogMessageFormat format) {
		return createAsync(name, channel, getDefaultLogLevel(), format);
	}
	
	public static Logger createAsync(Class<?> clazz, OutputChannel channel, LogMessageFormat format) {
		return createAsync(clazz, channel, getDefaultLogLevel(), format);
	}
	
	public static Logger createAsync(String name, LogMessageFormat format) {
		return createAsync(name, getDefaultOutputChannel(), getDefaultLogLevel(), format);
	}
	
	public static Logger createAsync(Class<?> clazz, LogMessageFormat format) {
		return createAsync(clazz, getDefaultOutputChannel(), getDefaultLogLevel(), format);
	}
	
	public static Logger createAsync(String name, LogLevel level, LogMessageFormat format) {
		return createAsync(name, getDefaultOutputChannel(), level, format);
	}
	
	public static Logger createAsync(Class<?> clazz, LogLevel level, LogMessageFormat format) {
		return createAsync(clazz, getDefaultOutputChannel(), level, format);
	}
	
	public static Logger createAsync(String name, OutputChannel channel, LogLevel level) {
		return createAsync(name, channel, level, getDefaultLogMessageFormat());
	}
	
	public static Logger createAsync(Class<?> clazz, OutputChannel channel, LogLevel level) {
		return createAsync(clazz, channel, level, getDefaultLogMessageFormat());
	}
	
	public static Logger createAsync(String name, OutputChannel channel, LogLevel level, LogMessageFormat format) {
		return new AsyncLogger(name, channel, level, format);
	}
	
	public static Logger createAsync(Class<?> clazz, OutputChannel channel, LogLevel level, LogMessageFormat format) {
		return createAsync(clazz.getSimpleName(), channel, level, format);
	}
	
}
