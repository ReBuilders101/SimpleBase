package lb.simplebase.javacore.scene;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import lb.simplebase.linalg.Matrix2D;

public class CoordinateSystem {
	
//	private boolean autoCalcX;
	private boolean autoCalcY;
	
	private double originXoffset;
	private double originYoffset;
	private double spanXunits;
	private double spanYunits;
	private AffineTransform transform;
	
	private RangedDrawable gridLayer;
	private RangedDrawable axisLayer;
	private List<RangedDrawable> graphLayer;
	private Paint backgroundPaint;
	
	public CoordinateSystem(Paint background, double originXoffset, double originYoffset, double spanXunits, double spanYunits, Matrix2D transform) {
		this.originXoffset = originXoffset;
		this.originYoffset = originYoffset;
		this.spanXunits = spanXunits;
		this.spanYunits = spanYunits;
		this.transform = transform == null ? new AffineTransform() : transform.getAffineTransform();
		
//		autoCalcX = false;
		autoCalcY = false;
		
		backgroundPaint = background;
		graphLayer = new ArrayList<>();
	}
	
	public CoordinateSystem(Paint background, double xSpan, double ySpan) {
		this(background, 0, 0, xSpan, ySpan, Matrix2D.IDENTITY);
	}
	
	public double getXSpan() {
		return spanXunits;
	}
	
	public double getYSpan() {
		return spanYunits;
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
		if(backgroundPaint != null) g2d.fillRect(x, y, width, height);
		
		final double unit2pixelX = width / spanXunits;
		double localSpanYunits = spanYunits;
		if(autoCalcY) localSpanYunits = height / unit2pixelX;
		final double unit2pixelY = height / localSpanYunits;
//		final AffineTransform translate = AffineTransform.getTranslateInstance(width / 2 + (unit2pixelX * originXoffset), (height / 2 + (unit2pixelY * -originYoffset)));
//		final AffineTransform translateInverse = AffineTransform.getTranslateInstance(-(width / 2 + (unit2pixelX * originXoffset)), -(height / 2 + (unit2pixelY * -originYoffset))); //Because inverting 3D matrices can be expensive, explicitly declare it
		final Graphics2D clipped = (Graphics2D) g2d.create(x, y, width, height);
		
		Shape bounds = new Rectangle2D.Double(0, 0, spanXunits, localSpanYunits);
//		bounds = translate.createTransformedShape(bounds);
		bounds = transform.createTransformedShape(bounds);
//		bounds = translateInverse.createTransformedShape(bounds);
		Rectangle2D newBounds = bounds.getBounds2D();
		final int newWidth = (int) (width * (newBounds.getWidth() / spanXunits));
		final int newHeight = (int) (height * (newBounds.getHeight() / localSpanYunits));
				
		//Apply general transformations
//		clipped.transform(translate); //Move origin to center
		clipped.translate(width / 2 + (unit2pixelX * originXoffset), (height / 2 + (unit2pixelY * -originYoffset)));
		clipped.scale(1, -1);	//Flip y axis
		//Apply special transformations
		clipped.transform(transform);
		
		if(gridLayer != null) gridLayer.draw(clipped, newWidth, newHeight, originXoffset, originYoffset, newBounds.getWidth(), newBounds.getHeight());
		if(axisLayer != null) axisLayer.draw(clipped, newWidth, newHeight, originXoffset, originYoffset, newBounds.getWidth(), newBounds.getHeight());
		graphLayer.forEach((g) ->     g.draw(clipped, newWidth, newHeight, originXoffset, originYoffset, newBounds.getWidth(), newBounds.getHeight()));
		clipped.dispose();
	}
	
	public void setOriginXOffset(double offset) {
		originXoffset = offset;
	}
	
	public void setTransform(AffineTransform transform) {
		this.transform = transform;
	}
	
	public void setTransform(Matrix2D transform) {
		this.transform = transform.getAffineTransform();
	}
	
	public double getOriginXOffset() {
		return originXoffset;
	}
	
	public void setOriginYOffset(double offset) {
		originYoffset = offset;
	}
	
	public double getOriginYOffset() {
		return originYoffset;
	}
	
//	public void setAutoSizeX(boolean value) {
//		autoCalcX = value;
//	}
//	
	public void setAutoSizeY(boolean value) {
		autoCalcY = value;
	}

	public void drawAt(Graphics2D g2d, double x, double y, double width, double height) {
		drawAt(g2d, (int) x, (int) y, (int) width, (int) height); 
	}
	
}
