package lb.simplebase.javacore.scene;

import java.util.function.DoubleFunction;
import java.util.function.Function;

import lb.simplebase.javacore.Utils;

public abstract class Transition {

	public static double apply(Transition trans, double from, double to, double totalTime, double time) {
		return trans.apply(from, to, totalTime, time);
	}
	
	public static final Transition LINEAR = new FunctionalTransition(x -> x);
	
	public static final Transition EASE_IN_QUAD = new FunctionalTransition(x -> x * x);
	public static final Transition EASE_OUT_QUAD = new FunctionalTransition(x -> x * (2 - x));
	public static final Transition EASE_IN_OUT_QUAD = new FunctionalTransition(x -> x < 0.5 ? 2 * x * x : -1 + (4 - 2 * x) * x);
	
	public static final Transition EASE_IN_CUBIC = new FunctionalTransition(x -> x * x * x);
	public static final Transition EASE_OUT_CUBIC = new FunctionalTransition(x -> (x - 1) * (x - 1) * (x - 1) + 1);
	public static final Transition EASE_IN_OUT_CUBIC = new FunctionalTransition(x -> x < 0.5 ? 4 * x * x * x : (x - 1) * (2 * x - 2) * (2 * x - 2) + 1);
	
	public static Transition fromFunction(DoubleFunction<Double> func) {
		return new FunctionalTransition(func);
	}
	
	public static Transition fromFunction(Function<Double, Double> func) {
		return new FunctionalTransition((d) -> func.apply(d));
	}
	
	public abstract double getValue(double time);
	
	public double apply(double from, double to, double totalTime, double time) {
		final double relativeTime = Utils.scale(time, 0, 1, 0, totalTime);
		final double relativeAmount = getValue(relativeTime);
		return Utils.scale(relativeAmount, from, to, 0, 1);
	}
	
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
}
