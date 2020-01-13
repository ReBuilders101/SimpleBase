package lb.simplebase.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface SynchronizedStateProvider<T> extends Supplier<T>{

	@Override
	@Deprecated
	public default T get() {
		return getState();
	}
	
	public T getState();
	
	public void withStateDo(Consumer<T> action, boolean requireWriteAccess);
	
	public <R> R withStateReturn(Function<T, R> action, boolean requireWriteAccess);
	
}
