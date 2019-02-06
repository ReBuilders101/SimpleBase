package lb.simplebase.log;

public class LogMessage {
	
	private LogLevel logLevel;
	private String rawMessage;
	private String formattedMessage;
	
	public LogMessage(LogLevel level, String raw, String formatted) {
		this.logLevel = level;
		this.rawMessage = raw;
		this.formattedMessage = formatted;
	}
	
	public LogMessage(LogLevel level, String text) {
		this(level, text, text);
	}
	
	public LogLevel getLogLevel() {
		return logLevel;
	}
	
	public String getRawMessage() {
		return rawMessage;
	}
	
	public String getFormattedMessage() {
		return formattedMessage;
	}
}
