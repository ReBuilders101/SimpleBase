package lb.simplebase.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import lb.simplebase.action.AsyncAction.DoneHandler;

public class AsyncResultGroup extends DoneHandler implements Iterable<AsyncResult> {

	private final AsyncResult[] results;
	
	public AsyncResultGroup(AsyncResult...results) {
		super(() -> new ArrayList<>());
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
			runDoneHandlers();
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
	public Iterator<AsyncResult> iterator() {
		return Arrays.stream(results).iterator();
	}
	
	
	
}
