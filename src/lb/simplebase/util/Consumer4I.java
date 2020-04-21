package lb.simplebase.util;

@FunctionalInterface
public interface Consumer4I<T> {

	public void accept(T t, int a, int b, int c, int d);
	
}
