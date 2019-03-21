package lb.simplebase.javacore.scene;

public class ReversableTransitionTask extends TransitionTask{

	public ReversableTransitionTask(Transition trans, double totalTime, double startValue, double endValue, double timeStep) {
		super(trans, totalTime, startValue, endValue, timeStep);
		isReversed = false;
	}

	private boolean isReversed;
	
	public void reverse() {
		isReversed = !isReversed;
	}
	
	public boolean isReversed() {
		return isReversed;
	}

	@Override
	public void skipCurrentValue() {
		if(isReversed) {
			updateTime(-getTimeStep());
		} else {
			updateTime(getTimeStep());
		}
	}
	
}
