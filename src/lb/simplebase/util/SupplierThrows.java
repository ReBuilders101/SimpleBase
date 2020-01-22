package lb.simplebase.util;

@FunctionalInterface
public interface SupplierThrows<T,E extends Throwable> {

	public T get() throws E;
	
}
