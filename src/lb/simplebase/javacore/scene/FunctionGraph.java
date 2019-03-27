package lb.simplebase.javacore.scene;

import java.awt.Graphics2D;
import java.util.function.DoubleFunction;

public abstract class FunctionGraph implements RangedDrawable{

	private int unitStep;
	private LineStyle paint;
	private boolean enabled;
	private FunctionGraphStyle style;
	
	protected FunctionGraph(FunctionGraphStyle style, LineStyle paint, int unitStep) {
		this.unitStep = unitStep;
		this.style = style;
		this.paint = paint;
		this.enabled = true;
	}
	
	public static FunctionGraph fromFunction(FunctionGraphStyle style, LineStyle paint, int unitStep, DoubleFunction<Double> func) {
		return new FunctionalFunctionGraph(style, paint, unitStep, func);
	}
	
	public abstract double getYValue(double xValue);

	@Override
	public void draw(Graphics2D g2d, int width, int height, double originXoffset, double originYoffset, double spanXunits, double spanYunits) {
		if(!enabled) return;
		
		final double unit2pixelX = width / spanXunits;
		final double unit2pixelY = height / spanYunits;
		
		//The left  / lower border of the visible area, in units
		final double minXunits = -((spanXunits / 2) + originXoffset);
//		final double minYunits = -((spanYunits / 2) + originYoffset);	//Not needed
		//The right / upper border of the visible area, in units
		final double maxXunits = (spanXunits / 2) - originXoffset;
//		final double maxYunits = (spanYunits / 2) - originYoffset;		//Not needed
		
		int lastPosX = 0, lastPosY = 0;
		final double localUnitStep = unitStep <= 0 ? 1D / unit2pixelX : unitStep; //if no step set, use size of one pixel in units
		
		double startXval = minXunits;
		if(style == FunctionGraphStyle.LINE) {
			lastPosX = (int) (minXunits * unit2pixelX);
			lastPosY = (int) (getYValue(minXunits) * unit2pixelY);
			startXval += localUnitStep;
		}
		
		g2d.setPaint(paint.getPaint());
		g2d.setStroke(paint.getStroke());
		
		for(double x = startXval; x <= maxXunits; x += localUnitStep) {
			//Draw at x units
			int xPx = (int) (x * unit2pixelX);
			int yPx = (int) (getYValue(x) * unit2pixelY);

			if(style == FunctionGraphStyle.LINE) {
				g2d.drawLine(lastPosX, lastPosY, xPx, yPx);
				lastPosX = xPx;
				lastPosY = yPx;
			} else { //Draw a dot
				g2d.fillOval(xPx-1, yPx-1, 2, 2);
			}
		}
	}

	public int getUnitStep() {
		return unitStep;
	}

	public void setUnitStep(int unitStep) {
		this.unitStep = unitStep;
	}

	public LineStyle getLineStyle() {
		return paint;
	}

	public void setLineStyle(LineStyle paint) {
		this.paint = paint;
	}

	public FunctionGraphStyle getStyle() {
		return style;
	}

	public void setStyle(FunctionGraphStyle style) {
		this.style = style;
	}
	
	private static class FunctionalFunctionGraph extends FunctionGraph {

		private final DoubleFunction<Double> func;
		
		protected FunctionalFunctionGraph(FunctionGraphStyle style, LineStyle paint, int unitStep, DoubleFunction<Double> func) {
			super(style, paint, unitStep);
			this.func = func;
		}

		@Override
		public double getYValue(double xValue) {
			return func.apply(xValue);
		}
		
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
