package lb.simplebase.javacore.transitions;

import lb.simplebase.javacore.Utils;

public class DoubleTransition extends TransitionTask<Double>{

	protected final double startValue;
	protected final double endValue;
	
	public DoubleTransition(Transition transition, ProgressUpdater updater, TransitionBehavior behavior, double startValue, double endValue) {
		super(transition, updater, behavior);
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
