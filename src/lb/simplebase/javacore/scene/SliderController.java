package lb.simplebase.javacore.scene;

import java.util.function.Consumer;

public class SliderController {

	private final Consumer<Double> valueUpdater; 
	private TransitionTask currentTask;
	
	private boolean autoClear = false;
	
	public SliderController(Consumer<Double> valueUpdater) {
		this.valueUpdater = valueUpdater;
	}
	
	public void update() {
		if(currentTask != null) {
			valueUpdater.accept(currentTask.getNextValue());
			if(autoClear && currentTask.isDone()) currentTask = null;
		}
	}
	
	public void setActiveTask(TransitionTask task) {
		currentTask = task;
	}
	
	public void removeActiveTask() {
		currentTask = null;
	}
	
	public void setRemoveTaskIfDone(boolean value) {
		autoClear = value;
	}
	
	public boolean getRemoveTaskIfDone() {
		return autoClear;
	}
	
}
