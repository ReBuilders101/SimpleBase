package lb.simplebase.glcore;

public interface GLBindable extends AutoCloseable {

	public void enable();
	public void disable();
	
	@Override
	@Deprecated //Don't call manually
	public default void close() {
		disable();
	}
	
}
