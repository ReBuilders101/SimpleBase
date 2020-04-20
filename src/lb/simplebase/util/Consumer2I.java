package lb.simplebase.util;

@FunctionalInterface
public interface Consumer2I<T> {

	public void accept(T t, int a, int b);
	
}
