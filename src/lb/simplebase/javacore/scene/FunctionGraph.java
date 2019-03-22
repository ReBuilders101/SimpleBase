package lb.simplebase.javacore.scene;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.function.DoubleFunction;

public abstract class FunctionGraph implements RangedDrawable{

	private int unitStep;
	private Paint paint;
	private boolean enabled;
	private FunctionGraphStyle style;
	
	protected FunctionGraph(FunctionGraphStyle style, Paint paint, int unitStep, boolean initiallyEnabled) {
		this.unitStep = unitStep;
		this.style = style;
		this.paint = paint;
		this.enabled = initiallyEnabled;
	}
	
	public static FunctionGraph fromFunction(FunctionGraphStyle style, Paint paint, int unitStep, DoubleFunction<Double> func, boolean initiallyEnabled) {
		return new FunctionalFunctionGraph(style, paint, unitStep, func, initiallyEnabled);
	}
	
	public abstract double getYValue(double xValue);

	@Override
	public void draw(Graphics2D g2d, int width, int height, double minXunits, double minYunits, double maxXunits, double maxYunits) {
		if(!enabled) return;
		final double spanXunits = maxXunits - minXunits;
		final double spanYunits = maxYunits - minYunits;
		final double unitXSizePx = width  / spanXunits;
		final double unitYSizePx = height / spanYunits;
		
		final double localUnitStep = unitStep <= 0 ? 1D / unitXSizePx : unitStep; //if no step set, use size of one pixel in units
		
		int lastPosX = 0, lastPosY = 0;
		double startXval = minXunits;
		if(style == FunctionGraphStyle.LINE) {
			lastPosX = (int) ((startXval - minXunits) * unitXSizePx);
			lastPosY = height - (int) ((getYValue(startXval) - minYunits) * unitYSizePx);
			startXval += localUnitStep;
		}
		
		g2d.setPaint(paint);
		
		for(double x = startXval; x <= maxXunits; x += localUnitStep) {
			//Draw at x units
			double y = getYValue(x);
			int xPx = (int) ((x - minXunits) * unitXSizePx);
			int yPx = height - (int) ((y - minYunits) * unitYSizePx); //Flip

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

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
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
		
		protected FunctionalFunctionGraph(FunctionGraphStyle style, Paint paint, int unitStep, DoubleFunction<Double> func, boolean initiallyEnabled) {
			super(style, paint, unitStep, initiallyEnabled);
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
