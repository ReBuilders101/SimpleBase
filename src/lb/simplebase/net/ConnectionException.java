package lb.simplebase.net;

abstract class ConnectionException extends Exception {
	private static final long serialVersionUID = 8243246207954895467L;

	private NetworkConnection connection;
	
	protected ConnectionException(String message, NetworkConnection connection) {
		super(message);
		this.connection = connection;
	}
	
	protected NetworkConnection connection() {
		return connection;
	}
	
}
