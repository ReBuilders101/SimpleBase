package lb.simplebase.log;

import java.io.OutputStream;
import java.util.function.BinaryOperator;

/**
 * A {@link Logger} can be used to write formatted messages to an {@link OutputChannel}, wich can,
 * for example, be an {@link OutputStream}.<br>
 * Instances can be created with the {@link LogHelper} class, and a default logger is available through {@link LogHelper#getDefaultLogger()}.
 */
public class Logger {
	
	private OutputChannel channel;
	private String name;
	private LogLevel minimalLevel;
	private LogMessageFormat format;
	
	public static final BinaryOperator<String> APPEND_COMBINER = (a, b) -> a + b; 
	
	protected Logger(String name, OutputChannel channel, LogLevel level, LogMessageFormat format) {
		this.name = name;
		this.channel = channel;
		this.minimalLevel = level;
		this.format = format;
	}
	
	protected OutputChannel getChannel() {
		return channel;
	}
	
	/**
	 * The name of the logger, which will be passed to the {@link LogMessageFormat} instance that formats the messages.
	 * The name fill be displayed as <code>[Name]</code> in front of the message if the default {@link LogMessageFormat}
	 * ({@link LogHelper#getDefaultLogMessageFormat()}) is used.
	 * @return The name of the logger
	 */
	public String getName() {
		return name;
	}
	
	public LogLevel getMinimalLevel() {
		return minimalLevel;
	}
	
	////////////////////////////LOG METHODS//////////////////////////
	
	/**
	 * Logs a text message.<br>
	 * The message is only logged if the level is greater than this logger's
	 * minimal logging level ({@link #getMinimalLevel()}). The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param level The {@link LogLevel} of the message
	 * @param message The message text
	 */
	public void log(LogLevel level, String message) {
		tryLog(level, this.format.create(name, level, message));
	}
	
	/**
	 * Logs a text message and additional objects. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * The message is only logged if the level is greater than this logger's
	 * minimal logging level ({@link #getMinimalLevel()}). The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param level The {@link LogLevel} of the message
	 * @param format The message text that describes how to format the objects
	 * @param objects Additional objects that should be logged
	 */
	public void log(LogLevel level, String format, Object...objects) {
		tryLog(level, this.format.create(name, level, format, objects));
	}
	
	/**
	 * Logs a text message and a {@link Throwable}.<br>
	 * The message is only logged if the level is greater than this logger's
	 * minimal logging level ({@link #getMinimalLevel()}). The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param level The {@link LogLevel} of the message
	 * @param message The message text
	 * @param throwable The throwable that should be logged
	 */
	public void log(LogLevel level, String message, Throwable throwable) {
		tryLog(level, this.format.create(name, level, throwable, message));
	}
	
	/**
	 * Logs a text message, a throwable and additional objects. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * The message is only logged if the level is greater than this logger's
	 * minimal logging level ({@link #getMinimalLevel()}). The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param level The {@link LogLevel} of the message
	 * @param format The message text that describes how to format the objects
	 * @param throwable The throwable that should be logged
	 * @param objects Additional objects that should be logged
	 */
	public void log(LogLevel level, String format, Throwable throwable, Object...toString) {
		tryLog(level, this.format.create(name, level, throwable, format, toString));
	}

	////////////////////////////////DEBUG///////////////////////////////////
	
	/**
	 * Logs a text message at {@link LogLevel#DEBUG}.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#DEBUG}. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param message The message text
	 */
	public void debug(String message) {
		log(LogLevel.DEBUG, message);
	}
	
	/**
	 * Logs a text message and additional objects at {@link LogLevel#DEBUG}. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#DEBUG}. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param format The message text that describes how to format the objects
	 * @param objects Additional objects that should be logged
	 */
	public void debug(String format, Object...objects) {
		log(LogLevel.DEBUG, format, objects);
	}
	
	/**
	 * Logs a text message and a {@link Throwable} at {@link LogLevel#DEBUG}.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#DEBUG}. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param message The message text
	 * @param throwable The throwable that should be logged
	 */
	public void debug(String message, Throwable throwable) {
		log(LogLevel.DEBUG, message, throwable);
	}
	
	
	/**
	 * Logs a text message, a throwable and additional objects at {@link LogLevel#DEBUG}. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#DEBUG}. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param format The message text that describes how to format the objects
	 * @param throwable The throwable that should be logged
	 * @param objects Additional objects that should be logged
	 */
	public void debug(String format, Throwable throwable, Object...objects) {
		log(LogLevel.DEBUG, format, throwable, objects);
	}
	
	//////////////////////////////////////INFO//////////////////////////////////
	
	/**
	 * Logs a text message at {@link LogLevel#INFO}.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#INFO} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param message The message text
	 */
	public void info(String message) {
		log(LogLevel.INFO, message);
	}
	
	/**
	 * Logs a text message and additional objects at {@link LogLevel#INFO}. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#INFO} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param format The message text that describes how to format the objects
	 * @param objects Additional objects that should be logged
	 */
	public void info(String format, Object...objects) {
		log(LogLevel.INFO, format, objects);
	}
	
	/**
	 * Logs a text message and a {@link Throwable} at {@link LogLevel#INFO}.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#INFO} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param message The message text
	 * @param throwable The throwable that should be logged
	 */
	public void info(String message, Throwable throwable) {
		log(LogLevel.INFO, message, throwable);
	}
	
	/**
	 * Logs a text message, a throwable and additional objects at {@link LogLevel#INFO}. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#INFO} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param format The message text that describes how to format the objects
	 * @param throwable The throwable that should be logged
	 * @param objects Additional objects that should be logged
	 */
	public void info(String format, Throwable throwable, Object...objects) {
		log(LogLevel.INFO, format, throwable, objects);
	}
	
	////////////////////////////////////WARN//////////////////////////////////////
	
	/**
	 * Logs a text message at {@link LogLevel#WARN}.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#WARN} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param message The message text
	 */
	public void warn(String message) {
		log(LogLevel.WARN, message);
	}
	
	/**
	 * Logs a text message and additional objects at {@link LogLevel#WARN}. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#WARN} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param format The message text that describes how to format the objects
	 * @param objects Additional objects that should be logged
	 */
	public void warn(String format, Object...objects) {
		log(LogLevel.WARN, format, objects);
	}
	
	/**
	 * Logs a text message and a {@link Throwable} at {@link LogLevel#WARN}.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#WARN} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param message The message text
	 * @param throwable The throwable that should be logged
	 */
	public void warn(String message, Throwable throwable) {
		log(LogLevel.WARN, message, throwable);
	}
	
	/**
	 * Logs a text message, a throwable and additional objects at {@link LogLevel#WARN}. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#WARN} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param format The message text that describes how to format the objects
	 * @param throwable The throwable that should be logged
	 * @param objects Additional objects that should be logged
	 */
	public void warn(String format, Throwable throwable, Object...objects) {
		log(LogLevel.WARN, format, throwable, objects);
	}
	
	//////////////////////////////////////ERROR///////////////////////////////////
	
	/**
	 * Logs a text message at {@link LogLevel#ERROR}.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#ERROR} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param message The message text
	 */
	public void error(String message) {
		log(LogLevel.ERROR, message);
	}
	
	/**
	 * Logs a text message and additional objects at {@link LogLevel#ERROR}. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#ERROR} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param format The message text that describes how to format the objects
	 * @param objects Additional objects that should be logged
	 */
	public void error(String format, Object...objects) {
		log(LogLevel.ERROR, format, objects);
	}
	
	/**
	 * Logs a text message and a {@link Throwable} at {@link LogLevel#ERROR}.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#ERROR} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param message The message text
	 * @param throwable The throwable that should be logged
	 */
	public void error(String message, Throwable throwable) {
		log(LogLevel.ERROR, message, throwable);
	}
	
	/**
	 * Logs a text message, a throwable and additional objects at {@link LogLevel#ERROR}. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * The message is only logged if this logger's minimal logging level ({@link #getMinimalLevel()})
	 * is {@link LogLevel#ERROR} or below. The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param format The message text that describes how to format the objects
	 * @param throwable The throwable that should be logged
	 * @param objects Additional objects that should be logged
	 */
	public void error(String format, Throwable throwable, Object...objects) {
		log(LogLevel.ERROR, format, throwable, objects);
	}

	//////////////////////////////////////FATAL//////////////////////////////////////
	
	/**
	 * Logs a text message at {@link LogLevel#ERROR}.<br>
	 * A fatal error usually means that the program will exit because of this error.<br>
	 * The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param message The message text
	 */
	public void fatal(String message) {
		log(LogLevel.FATAL, message);
	}
	
	/**
	 * Logs a text message and additional objects at {@link LogLevel#FATAL}. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * A fatal error usually means that the program will exit because of this error.<br>
	 * The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * @param format The message text that describes how to format the objects
	 * @param objects Additional objects that should be logged
	 */
	public void fatal(String format, Object...objects) {
		log(LogLevel.FATAL, format, objects);
	}
	
	/**
	 * Logs a text message and a {@link Throwable} at {@link LogLevel#FATAL}.<br>
	 * A fatal error usually means that the program will exit because of this error.<br>
	 * The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param message The message text
	 * @param throwable The throwable that should be logged
	 */
	public void fatal(String message, Throwable throwable) {
		log(LogLevel.FATAL, message, throwable);
	}
	
	/**
	 * Logs a text message, a throwable and additional objects at {@link LogLevel#FATAL}. Both the format string and the objects will be
	 * combined into a single mesage text by the {@link LogMessageFormat}, by default by using the
	 * {@link String#format(String, Object...)} method.<br>
	 * A fatal error usually means that the program will exit because of this error.<br>
	 * The message will be formatted by the
	 * {@link LogMessageFormat} object for this logger before it is passed to the {@link OutputChannel}.
	 * The default formatter will print the stack trace of the throwable below the message.
	 * @param format The message text that describes how to format the objects
	 * @param throwable The throwable that should be logged
	 * @param objects Additional objects that should be logged
	 */
	public void fatal(String format, Throwable throwable, Object...objects) {
		log(LogLevel.FATAL, format, throwable, objects);
	}
	
	////////////////////////////IMPLEMENTATION METHODS////////////////////////// 
	
	protected final void tryLog(LogLevel level, LogMessage message) {
		if(tryImpl(level)) logImpl(message);
	}
	
	protected void logImpl(LogMessage message) {
		channel.appendMessage(message);
	}
	
	protected boolean tryImpl(LogLevel level) {
		return level.isHigherOrEqual(minimalLevel);
	}
}
