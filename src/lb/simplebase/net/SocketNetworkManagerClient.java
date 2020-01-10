package lb.simplebase.net;

import java.util.Objects;

import lb.simplebase.action.AsyncResult;

/**
 * A {@link NetworkManager} that  represents the client side of the application. It only
 * supports one connectiont to the server.
 */
@ClientSide
class SocketNetworkManagerClient extends NetworkManager implements NetworkManagerClient{

	private final NetworkConnection serverConnection; 
	private final TargetIdentifier serverId;
	
	private final PacketDistributor allHandlers;
	private final InboundPacketThreadHandler handler;
	
	/**
	 * 
	 * @param localId The {@link TargetIdentifier} of the network target represented by this {@link SocketNetworkManagerClient}
	 * @param serverId The {@link TargetIdentifier} of the server that the client should connect to
	 * @param config Options for this client
	 */
	protected SocketNetworkManagerClient(TargetIdentifier localId, TargetIdentifier serverId, ClientConfig config) {
		super(localId);
		
		Objects.requireNonNull(localId,  "Local TargetIdentifier must not be null");
		Objects.requireNonNull(serverId, "Server TargetIdentifier must not be null");
		Objects.requireNonNull(config,   "ClientConfig must not be null");
		
		this.serverId = serverId;
		allHandlers = new PacketDistributor();
		handler = new InboundPacketThreadHandler(allHandlers, 0);
		
		if(LocalConnectionManager.hasServer(serverId) || serverId.isLocalOnly()) { //If local is possible, then do it
			serverConnection = new LocalNetworkConnection(localId, serverId, this, false, config.getCustomObject());
		} else {
			serverConnection = new RemoteNetworkConnection(localId, serverId, this, config.configuredSocket(), false, config.getCustomObject());
		}
	}
	
	/**
	 * Sends the {@link Packet} to the connected server, and returns whether the packet was sent successfully.
	 * @param packet The Packet that should be sent
	 * @return Whether the {@link Packet} was sent successfully
	 */
	@Override
	public AsyncResult sendPacketToServer(Packet packet) {
		if(!serverConnection.isConnectionOpen()) return AsyncNetTask.createFailed(null, "Connection to server is not open");
		return serverConnection.sendPacketToTarget(packet);
	}
	
	/**
	 * Tries to open the connection to the server.
	 * If the connection is already open, or could not be made, <code>false</code> is returned.
	 * @return Whether the connection was opened successfully
	 */
	@Override
	public void openConnectionToServer() {
		NetworkManager.NET_LOG.info("Client Manager: Connecting to server (" + serverId.getConnectionAddress() +")");
		serverConnection.connect();
	}
	
	/**
	 * The {@link NetworkConnection} to the server that is used to send packets.
	 * @return The {@link NetworkConnection} to the server
	 */
	public NetworkConnection getServerConnection() {
		return serverConnection;
	}

	@Override
	public ConnectionState getConnectionState() {
		return serverConnection.getState();
	}

	@Override
	public void closeConnectionToServer() {
		if(serverConnection.state == ConnectionState.CLOSED) return;
		NetworkManager.NET_LOG.info("Client Manager: Closing server connection");
		serverConnection.close();
	}

	@Override
	public TargetIdentifier getServerIndentifier() {
		return serverId;
	}

	@Override
	public void addIncomingPacketHandler(PacketReceiver handler) {
		allHandlers.addPacketReceiver(handler);
	}

	@Override
	public void processPacket(Packet received, PacketContext source) {
		handler.processPacket(received, source);
	}

	@Override
	protected void shutdown() {
		closeConnectionToServer();
		handler.shutdownExecutor();
	}

}
