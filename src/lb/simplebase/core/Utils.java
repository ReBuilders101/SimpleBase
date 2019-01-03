package lb.simplebase.core;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Collector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import sun.awt.image.ToolkitImage;

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
	
	public static JPanel getGroupBox(String title) {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(title));
		return panel;
	}
	
	/**
	 * Scales a {@link BufferedImage} to a certain size. This method uses the {@link BufferedImage#getScaledInstance(int, int, int)}-Method, 
	 * but returns a BufferedImage instead of a {@link ToolkitImage}
	 * @param image The image to be scaled or resized
	 * @param newWidth The width of the new image
	 * @param newHeight The height of the new image
	 * @param hints Hint flags form the {@link Image} class used for scaling
	 * @return The scaled image
	 */
	public static BufferedImage scaleImage(Image image, int newWidth, int newHeight, int hints) {
		Image scaledImage = image.getScaledInstance(newWidth, newHeight, hints);
		BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		newImage.createGraphics().drawImage(scaledImage, 0, 0, newWidth, newHeight, null);
		return newImage;
	}
	
	/**
	 * Calculates the font size (in pt) for a text to be x pixels high
	 * @param pixelSize The height of the text in pixels
	 * @return The correct font size
	 */
	public static float getFontSize(float pixelSize){
		return (float) (pixelSize * Toolkit.getDefaultToolkit().getScreenResolution() / 72.0);
	}
	
	/**
	 * Calculates the pixel size for a text that has the font size x
	 * @param fontSize The height of the text as a font size
	 * @return The correct pixel size
	 */
	public static float getPixelSize(float fontSize){
		return (float) (fontSize * 72.0 / Toolkit.getDefaultToolkit().getScreenResolution());
	}
	
	/**
	 * A collector that converts all stream elements to Strings by their toString() method, and then appends the strings
	 * @param <T> The type of Stram that this collector will operate on
	 * @return A collector that converts all stream elements to Strings by their toString() method, and then appends the strings 
	 */
	public static <T> Collector<T, ?, String> collectString() {
		return Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString);
	}
	
}
