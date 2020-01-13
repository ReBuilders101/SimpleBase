package lb.simplebase.javacore.transitions;

import java.util.function.DoubleFunction;
import java.util.function.Supplier;

import lb.simplebase.javacore.Utils;
import lb.simplebase.util.GetOnce;

public abstract class TransitionTask<T> {

	protected final Transition transition;
	protected final TransitionBehavior behavior;
	protected final GetOnce<ProgressUpdater> update;
	
	protected double completionRatio;
	protected boolean directionReversed;
	
	protected TransitionTask(Transition transition, GetOnce<ProgressUpdater> update, TransitionBehavior behavior) {
		this.transition = transition;
		this.behavior = behavior;
		this.update = update;
		this.directionReversed = false;
		this.completionRatio = 0;
	}
	
	public final Transition getTransitionFunction() {
		return transition;
	}
	
	public final TransitionBehavior getTransitionBehavior() {
		return behavior;
	}
	
	public abstract T getValue();
	
	public final void update() {
		completionRatio = Utils.clamp(update.get().updateProgress(completionRatio, directionReversed));
		if(directionReversed && completionRatio == 0D) { //Arrived at 0, backwards
			completionRatio = behavior.onEndSetRatio(true);
			directionReversed = behavior.onEndSetReverse(true);
		} else if(!directionReversed && completionRatio == 1D) {
			completionRatio = behavior.onEndSetRatio(false);
			directionReversed = behavior.onEndSetReverse(false);
		}
	}
	
	public final void reset() {
		completionRatio = 0;
	}
	
	public final void setCompletionRatio(double ratio) {
		completionRatio = Utils.clamp(ratio);
	}
	
	public final boolean isDone() {
		if(isRepeating()) return false;
		return completionRatio == (directionReversed ? 0D : 1D);
	}
	
	public final boolean isDirectionReversed() {
		return directionReversed;
	}
	
	public final void setDirectionReversed(boolean reversed) {
		this.directionReversed = reversed;
	}
	
	public final boolean isRepeating() {
		return behavior.isRepeating();
	}
	
	public static <T> TransitionTask<T> of(DoubleFunction<T> createValue, Transition transition,
			Supplier<ProgressUpdater> update, TransitionBehavior behavior) {
		return new TransitionTask<T>(transition, new GetOnce<>(update), behavior) {
			@Override public T getValue() {
				return createValue.apply(completionRatio);
			}
		};
	}
	
	public static <T> TransitionTask<T> of(DoubleFunction<T> createValue, Transition transition,
			ProgressUpdater update, TransitionBehavior behavior) {
		return new TransitionTask<T>(transition, new GetOnce<>(update), behavior) {
			@Override public T getValue() {
				return createValue.apply(completionRatio);
			}
		};
	}
}
