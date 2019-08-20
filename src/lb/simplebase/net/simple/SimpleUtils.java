package lb.simplebase.net.simple;

import lb.simplebase.log.LogLevel;
import lb.simplebase.net.NetworkManager;

public final class SimpleUtils {

	private SimpleUtils() {}
	
	public static void setup() {
		NetworkManager.setLogLevel(LogLevel.WARN);
		NetworkManager.setAsyncMode(false);
	}
	
	public static void cleanup() {
		NetworkManager.cleanUpAndExit();
	}
	
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			System.err.println("Waiting interrupted");
			return;
		}
	}
	
}
