package lb.simplebase.javacore.transitions;

import java.util.function.Supplier;

import lb.simplebase.javacore.Utils;
import lb.simplebase.util.GetOnce;

public class DoubleSlider extends TransitionTask<Double>{

	protected final double startValue;
	protected final double endValue;
	
	public DoubleSlider(Transition transition, Supplier<ProgressUpdater> updater, TransitionBehavior behavior, double startValue, double endValue) {
		super(transition, new GetOnce<>(updater), behavior);
		this.startValue = startValue;
		this.endValue = endValue;
	}
	
	public DoubleSlider(Transition transition, ProgressUpdater updater, TransitionBehavior behavior, double startValue, double endValue) {
		super(transition, new GetOnce<>(updater), behavior);
		this.startValue = startValue;
		this.endValue = endValue;
	}

	@Override
	public Double getValue() {
		return getDoubleValue();
	}
	
	public double getDoubleValue() {
		return Utils.scale(completionRatio, startValue, endValue, 0, 1);
	}
}
