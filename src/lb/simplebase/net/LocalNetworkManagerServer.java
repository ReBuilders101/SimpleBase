package lb.simplebase.net;

public class LocalNetworkManagerServer extends CommonServer {

	public LocalNetworkManagerServer(TargetIdentifier localId, int threads) {
		super(localId, threads);
	}

	@Override
	public void startServer() {
		if(state == ServerState.INITIALIZED) {
			NetworkManager.NET_LOG.info("Server Manager: Starting server...");
				LocalConnectionManager.addServer(this);
				NetworkManager.NET_LOG.info("Server Manager: Server start complete.");
		} else {
			NetworkManager.NET_LOG.warn("Server Manager: Server has already been started");
		}
	}

	@Override
	public void stopServer() {
		if(state == ServerState.STOPPED) {
			NetworkManager.NET_LOG.info("Server Manager: Server already stopped");
		} else {
			NetworkManager.NET_LOG.info("Server Manager: Stopping server...");
			LocalConnectionManager.removeServer(this);
			NetworkManager.NET_LOG.info("Server Manager: Server stop complete.");
		}
	}

	@Override
	protected void shutdown() {
		stopServer();
	}

}
