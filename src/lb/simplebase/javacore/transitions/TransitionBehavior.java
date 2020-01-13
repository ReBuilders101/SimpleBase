package lb.simplebase.javacore.transitions;

public enum TransitionBehavior {

	ONCE(false, false, false), REPEATING(true, false, true), REVERSING(true, true, false);
	
	private final boolean isRepeating;
	private final boolean invertReverse;
	private final boolean resetOnEnd;
	
	private TransitionBehavior(boolean isRepeating, boolean invertReverse, boolean resetOnEnd) {
		this.isRepeating = isRepeating;
		this.invertReverse = invertReverse;
		this.resetOnEnd = resetOnEnd;
	}
	
	public boolean isRepeating() {
		return isRepeating;
	}
	
	public boolean onEndSetReverse(boolean reverse) {
		return invertReverse ^ reverse;
	}
	
	public double onEndSetRatio(boolean reverse) {
		return resetOnEnd ^ reverse ? 0D : 1D;
	}
	
}
