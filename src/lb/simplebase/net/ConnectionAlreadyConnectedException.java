package lb.simplebase.net;

public class ConnectionAlreadyConnectedException extends ConnectionException {

	private static final long serialVersionUID = -4771901811899490887L;

	private NetworkConnection alreadyConnection;
	
	public ConnectionAlreadyConnectedException(String message, NetworkConnection connection) {
		super(message, connection);
	}
	
	public NetworkConnection getExistingConnection() {
		return connection();
	}
	
}
