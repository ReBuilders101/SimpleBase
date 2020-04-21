package lb.simplebase.glcore.oop;

@Deprecated
public interface GLBindable extends AutoCloseable {

	public void enable();
	public void disable();
	
	public default GLBindable use() {
		enable();
		return this;
	}
	
	@Override
	@Deprecated //Don't call manually
	public default void close() {
		disable();
	}
	
}
