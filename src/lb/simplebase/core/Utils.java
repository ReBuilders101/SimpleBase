package lb.simplebase.core;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @version 1.2
 * @author LB
 * A collection of utility methods
 */
public final class Utils {
	
	/**
	 * Private constructor. This class has no instance methods.
	 */
	private Utils() {}
	
	/**
	 * Converts the Stacktrace of a {@link Throwable} into a single string
	 * @param t The Throwable to get the Stacktrace from
	 * @return The resulting string
	 */
	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
	
	/**
	 * Tries to set the application look and feel to the one of the current operating system.
	 * @return Whether the change was successful.
	 */
	public static boolean setSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Tries to set the application look and feel to the default java look and feel.
	 * @return Whether the change was successful.
	 */
	public static boolean setJavaLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			return false;
		}
		return true;
	}
}
