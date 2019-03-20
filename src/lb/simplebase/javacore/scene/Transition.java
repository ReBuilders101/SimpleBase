package lb.simplebase.javacore.scene;

import java.util.function.DoubleFunction;
import java.util.function.Function;

import lb.simplebase.javacore.Utils;

public abstract class Transition {

	public static double apply(Transition trans, double from, double to, double totalTime, double time) {
		final double relativeTime = Utils.scale(time, 0, 1, 0, totalTime);
		final double relativeAmount = trans.getValue(relativeTime);
		return Utils.scale(relativeAmount, from, to, 0, 1);
	}
	
	public static final Transition LINEAR = new FunctionalTransition(x -> x);
	public static final Transition EASE = new CubicTransition(0.42D, 0.58D);
	
	public static Transition fromFunction(DoubleFunction<Double> func) {
		return new FunctionalTransition(func);
	}
	
	public static Transition fromFunction(Function<Double, Double> func) {
		return new FunctionalTransition((d) -> func.apply(d));
	}
	
	public static Transition fromCubicBezier(double p1x, double p2x) {
		return new CubicTransition(p1x, p2x);
	}
	
	public static Transition fromCubicBezier(double x) {
		return new CubicTransition(x, 1 - x);
	}
	
	public abstract double getValue(double time);
	
	private static class FunctionalTransition extends Transition {

		public FunctionalTransition(DoubleFunction<Double> func) {
			this.func = func;
		}
		
		private final DoubleFunction<Double> func;
		
		@Override
		public double getValue(double width) {
			return func.apply(Utils.clamp(width));
		}
		
	}
	
	private static class CubicTransition extends Transition {

		private final double p0;
		private final double p1;
		
		public CubicTransition(double p0, double p1) {
			this.p0 = Utils.clamp(p0);
			this.p1 = Utils.clamp(p1);
		}
		
		@Override
		public double getValue(double width) {
			final double t = Utils.clamp(width);
			final double tm1 = 1 - t;
			return (3 * t * tm1 * tm1 * p0) + (3 * t * t * tm1 * p1) + (t * t * t);
		}
		
	}
}
