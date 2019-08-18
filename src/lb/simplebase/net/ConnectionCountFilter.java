package lb.simplebase.net;

import java.util.function.BiPredicate;

/**
 * A simple filter that can be used in a {@link ServerConfiguration} to limit the amount of clients on the server
 */
public class ConnectionCountFilter implements BiPredicate<NetworkManagerServer, ConnectionInformation>{

	private int connectionLimit;
	
	/**
	 * Creates a new {@link ConnectionCountFilter}
	 * @param connectionLimit The connection count
	 */
	public ConnectionCountFilter(int connectionLimit) {
		this.connectionLimit = connectionLimit;
	}
	
	/**
	 * The method used to check whether a connection should be accepted.
	 * Gets called by the {@link NetworkManagerServer} when a new connection is attempted.
	 * @param server The {@link NetworkManagerServer} that calls this method.
	 * @param info A {@link ConnectionInformation} object with information about the attempted connection. For {@link ConnectionCountFilter}, this may be <code>null</code>
	 */
	@Override
	public boolean test(NetworkManagerServer var1, ConnectionInformation var2) {
		return var1.getCurrentClientCount() < connectionLimit;
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

}
