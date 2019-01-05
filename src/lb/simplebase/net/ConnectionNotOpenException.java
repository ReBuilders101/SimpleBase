package lb.simplebase.net;

public class ConnectionNotOpenException extends ConnectionException {
	private static final long serialVersionUID = -2288236551129532220L;
	
	public ConnectionNotOpenException(String message, NetworkConnection connection) {
		super(message, connection);
	}
	
	public NetworkConnection getClosedConnection() {
		return connection();
	}
	
}
