package lb.simplebase.util;

import java.io.Closeable;

/**
 * A {@link Closeable} that will never throw an exception when closing
 */
public interface SilentCloseable extends Closeable{

	@Override
	public void close();

}
