package lb.simplebase.javacore;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Collector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
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
	
	public static JComponent setMaxHeight(JComponent comp) {
		comp.setMaximumSize(new Dimension(comp.getMaximumSize().width, comp.getPreferredSize().height));
		return comp;
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
	
	/**
	 * Clamps a value between 0 and 1
	 * @param value The value to be clamped
	 * @return The clamped value
	 */
	public static double clamp(double value) {
		return clamp(value, 0, 1);
	}
	
	/**
	 * Creates a new {@link Rectangle} that represents the bounds of the transformed rectangle
	 * @param boundsBefore The area to draw before the transformation
	 * @param transform The transformation to apply
	 * @return The new area to draw after the transformation
	 */
	public static Rectangle getDrawArea(Rectangle boundsBefore, AffineTransform transform) {
		return transform.createTransformedShape(boundsBefore).getBounds();
	}
	
	/**
	 * Creates a new {@link Rectangle2D} that represents the bounds of the transformed rectangle
	 * @param boundsBefore The area to draw before the transformation
	 * @param transform The transformation to apply
	 * @return The new area to draw after the transformation
	 */
	public static Rectangle2D getDrawArea(Rectangle2D boundsBefore, AffineTransform transform) {
		return transform.createTransformedShape(boundsBefore).getBounds2D();
	}
	
	/**
	 * Clamps a value between <code>min</code> and <code>max</code>
	 * @param value The value to be clamped
	 * @param min The minimal value
	 * @param max the maximal value
	 * @return The clamped value
	 */
	public static double clamp(double value, double min, double max) {
		if(value > max) return max;
		if(value < min) return min;
		return value;
	}
	
	/**
	 * Clamps a value between min and max by adding or subtracting the interval from the initial
	 * value until it is between min and max.
	 * @param value The value
	 * @param min The minimal value
	 * @param max The maximal value
	 * @return The value between min and max
	 */
	public static double clampCyclic(double value, double min, double max) {
		if(value > max) {
			return clampCyclic(value - (max - min), min, max);
		}else if(value < min) {
			return clampCyclic(value + (max - min), min, max);
		} else {
			return value;
		}
	}
	
	/**
	 * Clamps a radian angle between <code>-pi</code> and <code>pi</code>.
	 * To get an angle between <code>0</code> and <code>2pi</code>, use {@link #clampCyclic(double, double, double)}
	 * or add <code>pi</code> to the result.
	 * @param radianAngle The angle
	 * @return The angle between <code>-pi</code> and <code>pi</code>
	 */
	public static double clampAngle(double radianAngle) {
		return clampCyclic(radianAngle, -Math.PI, Math.PI);
	}
	
	public static double scale(double value, double minOut, double maxOut, double minIn, double maxIn) {
		value = clamp(value, minIn, maxIn);
//		final double size = maxIn - minIn;
//		final double shiftVal = value - minIn;
//		final double unitVal = shiftVal / size;
//		final double outSize = maxOut - minOut;
//		final double outShiftVal = unitVal * outSize;
//		return outShiftVal + minOut;
		return (((value - minIn) / (maxIn - minIn)) * (maxOut - minOut) ) + minOut;
	}
}
