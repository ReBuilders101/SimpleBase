package lb.simplebase.javacore.transitions;

import java.util.function.Function;

import lb.simplebase.javacore.Utils;

@FunctionalInterface
public interface Transition {
	
	public static final Transition LINEAR = x -> x;
	
	public static final Transition EASE_IN_QUAD = x -> x * x;
	public static final Transition EASE_OUT_QUAD = x -> x * (2 - x);
	public static final Transition EASE_IN_OUT_QUAD = x -> x < 0.5 ? 2 * x * x : -1 + (4 - 2 * x) * x;
	
	public static final Transition EASE_IN_CUBIC = x -> x * x * x;
	public static final Transition EASE_OUT_CUBIC = x -> (x - 1) * (x - 1) * (x - 1) + 1;
	public static final Transition EASE_IN_OUT_CUBIC = x -> x < 0.5 ? 4 * x * x * x : (x - 1) * (2 * x - 2) * (2 * x - 2) + 1;
	
	public double getValue(double time);
	
	public static Transition fromFunction(Function<Double, Double> func) {
		return (d) -> func.apply(d);
	}
	
	@Deprecated
	public static double apply(Transition trans, double from, double to, double totalTime, double time) {
		return trans.apply(from, to, totalTime, time);
	}
	
	@Deprecated
	public default double apply(double from, double to, double totalTime, double time) {
		final double relativeTime = Utils.scale(time, 0, 1, 0, totalTime);
		final double relativeAmount = getValue(relativeTime);
		return Utils.scale(relativeAmount, from, to, 0, 1);
	}
}
