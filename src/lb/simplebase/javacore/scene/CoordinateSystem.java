package lb.simplebase.javacore.scene;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

public class CoordinateSystem {
	
	private boolean autoCalcX;
	private boolean autoCalcY;
	
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	
	private RangedDrawable gridLayer;
	private RangedDrawable axisLayer;
	private List<RangedDrawable> graphLayer;
	private Paint backgroundPaint;
	
	public CoordinateSystem(Paint background, double xSpan, double ySpan, double originXoffset, double originYoffset) {
		calcX(xSpan, originXoffset);
		calcY(ySpan, originYoffset);
		
		autoCalcX = false;
		autoCalcY = false;
		
		backgroundPaint = background;
		graphLayer = new ArrayList<>();
	}
	
	public CoordinateSystem(Paint background, double xSpan, double ySpan) {
		this(background, xSpan, ySpan, 0, 0);
	}
	
	private void calcX(double span, double offset) {
		minX = -((span / 2) + offset);
		maxX =  ((span / 2) - offset);
	}
	
	private void calcY(double span, double offset) {
		minY = -((span / 2) + offset);
		maxY =  ((span / 2) - offset);
	}
	
	public double getXSpan() {
		return maxX - minX;
	}
	
	public double getYSpan() {
		return maxY - minY;
	}
	
	public void setGrid(RangedDrawable grid) {
		gridLayer = grid;
	}
	
	public void setAxis(RangedDrawable axis) {
		axisLayer = axis;
	}
	
	public void addGraph(RangedDrawable graph) {
		graphLayer.add(graph);
	}
	
	public void drawAt(Graphics2D g2d, int x, int y, int width, int height) {
		g2d.setPaint(backgroundPaint);
		g2d.fillRect(x, y, width, height);
		
		Graphics2D clipped = (Graphics2D) g2d.create(x, y, width, height);
		if(gridLayer != null) gridLayer.draw(clipped, width, height, minX, minY, maxX, maxY);
		if(axisLayer != null) axisLayer.draw(clipped, width, height, minX, minY, maxX, maxY);
		graphLayer.forEach((g) -> g.draw(clipped, width, height, minX, minY, maxX, maxY));
	}

	public double getMinX() {
		return minX;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public double getMaxX() {
		return maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public double getMinY() {
		return minY;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}
	
	public void setOriginXOffset(double offset) {
		calcX(getXSpan(), offset);
	}
	
	public void setOriginYOffset(double offset) {
		calcY(getYSpan(), offset);
	}

	public void drawAt(Graphics2D g2d, double x, double y, double width, double height) {
		drawAt(g2d, (int) x, (int) y, (int) width, (int) height); 
	}
	
}
