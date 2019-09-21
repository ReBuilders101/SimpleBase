package lb.simplebase.net;

public interface LocalConnectionServer extends NetworkManagerServer{

	public LocalNetworkConnection attemptLocalConnection(LocalNetworkConnection connection);
	
}
