package lb.simplebase.net;

//Instead of binding Sockets, bind connections here
public final class LocalServerManager {
	
	public static NetworkConnection getLocalConnectionPartner(NetworkConnection connection) throws ConnectionNotConnectedException{
		throw new ConnectionNotConnectedException("Requested local connection partner not found", connection);
	}
	
	public static NetworkConnection waitForLocalConnectionPartner(NetworkConnection connection, int timeout) throws ConnectionNotConnectedException{
		throw new ConnectionNotConnectedException("Requested local connection partner not found", connection);
	}
}
