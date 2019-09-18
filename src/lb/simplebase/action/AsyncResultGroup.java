package lb.simplebase.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class AsyncResultGroup implements AsyncAction, Iterable<AsyncResult> {

	private final AsyncResult[] results;
	private Collection<Runnable> doneTasks;
	
	public AsyncResultGroup(AsyncResult...results) {
		this.results = results;
		for(AsyncResult result : results) {
			result.addDoneHandler(this::partTaskDoneHandler);
		}
	}
	
	public int getGroupSize() {
		return results.length;
	}

	@Override
	public AsyncAction syncOrError() throws InterruptedException {
		for(AsyncResult result : results) {
			result.syncOrError();
		}
		return this;
	}

	private void partTaskDoneHandler() {
		if(isDone()) {
			doneTasks.forEach((t) -> t.run());
		}
	}

	@Override
	public boolean isDone() {
		for(AsyncResult result : results) {
			if(!result.isDone()) return false;
		}
		return true;
	}

	public int getCurrentSuccessCount() { //All sent and no error
		int count = 0;
		for(AsyncResult result : results) {
			if(result.isSuccess()) count++;
		}
		return count;
	}
	
	public int getCurrentDoneCount() {
		int count = 0;
		for(AsyncResult result : results) {
			if(result.isDone()) count++;
		}
		return count;
	}
	
	public int getCurrentFailureCount() {
		int count = 0;
		for(AsyncResult result : results) {
			if(result.isFailed()) count++;
		}
		return count;
	}

	@Override
	public void addDoneHandler(Runnable handler) {
		if(doneTasks == null) doneTasks = new ArrayList<>();
		doneTasks.add(handler);
	}

	@Override
	public Iterator<AsyncResult> iterator() {
		return Arrays.stream(results).iterator();
	}
	
	
	
}
