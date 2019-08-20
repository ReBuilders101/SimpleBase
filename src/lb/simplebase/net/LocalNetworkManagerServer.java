package lb.simplebase.net;

public class LocalNetworkManagerServer extends CommonServer {

	public LocalNetworkManagerServer(TargetIdentifier localId, int threads) {
		super(localId, threads);
	}

	@Override
	public ServerStateFuture startServer() {
		if(state == ServerState.INITIALIZED) {
			NetworkManager.NET_LOG.info("Server Manager: Starting server...");
			return (ServerStateFuture) ServerStateFuture.create(getServerState(), (f) -> {
				LocalConnectionManager.addServer(this);
				NetworkManager.NET_LOG.info("Server Manager: Server start complete.");
			}).runInSync(); //Not in a new thread
		} else {
			return ServerStateFuture.quickFailed("Server has already been started", state);
		}
	}

	@Override
	public ServerStateFuture stopServer() {
		if(state == ServerState.STOPPED) {
			return ServerStateFuture.quickDone(ServerState.STOPPED);
		} else {
			NetworkManager.NET_LOG.info("Server Manager: Stopping server...");
			return (ServerStateFuture) ServerStateFuture.create(getServerState(), (f) -> {
				LocalConnectionManager.removeServer(this);
				NetworkManager.NET_LOG.info("Server Manager: Server stop complete.");
			}).runInSync(); //Not in a new thread
		}
	}

	@Override
	protected void shutdown() {
		stopServer();
	}

}
