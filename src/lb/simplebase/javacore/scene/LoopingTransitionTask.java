package lb.simplebase.javacore.scene;

import lb.simplebase.javacore.transitions.Transition;

public class LoopingTransitionTask extends ReversableTransitionTask{

	public LoopingTransitionTask(Transition trans, double totalTime, double startValue, double endValue,
			double timeStep, boolean reverseToLoop) {
		super(trans, totalTime, startValue, endValue, timeStep);
		this.reverseToLoop = reverseToLoop;
	}

	private boolean reverseToLoop;

	@Override
	public void skipCurrentValue() {
		super.skipCurrentValue();
		if(isDone()) { //loop
			if(reverseToLoop) {
				reverse();
			} else {
				reset();
			}
		}
	}
	
	
}
