package lb.simplebase.javacore.scene;

import java.util.function.Consumer;

public class SliderController {

	private final Consumer<Double> valueUpdater; 
	private TransitionTask currentTask;
	
	private boolean autoClear = false;
	private boolean active = false;
	
	public SliderController(Consumer<Double> valueUpdater, TransitionTask inactiveTask) {
		this.valueUpdater = valueUpdater;
		this.currentTask = inactiveTask;
		this.active = false;
	}
	
	public SliderController(Consumer<Double> valueUpdater) {
		this(valueUpdater, null);
		
	}
	
	public void update() {
		if(currentTask != null && active) {
			valueUpdater.accept(currentTask.getNextValue());
			if(autoClear && currentTask.isDone()) currentTask = null;
		}
	}
	
	public void setTaskActive(TransitionTask task) {
		currentTask = task;
		active = true;
	}
	
	public void setActive(boolean value) {
		active = value;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setTaskInactive(TransitionTask task) {
		currentTask = task;
		active = false;
	}
	
	public void removeTask() {
		currentTask = null;
		active = false;
	}
	
	public double getCurrentValue() {
		return currentTask == null ? 0 : currentTask.getCurrentValue();
	}
	
	public TransitionTask getCurrentTask() {
		return currentTask;
	}
	
	public void resetTask() {
		if(currentTask != null) currentTask.reset();
	}
	
	public void setRemoveTaskIfDone(boolean value) {
		autoClear = value;
	}
	
	public boolean getRemoveTaskIfDone() {
		return autoClear;
	}
	
}
