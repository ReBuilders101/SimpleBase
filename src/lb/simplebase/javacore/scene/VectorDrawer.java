package lb.simplebase.javacore.scene;

import java.awt.Graphics2D;
import java.awt.Paint;

import lb.simplebase.javacore.Framework;
import lb.simplebase.linalg.Vector2D;

public class VectorDrawer implements RangedDrawable {
	
	private double xStart;
	private double yStart;
	private double xEnd;
	private double yEnd;
	
	private boolean drawArrow;
	private double arrow1angle;
	private double arrow2angle;
	
	private int attribute;
	private LineStyle paint;
	private boolean enabled;
	
	public VectorDrawer(LineStyle paint, Vector2D toDraw, double anchorX, double anchorY, int attribute, boolean drawArrow) {
		this(paint, toDraw.getX(), toDraw.getY(), anchorX, anchorY, attribute, drawArrow);
	}
	
	public VectorDrawer(LineStyle paint, double toDrawX, double toDrawY, double anchorX, double anchorY, int attribute, boolean drawArrow) {
		xStart = anchorX;
		yStart = anchorY;
		xEnd = anchorX + toDrawX;
		yEnd = anchorY + toDrawY;
		enabled = true;
		
		this.paint = paint;
		this.attribute = attribute;
		this.drawArrow = drawArrow;
		
		calcArrow();
	}
	
	public VectorDrawer(LineStyle paint, Vector2D toDraw, int attribute, boolean drawArrow) {
		this(paint, toDraw, 0, 0, attribute, drawArrow);
	}
	
	public VectorDrawer(LineStyle paint, double toDrawX, double toDrawY, int attribute, boolean drawArrow) {
		this(paint, toDrawX, toDrawY, 0, 0, attribute, drawArrow);
	}

	@Override
	public void draw(Graphics2D g2d, int width, int height, double originXOffset, double originYOffset, double spanXunits, double spanYunits) {
		if(!enabled) return;
		
		final double unit2pixelX = width / spanXunits;
		final double unit2pixelY = height / spanYunits;
		
		g2d.setPaint(paint.getPaint());
		g2d.setStroke(paint.getStroke());
		g2d.drawLine((int) (xStart * unit2pixelX), (int) (yStart * unit2pixelY), (int) (xEnd * unit2pixelX), (int) (yEnd * unit2pixelY));
		
		if(drawArrow) {
			final double localAttribute = Framework.getAttributePx(attribute);
			g2d.drawLine((int) (xEnd * unit2pixelX), (int) (yEnd * unit2pixelY), (int) (Math.cos(arrow1angle) * localAttribute + xEnd * unit2pixelX), (int) (Math.sin(arrow1angle) * localAttribute + yEnd * unit2pixelY));
			g2d.drawLine((int) (xEnd * unit2pixelX), (int) (yEnd * unit2pixelY), (int) (Math.cos(arrow2angle) * localAttribute + xEnd * unit2pixelX), (int) (Math.sin(arrow2angle) * localAttribute + yEnd * unit2pixelY));
		}
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}
	
	public void setLineStyle(LineStyle style) {
		paint = style;
	}
	
	private void calcArrow() {
		final double lineAngle = Math.atan2(yStart - yEnd, xStart - xEnd);
		arrow1angle = lineAngle + Math.PI / 4;
		arrow2angle = lineAngle - Math.PI / 4;
	}
}
