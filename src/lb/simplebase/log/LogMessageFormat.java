package lb.simplebase.log;

import java.io.PrintWriter;
import java.io.StringWriter;

public interface LogMessageFormat {

	public LogMessage create(String loggerName, LogLevel level, String message);
	public LogMessage create(String loggerName, LogLevel level, String format, Object[] objects);
	public LogMessage create(String loggerName, LogLevel level, Throwable t, String message);
	public LogMessage create(String loggerName, LogLevel level, Throwable t, String format, Object[] objects);
	
	
	public static LogMessageFormat getDefault() {
		return new LogMessageFormat() {
			
			@Override
			public LogMessage create(String loggerName, LogLevel level, Throwable t, String format, Object[] objects) {
				return create(loggerName, level, t, String.format(format, objects));
			}
			
			@Override
			public LogMessage create(String loggerName, LogLevel level, Throwable t, String message) {
				final String throwable = getStackTraceAsString(t);
				final StringBuilder out = loggerName(loggerName, level.getPrefix(), 0);
				out.append(message).append("\n");
				out.append("==> Stack Trace: ").append(throwable);
				return new LogMessage(level, out.toString());
			}
			
			@Override
			public LogMessage create(String loggerName, LogLevel level, String format, Object[] objects) {
				return create(loggerName, level, String.format(format, objects));
			}
			
			@Override
			public LogMessage create(String loggerName, LogLevel level, String message) {
				final StringBuilder out = loggerName(loggerName, level.getPrefix(), message.length());
				out.append(message);
				return new LogMessage(level, out.toString());
			}
			
			private StringBuilder loggerName(String loggerName, String level, int addCapacity) {
				final StringBuilder out = new StringBuilder(loggerName.length() + 7 + level.length() + (addCapacity > 0 ? addCapacity : 0));
				out.append('[').append(loggerName).append("] [").append(level).append("] ");
				return out;
			}
		};
	}
	
	public static enum ANSIColors {
		
		RESET("\033[0m"),
		
		BLACK ("\033[0;30m"),
		RED   ("\033[0;31m"),
		GREEN ("\033[0;32m"),
		YELLOW("\033[0;33m"),
		BLUE  ("\033[0;34m"),
		PURPLE("\033[0;35m"),
		CYAN  ("\033[0;36m"),
		WHITE ("\033[0;37m"),
		
		BLACK_BOLD ("\033[1;30m"), 
		RED_BOLD   ("\033[1;31m"),
		GREEN_BOLD ("\033[1;32m"),
		YELLOW_BOLD("\033[1;33m"), 
		BLUE_BOLD  ("\033[1;34m"),
		PURPLE_BOLD("\033[1;35m"),
		CYAN_BOLD  ("\033[1;36m"),
		WHITE_BOLD ("\033[1;37m"),
		
		BLACK_UNDERLINE ("\033[4;30m"),
		RED_UNDERLINE   ("\033[4;31m"),
		GREEN_UNDERLINE ("\033[4;32m"),
		YELLOW_UNDERLINE("\033[4;33m"),
		BLUE_UNDERLINE  ("\033[4;34m"),
		PURPLE_UNDERLINE("\033[4;35m"),
		CYAN_UNDERLINE  ("\033[4;36m"),
		WHITE_UNDERLINE ("\033[4;37m"),
		
		BLACK_BACKGROUND ("\033[40m"),
		RED_BACKGROUND   ("\033[41m"),
		GREEN_BACKGROUND ("\033[42m"),
		YELLOW_BACKGROUND("\033[43m"),
		BLUE_BACKGROUND  ("\033[44m"),
		PURPLE_BACKGROUND("\033[45m"),
		CYAN_BACKGROUND  ("\033[46m"),
		WHITE_BACKGROUND ("\033[47m");
		
		private ANSIColors(String code) {
			this.code = code;
		}
		
		private String code;
		
		public String getEscapeCode() {
			return code;
		}
	}
	
	public static String getStackTraceAsString(Throwable throwable) {
		StringWriter writer = new StringWriter();
		PrintWriter print = new PrintWriter(writer);
		throwable.printStackTrace(print);
		return writer.toString();
	}
}
