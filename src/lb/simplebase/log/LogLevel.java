package lb.simplebase.log;

/**
 * The {@link LogLevel} of a message indicates how important it is, and most {@link Logger}s have a minimal level, which means
 * that messages that are less important than this minimal level will not be logged.<p>
 * The {@link #getLevel()} returns the level / priority of the {@link LogLevel}, where {@link #DEBUG} has the lowest
 * priority with a return value of <code>0</code>, and {@link #FATAL} has the highest priority with a return value of <code>4</code>.<p>
 * <h3>Priority overwiew:</h3>
 * <table border="1" cellpadding="2">
 * <tr> <td>LogLevel       </td> <td>Priority </td> </tr>
 * <tr> <td>{@link #DEBUG} </td> <td>0        </td> </tr>
 * <tr> <td>{@link #INFO}  </td> <td>1        </td> </tr>
 * <tr> <td>{@link #WARN}  </td> <td>2        </td> </tr>
 * <tr> <td>{@link #ERROR} </td> <td>3        </td> </tr>
 * <tr> <td>{@link #FATAL} </td> <td>4        </td> </tr>
 * </table>
 */
public enum LogLevel {

	DEBUG(0, "Debug", 1),
	INFO(1, "Info", 2),
	WARN(2, "Warn", 4),
	ERROR(3, "Error", 8),
	FATAL(4, "Fatal", 16);
	
	private final int level;
	private final String prefix;
	
	
	private LogLevel(int level, String prefix, int flag) {
		this.level = level;
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public int getLevel() {
		return level;
	}
	
	public boolean isHigherOrEqual(LogLevel logLevel) {
		return getLevel() >= logLevel.getLevel(); 
	}
	
	public static LogLevel fromLevel(int level) {
		switch (level) {
		case 0: return DEBUG;
		case 1: return INFO;
		case 2: return WARN;
		case 3: return ERROR;
		case 4: return FATAL;
		default: return null;
		}
	}
	
	public static LogLevel getDefault() {
		return LogLevel.WARN;
	}
}
