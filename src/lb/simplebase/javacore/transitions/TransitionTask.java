package lb.simplebase.javacore.transitions;

import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import lb.simplebase.javacore.TickUpdate;
import lb.simplebase.javacore.Utils;

public abstract class TransitionTask<T> implements TickUpdate{

	protected final Transition transition;
	protected final TransitionBehavior behavior;
	protected final ProgressUpdater update;
	
	protected double completionRatio;
	protected boolean directionReversed;
	private boolean unticked;
	private boolean active;
	
	protected TransitionTask(Transition transition, ProgressUpdater update, TransitionBehavior behavior) {
		this.transition = transition;
		this.behavior = behavior;
		this.update = update;
		this.directionReversed = false;
		this.completionRatio = 0;
		this.unticked = true;
		this.active = true;
	}
	
	public final Transition getTransitionFunction() {
		return transition;
	}
	
	public final TransitionBehavior getTransitionBehavior() {
		return behavior;
	}
	
	public abstract T getValue();
	
	@Override
	public final void update() {
		if(!active) return;
		if(unticked) reset();
		completionRatio = Utils.clamp(update.updateProgress(completionRatio, directionReversed));
		if(directionReversed && completionRatio == 0D) { //Arrived at 0, backwards
			completionRatio = behavior.onEndSetRatio(true);
			directionReversed = behavior.onEndSetReverse(true);
			if(!behavior.isRepeating()) setActive(false); //Deactivate for tasks that are executed once
		} else if(!directionReversed && completionRatio == 1D) {
			completionRatio = behavior.onEndSetRatio(false);
			directionReversed = behavior.onEndSetReverse(false);
			if(!behavior.isRepeating()) setActive(false); //Deactivate for tasks that are executed once
		}
		unticked = false;
	}
	
	public final void reset() {
		completionRatio = 0;
		unticked = true;
		update.reset();
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
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
			ProgressUpdater update, TransitionBehavior behavior) {
		return new TransitionTask<T>(transition, update, behavior) {
			@Override public T getValue() {
				return createValue.apply(completionRatio);
			}
		};
	}
	
	public static <T> TickUpdate withSetter(TransitionTask<T> task, Consumer<T> setter) {
		return new TickUpdate() {
			
			@Override
			public void update() {
				task.update();
				setter.accept(task.getValue());
			}
		};
	}
}
