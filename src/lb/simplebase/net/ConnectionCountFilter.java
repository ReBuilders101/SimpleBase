package lb.simplebase.net;

import java.util.function.Consumer;

/**
 * A simple filter that can be used as a listener for the {@link AttemptedConnectionEvent} to limit the amount of clients on the server
 */
public class ConnectionCountFilter implements Consumer<AttemptedConnectionEvent>{

	private int connectionLimit;
	
	/**
	 * Creates a new {@link ConnectionCountFilter}
	 * @param connectionLimit The connection count
	 */
	public ConnectionCountFilter(int connectionLimit) {
		this.connectionLimit = connectionLimit;
	}
	
	/**
	 * Sets the max count to a new value
	 * @param newLimit The new value
	 */
	public void setConnectionLimit(int newLimit) {
		connectionLimit = newLimit;
	}
	
	/**
	 * Returns the max connection count
	 * @return The max connection count
	 */
	public int getConnectionLimit() {
		return connectionLimit;
	}

	@Override
	public void accept(AttemptedConnectionEvent var1) {
		if(var1.getServerManager().getCurrentClientCount() >= connectionLimit) var1.tryCancel();
	}

}
