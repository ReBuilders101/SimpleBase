package lb.simplebase.javacore.scene;

import java.awt.Graphics2D;
import lb.simplebase.javacore.Framework;

public class Grid implements RangedDrawable{
	
	private GridType style;
	private LineStyle paint;
	private final double lineDistX;
	private final double lineDistY;
	private final int attribute;
	
	public Grid(GridType style, LineStyle paint, double lineDistX, double lineDistY, int attribute) {
		this.style = style;
		this.paint = paint;
		this.lineDistX = lineDistX;
		this.lineDistY = lineDistY;
		this.attribute = attribute;
	}

	public int getSizeAttribute() {
		return attribute;
	}
	
	public LineStyle getLineStyle() {
		return paint;
	}
	
	public double getLineDistanceX() {
		return lineDistX;
	}
	
	public double getLineDistanceY() {
		return lineDistY;
	}
	
	public GridType getType() {
		return style;
	}
	
	public void draw(final Graphics2D g2d, final int width, final int height, final double originXoffset, final double originYoffset, final double spanXunits, final double spanYunits) {
		if(style == null) return;
		if(spanXunits <= 0) throw new IllegalArgumentException("The span in x direction must be larger than 0");
		if(spanYunits <= 0) throw new IllegalArgumentException("The span in y direction must be larger than 0");
		
		final double unit2pixelX = width / spanXunits;
		final double unit2pixelY = height / spanYunits;
		
		//The left  / lower border of the visible area, in units
		final int minXunits = (int) Math.ceil(-((spanXunits / 2) + originXoffset));
		final int minYunits = (int) Math.ceil(-((spanYunits / 2) + originYoffset));
		//The right / upper border of the visible area, in units
		final int maxXunits = (int) Math.floor((spanXunits / 2) - originXoffset);
		final int maxYunits = (int) Math.floor((spanYunits / 2) - originYoffset);
		
		final int attributePx = Framework.getAttributePx(attribute);
		
		g2d.setPaint(paint.getPaint());
		g2d.setStroke(paint.getStroke());
		
		switch (style) {
		case AXIS:
			//Axis lines
			g2d.drawLine(0, -height / 2, 0, height / 2);
			g2d.drawLine(-width / 2, 0, width / 2, 0);
			
			//Also draw the small lines
			int current0;
			//Draw x lines, at unitpos * unit2px
			if(lineDistX > 0) {
				for(int i = minXunits; i <= maxXunits; i++) {
					current0 = (int) (i * unit2pixelX);
					g2d.drawLine(current0, -attributePx, current0, attributePx);
				}
			}

			if(lineDistY > 0) {
				for(int i = minYunits; i <= maxYunits; i++) {
					current0 = (int) (i * unit2pixelY);
					g2d.drawLine(-attributePx, current0, attributePx, current0);
				}
			}
			
			return;
		case CROSS:
			if(lineDistX > 0 && lineDistY > 0 && attributePx > 0) {
				for(int x = minXunits; x <= maxXunits; x++) {
					for(int y = minYunits; y <= maxYunits; y++) {
						g2d.drawLine((int) (x * unit2pixelX) - attributePx, (int) (y * unit2pixelY), (int) (x * unit2pixelX) + attributePx, (int) (y * unit2pixelY));
						g2d.drawLine((int) (x * unit2pixelX), (int) (y * unit2pixelY) - attributePx, (int) (x * unit2pixelX), (int) (y * unit2pixelY) + attributePx);
					}
				}
			}
			return;
		case DOT:
			if(lineDistX > 0 && lineDistY > 0 && attributePx > 0) {
				for(int x = minXunits; x <= maxXunits; x++) {
					for(int y = minYunits; y <= maxYunits; y++) {
						g2d.fillOval((int) (x * unit2pixelX), (int) (y * unit2pixelY), attributePx, attributePx);
					}
				}
			}
			return;
		case LINE:
			int current;
			//Draw x lines, at unitpos * unit2px
			if(lineDistX > 0) {
				for(int i = minXunits; i <= maxXunits; i++) {
					current = (int) (i * unit2pixelX);
					g2d.drawLine(current, -height / 2, current, height / 2);
				}
			}

			if(lineDistY > 0) {
				for(int i = minYunits; i <= maxYunits; i++) {
					current = (int) (i * unit2pixelY);
					g2d.drawLine(-width / 2, current, width / 2, current);
				}
			}
			return;
		default:
			return;
		}
	}

	public void setType(GridType style) {
		this.style = style;
	}

	public void setLineStyle(LineStyle paint) {
		this.paint = paint;
	}
	
}
