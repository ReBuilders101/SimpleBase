package lb.simplebase.javacore.scene;

import java.awt.Graphics2D;
import java.awt.Paint;

import lb.simplebase.javacore.Framework;

public class Grid implements RangedDrawable{
	
	private GridLineStyle style;
	private Paint paint;
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
		//Distance between the grid lines, in pixels
//		final double lineDistXpx = lineDistX * unit2pixelX;
//		final double lineDistYpx = lineDistY * unit2pixelY;
		
//		double offsetXpx = (minXunits % lineDistX) * unit2pixelX;
//		final int lineXnum = (int) (spanXunits / lineDistX);
//		//don't draw edge lines
//		if(offsetXpx <= 0) {
//			offsetXpx += lineDistXpx;
//		}
		
//		double offsetYpx = (minYunits % lineDistY) * unit2pixelY;
//		final int lineYnum = (int) (spanYunits / lineDistY);
//		if(offsetYpx <= 0) {
//			offsetYpx += lineDistYpx;
//		}

		final int attributePx = Framework.getAttributePx(attribute);
		
		g2d.setPaint(paint);
		
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
//		
//		if(style == GridLineStyle.AXIS) {//Different from the others
//			int yAxisXpx, xAxisYpx;
//			if(minXunits >= 0) {//Y-achse am linken rand
//				yAxisXpx = 0;
//			} else if(maxXunits <= 0) { //Y-Achse am rechten rand
//				yAxisXpx = width - 1;
//			} else { //Y-Achse mittendrin, -minX in px vom linken rand
//				yAxisXpx = (int) (-minXunits * unitXSizePx);
//			}
//			
//			if(minYunits >= 0) {//Y-achse am oberen rand
//				xAxisYpx = 1;
//			} else if(maxYunits <= 0) { //Y-Achse am unteren rand
//				xAxisYpx = height;
//			} else { //Y-Achse mittendrin, -minY in px vom oberen rand
//				xAxisYpx = (int) (-minYunits * unitYSizePx);
//			}
//			//Draw axis
//			g2d.setPaint(paint);
//			g2d.drawLine(0, height - xAxisYpx, width, height - xAxisYpx);
//			g2d.drawLine(yAxisXpx, 0, yAxisXpx, height);
//			
//			//Draw lines
//			int current;
//			if(lineDistX > 0) {
//				for(int i = 0; i < lineXnum; i++) {
//					current = (int) (offsetXpx + i * lineDistXpx);
//					g2d.drawLine(current, height - xAxisYpx - localAttribute, current, height - xAxisYpx + localAttribute);
//				}
//			}
//
//			if(lineDistY > 0) {
//				for(int i = 0; i < lineYnum; i++) {
//					current = height - (int) (offsetYpx + i * lineDistYpx); //Height - ... flips so y points upwards
//					g2d.drawLine(yAxisXpx - localAttribute, current, yAxisXpx + localAttribute, current);
//				}
//			}
//			return;
//		}
//		
//		g2d.setPaint(paint);
//		
//		//Decide how to draw
//		if(style == GridLineStyle.LINE) {
//			int current;
//			if(lineDistX > 0) {
//				for(int i = 0; i < lineXnum; i++) {
//					current = (int) (offsetXpx + i * lineDistXpx);
//					g2d.drawLine(current, 0, current, height);
//				}
//			}
//
//			if(lineDistY > 0) {
//				for(int i = 0; i < lineYnum; i++) {
//					current = height - (int) (offsetYpx + i * lineDistYpx); //Height - ... flips so y points upwards
//					g2d.drawLine(0, current, width, current);
//				}
//			}
//		} else if(style == GridLineStyle.DOT) {
//			//this time nested loops
//			for(int x = 0; x < lineXnum; x++) {
//				for(int y = 0; y < lineYnum; y++) {
//					g2d.fillOval((int) (offsetXpx + x * lineDistXpx) - (localAttribute / 2), height - (int) (offsetYpx + y * lineDistYpx) - (localAttribute / 2), localAttribute, localAttribute);
//				}
//			}
//		} else if(style == GridLineStyle.CROSS) {
//			for(int x = 0; x < lineXnum; x++) {
//				for(int y = 0; y < lineYnum; y++) {
//					final int currentX = (int) (offsetXpx + x * lineDistXpx);
//					final int currentY = height - (int) (offsetYpx + y * lineDistYpx);
//					g2d.drawLine(currentX, currentY - localAttribute, currentX, currentY + localAttribute); //vertical
//					g2d.drawLine(currentX - localAttribute, currentY, currentX + localAttribute, currentY); //horizontal
//				}
//			}
//		}
		
	}

	public void setStyle(GridLineStyle style) {
		this.style = style;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	
}
