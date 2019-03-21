package lb.simplebase.javacore.scene;

import java.awt.Graphics2D;
import java.awt.Paint;

public class Grid {
	
	private final GridLineStyle style;
	private final Paint paint;
	private final double lineDistX;
	private final double lineDistY;
	private final int attribute;
	
	public Grid(GridLineStyle style, Paint paint, double lineDistX, double lineDistY, int attribute) {
		this.style = style;
		this.paint = paint;
		this.lineDistX = lineDistX;
		this.lineDistY = lineDistY;
		this.attribute = attribute;
	}

	public int getSizeAttribute() {
		return attribute;
	}
	
	public Paint getGridPaint() {
		return paint;
	}
	
	public double getLineDistanceX() {
		return lineDistX;
	}
	
	public double getLineDistanceY() {
		return lineDistY;
	}
	
	public GridLineStyle getStyle() {
		return style;
	}
	
	public void draw(final Graphics2D g2d, final int width, final int height, final double minXunits, final double minYunits, final double maxXunits, final double maxYunits) {
		if(style == null) return;
		if(style == GridLineStyle.AXIS) {//Different from the others
			
			return;
		}
		
		final double spanXunits = maxXunits - minXunits;
		final double spanYunits = maxYunits - minYunits;
		final double unitXSizePx = width  / spanXunits;
		final double unitYSizePx = height / spanYunits;
		final double lineDistXpx = lineDistX * unitXSizePx;
		final double lineDistYpx = lineDistY * unitYSizePx;
		
		double offsetXpx = (minXunits % lineDistX) * unitXSizePx;
		int lineXnum = (int) (spanXunits / lineDistX);
		//don't draw edge lines
		if(offsetXpx <= 0) {
			offsetXpx += lineDistXpx;
		}
		
		double offsetYpx = (minYunits % lineDistY) * unitYSizePx;
		int lineYnum = (int) (spanYunits / lineDistY);
		if(offsetYpx <= 0) {
			offsetYpx += lineDistYpx;
		}
		
		g2d.setPaint(paint);
		
		//Decide how to draw
		if(style == GridLineStyle.LINE) {
			int current;
			for(int i = 0; i < lineXnum; i++) {
				current = (int) (offsetXpx + i * lineDistXpx);
				g2d.drawLine(current, 0, current, height);
			}

			for(int i = 0; i < lineYnum; i++) {
				current = (int) (offsetYpx + i * lineDistYpx);
				g2d.drawLine(0, current, width, current);
			}
		} else if(style == GridLineStyle.DOT) {
			//this time nested loops
			for(int x = 0; x < lineXnum; x++) {
				for(int y = 0; y < lineYnum; y++) {
					g2d.fillOval((int) (offsetXpx + x * lineDistXpx), (int) (offsetYpx + y * lineDistYpx), attribute, attribute);
				}
			}
		} else if(style == GridLineStyle.CROSS) {
			for(int x = 0; x < lineXnum; x++) {
				for(int y = 0; y < lineYnum; y++) {
					final int currentX = (int) (offsetXpx + x * lineDistXpx);
					final int currentY = (int) (offsetYpx + y * lineDistYpx);
					g2d.drawLine(currentX, currentY - attribute, currentX, currentY + attribute); //vertical
					g2d.drawLine(currentX - attribute, currentY, currentX + attribute, currentY); //horizontal
				}
			}
		}
		
	}
	
}
