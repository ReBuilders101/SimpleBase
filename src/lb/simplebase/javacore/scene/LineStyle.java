package lb.simplebase.javacore.scene;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

/**
 * A {@link LineStyle} describes properties of a drawn line.
 * It contains a {@link Paint} and a {@link Stroke} object.
 * This class is immutable.
 */
public class LineStyle {

	private final Paint  paint;
	private final Stroke stroke;
	
	/**
	 * Creates a {@link LineStyle} with a custom paint
	 * @param paint The {@link Paint} for the line
	 */
	public LineStyle(Paint paint) {
		this(paint, new BasicStroke());
	}
	
	/**
	 * Creates a {@link LineStyle} with a custom stroke
	 * @param stroke The {@link Stroke} for the line
	 */
	public LineStyle(Stroke stroke) {
		this(Color.BLACK, stroke);
	}
	
	/**
	 * Creates a {@link LineStyle} with a custom paint and stroke
	 * @param paint The {@link Paint} for the line
	 * @param stroke The {@link Stroke} for the line
	 */
	public LineStyle(Paint paint, Stroke stroke) {
		this.paint = paint;
		this.stroke = stroke;
	}
	
	/**
	 * The {@link Paint} for this {@link LineStyle}.
	 * @return The {@link Paint} for this {@link LineStyle}
	 */
	public Paint getPaint() {
		return paint;
	}
	
	/**
	 * The {@link Stroke} for this {@link LineStyle}.
	 * @return The {@link Stroke} for this {@link LineStyle}
	 */
	public Stroke getStroke() {
		return stroke;
	}
	
	/**
	 * Creates a new {@link LineStyle} with the same {@link Paint} and a new {@link Stroke}.
	 * @param stroke The new stroke to use
	 * @return The new line style
	 */
	public LineStyle withStroke(Stroke stroke) {
		return new LineStyle(paint, stroke);
	}
	
	/**
	 * Creates a new {@link LineStyle} with the same {@link Stroke} and a new {@link Paint}
	 * @param paint The new paint to use
	 * @return The new line style
	 */
	public LineStyle withPaint(Paint paint) {
		return new LineStyle(paint, stroke);
	}
	
}
