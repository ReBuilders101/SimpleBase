package lb.simplebase.function;

@FunctionalInterface
public interface TriConsumer<T,S,U> {

	public void accept(T t, S s, U u);
	
}
