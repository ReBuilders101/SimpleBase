package lb.simplebase.net;

//Instead of binding Sockets, bind connections here
public final class LocalServerManager {
	
	public static LocalNetworkConnection getLocalConnectionPartner(NetworkConnection connection) throws ConnectionStateException{
		throw new Exception("Requested local connection partner not found");
	}
	
	public static LocalNetworkConnection waitForLocalConnectionPartner(NetworkConnection connection, int timeout) throws ConnectionStateException{
		throw new Exception("Requested local connection partner not found");
	}
}
