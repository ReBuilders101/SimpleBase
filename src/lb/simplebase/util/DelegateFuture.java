package lb.simplebase.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class DelegateFuture<V, D> implements Future<D> {

	private final Future<V> parent;
	private final Function<V, D> converter;
	
	public DelegateFuture(final Future<V> delegate, final Function<V, D> converter) {
		this.parent = delegate;
		this.converter = converter;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return parent.cancel(mayInterruptIfRunning);
	}

	@Override
	public D get() throws InterruptedException, ExecutionException {
		return converter.apply(parent.get());
	}

	@Override
	public D get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return converter.apply(parent.get(timeout, unit));
	}

	@Override
	public boolean isCancelled() {
		return parent.isCancelled();
	}

	@Override
	public boolean isDone() {
		return parent.isDone();
	}

}
