package lb.simplebase.javacore.scene;

import lb.simplebase.javacore.Utils;

public class TransitionTask {

	private final Transition trans;
	private final double totalTime;
	private final double startValue;
	private final double endValue;
	private final double timeStep;
	
	private double currentTime;
	private boolean done;
	
	public TransitionTask(Transition trans, double totalTime, double startValue, double endValue, double timeStep) {
		this.trans = trans;
		this.totalTime = totalTime;
		this.startValue = startValue;
		this.endValue = endValue;
		this.timeStep = timeStep;
		this.currentTime = 0;
		this.done = false;
	}
	
	public double getNextValue() {
		skipCurrentValue();
		return getCurrentValue();
	}
	
	public double getCurrentValue() {
		return done ? endValue : trans.apply(startValue, endValue, totalTime, currentTime);
	}
	
	public void skipCurrentValue() {
		currentTime += timeStep;
		if(currentTime >= totalTime) {
			currentTime = totalTime;
			done = true;
		}
	}
	
	public void reset() {
		done = false;
		currentTime = 0;
	}
	
	public double getCompletionRatio() {
		return Utils.scale(currentTime, 0, 1, 0, totalTime);
	}
	
	public boolean isDone() {
		return done;
	}
	
}
