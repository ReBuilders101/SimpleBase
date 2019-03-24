package lb.simplebase.javacore.scene;

import java.awt.Graphics2D;

/**
 * A {@link RangedDrawable} represents an object of large or possibly infinite size that has a graphical representation.
 * The {@link #draw(Graphics2D, int, int, double, double, double, double)} method is used to draw a finite section of this object.
 * However, {@link RangedDrawable}s are not meant to be drawn directly by user code, but by other objects that manage the transformations.
 */
public interface RangedDrawable {
	
	/**
	 * Draws a section of this {@link RangedDrawable}. It is usually valid to assume that the origin is centered on the screen an the y-axis is pointing upwards.
	 * @param g2d The used graphics context. If transforms are applied, they must be reversed before thd method returns. It may not be disposed.
	 * @param width The width of the area to draw in, in pixels
	 * @param height The height of the area to draw in, in pixels
	 * @param originXOffset The amount of logical units that the origin's x coordinate is offset from the center of the screen. A positive offset means a movement of the origin to the right
	 * @param originYOffset The amount of logical units that the origin's y coordinate is offset from the center of the screen. A positive offset means a movement of the origin to the top
	 * @param spanXunits The amount of logical units that the drawn area should be wide 
	 * @param spanYunits The amount of logical units that the drawn area should be high
	 */
	public void draw(Graphics2D g2d, int width, int height, double originXOffset, double originYOffset, double spanXunits, double spanYunits);
	
	/**
	 * Creates a {@link RangedDrawable} that draws two other RangedDrawables in order, in the same range.
	 * @param top The RangedDrawable to draw on top of the other
	 * @param bottom  The RangedDrawable to draw below the other
	 * @return A new {@link RangedDrawable} that draws two others
	 */
	public static RangedDrawable combine(RangedDrawable top, RangedDrawable bottom) {
		return (g2d, width, height, minXunits, minYunits, maxXunits, maxYunits) -> { //The parameter names are not changed, but they work
			bottom.draw(g2d, width, height, minXunits, minYunits, maxXunits, maxYunits);
			top.draw(g2d, width, height, minXunits, minYunits, maxXunits, maxYunits);
		};
	}
	
	/**
	 * Creates a {@link RangedDrawable} that draws three other RangedDrawables in order, in the same range.
	 * @param top The RangedDrawable to draw on top of the other
	 * @param middle The RangedDrawable to draw between the others
	 * @param bottom  The RangedDrawable to draw below the other
	 * @return A new {@link RangedDrawable} that draws two others
	 */
	public static RangedDrawable combine(RangedDrawable top, RangedDrawable middle, RangedDrawable bottom) {
		return (g2d, width, height, minXunits, minYunits, maxXunits, maxYunits) -> { //The parameter names are not changed, but they work
			bottom.draw(g2d, width, height, minXunits, minYunits, maxXunits, maxYunits);
			middle.draw(g2d, width, height, minXunits, minYunits, maxXunits, maxYunits);
			top.draw(g2d, width, height, minXunits, minYunits, maxXunits, maxYunits);
		};
	}
	
}
