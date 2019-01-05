package lb.simplebase.net;

/**
 * This exception is thrown whenever the state of a {@link NetworkConnection} does not match the expected or required state.
 * @see NetworkConnection#getState()
 */
public class ConnectionStateException extends Exception{
	private static final long serialVersionUID = 4919427229534949332L;

	private final ConnectionState currentState;
	private final ConnectionState expectedstate;
	private final NetworkConnection connection;
	
	/**
	 * Creates a new {@link ConnectionStateException} with a message, the {@link NetworkConnection} where the exception occurred,
	 * and a {@link Throwable} that initially caused the exception.
	 * @param message The error message for this {@link Exception}
	 * @param cause The {@link Throwable} that caused this exception
	 * @param connection The {@link NetworkConnection} for which the exception occurred
	 * @param expectedState The {@link ConnectionState} that was required to the operation, but was not the state of the {@link NetworkConnection}
	 * @see Exception#Exception(String, Throwable)
	 */
	public ConnectionStateException(String message, Throwable cause, NetworkConnection connection, ConnectionState expectedState) {
		super(message, cause);
		this.connection = connection;
		this.currentState = connection.getState();
		this.expectedstate = expectedState;
	}
	
	/**
	 * Creates a new {@link ConnectionStateException} with a message, the {@link NetworkConnection} where the exception occurred,
	 * without a cause.
	 * @param message The error message for this {@link Exception}
	 * @param connection The {@link NetworkConnection} for which the exception occurred
	 * @param expectedState The {@link ConnectionState} that was required to the operation, but was not the state of the {@link NetworkConnection}
	 * @see Exception#Exception(String, Throwable)
	 */
	public ConnectionStateException(String message, NetworkConnection connection, ConnectionState expectedState) {
		super(message);
		this.connection = connection;
		this.currentState = connection.getState();
		this.expectedstate = expectedState;
	}
	
	/**
	 * The {@link NetworkConnection} that the exception occurred on.
	 * @return The {@link NetworkConnection} that the exception occurred on
	 */
	public NetworkConnection getNetworkConnection() {
		return connection;
	}
	
	/**
	 * The {@link ConnectionState} that the {@link NetworkConnection} had at the time when the exception occurred
	 * (or, more exact, at the time the {@link ConnectionStateException} object was constructed).
	 * @return The {@link ConnectionState} that the {@link NetworkConnection} had at the time when the exception occurred
	 */
	public ConnectionState getConnectionState() {
		return currentState;
	}
	
	/**
	 * 
	 * @return The {@link ConnectionState} that would have been reqired to execute the operation without an exception
	 */
	public ConnectionState getExpectedConnectionState() {
		return expectedstate;
	}
	
	/**
	 * Maybe
	 */
	protected <T extends Throwable> void doAndThrow(Runnable doThis, T throwThis) throws T{
		doThis.run();
		throw throwThis;
	}
}
