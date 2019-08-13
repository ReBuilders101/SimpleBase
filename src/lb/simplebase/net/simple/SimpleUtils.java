package lb.simplebase.net.simple;

import lb.simplebase.net.FutureState;
import lb.simplebase.net.NetworkManager;

public final class SimpleUtils {

	private SimpleUtils() {}
	
	public static void setup() {
		NetworkManager.fixLogLevel();
		FutureState.RUN_ASYNC = false;
	}
	
	public static void cleanup() {
		NetworkManager.cleanUpAndExit();
	}
	
}
