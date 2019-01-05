package lb.simplebase.net;

public class ConnectionNotConnectedException extends ConnectionException {
	
	private static final long serialVersionUID = 8939523414029862107L;
	
	public ConnectionNotConnectedException(String message, NetworkConnection connection) {
		super(message, connection);
	}
	
	public NetworkConnection getUnopenedConnection() {
		return connection();
	}
	
	public TargetIdentifier getConnectionPartner() {
		return connection().getRemoteTargetId();
	}
	
}
