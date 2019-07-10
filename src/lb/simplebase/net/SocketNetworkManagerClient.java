package lb.simplebase.net;

/**
 * A {@link NetworkManager} that  represents the client side of the application. It only
 * supports one connectiont to the server.
 */
@ClientSide
public class SocketNetworkManagerClient extends NetworkManager implements NetworkManagerClient{

	AbstractNetworkConnection serverConnection;
	TargetIdentifier serverId;
	PacketDistributor allHandlers;
	
	/**
	 * 
	 * @param threadReceiver The {@link PacketReceiver} that will receive incoming {@link Packet}s from all clients on a separate {@link Thread}
	 * @param localId The {@link TargetIdentifier} of the network target represented by this {@link SocketNetworkManagerClient}
	 * @param serverId The {@link TargetIdentifier} of the server that the client should connect to
	 */
	public SocketNetworkManagerClient(TargetIdentifier localId, TargetIdentifier serverId) {
		super(localId);
		this.serverId = serverId;
		serverConnection = AbstractNetworkConnection.createConnection(localId, serverId, this);
		allHandlers = new PacketDistributor();
	}

	/**
	 * Sends a packet to the specified target. The {@link Packet} will not be sent when the {@link TargetIdentifier} does
	 * not equal the {@link TargetIdentifier} of the server, which is available through {@link #getSenderID()}.
	 * @param packet The packet that should be sent
	 * @param id The {@link TargetIdentifier} of the target
	 */
	@Override
	public PacketSendFuture sendPacketTo(Packet packet, TargetIdentifier id) {
		if(id.equals(serverId)) {
			return sendPacketToServer(packet);
		} else {
			return PacketSendFuture.quickFailed("Target id does not match server id");
		}
	}
	
	/**
	 * Sends the {@link Packet} to the connected server, and returns whether the packet was sent successfully.
	 * @param packet The Packet that should be sent
	 * @return Whether the {@link Packet} was sent successfully
	 */
	public PacketSendFuture sendPacketToServer(Packet packet) {
		if(!serverConnection.isConnectionOpen()) return PacketSendFuture.quickFailed("Connection to server is not open");
		return serverConnection.sendPacketToTarget(packet);
	}
	
	/**
	 * Tries to open the connection to the server.
	 * If the connection is already open, or could not be made, <code>false</code> is returned.
	 * @return Whether the connection was opened successfully
	 */
	public ConnectionStateFuture openConnectionToServer() {
		NetworkManager.NET_LOG.info("Client Manager: Connecting to server (" + serverId.getConnectionAddress() +")...");
		return serverConnection.connect();
	}
	
	/**
	 * The {@link AbstractNetworkConnection} to the server that is used to send packets.
	 * @return The {@link AbstractNetworkConnection} to the server
	 */
	public AbstractNetworkConnection getServerConnection() {
		return serverConnection;
	}

	/**
	 * do nothing 
	 */
	@Override
	public void notifyConnectionClosed(AbstractNetworkConnection connection) {}

	@Override
	public ConnectionState getConnectionState() {
		return serverConnection.getState();
	}

	@Override
	public ConnectionStateFuture closeConnectionToServer() {
		if(serverConnection.state == ConnectionState.CLOSED) return ConnectionStateFuture.quickDone(ConnectionState.CLOSED, ConnectionState.CLOSED);
		NetworkManager.NET_LOG.info("Client Manager: Closing server connection...");
		return serverConnection.close();
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
	public void processPacket(Packet received, TargetIdentifier source) {
		allHandlers.processPacket(received, source);
	}

	@Override
	protected void shutdown() {
		closeConnectionToServer();
	}

}
