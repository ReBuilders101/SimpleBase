package lb.simplebase.net;

/**
 * A {@link NetworkManager} that  represents the client side of the application. It only
 * supports one connectiont to the server.
 */
@ClientSide
public class NetworkManagerClient extends NetworkManager{

	NetworkConnection serverConnection;
	TargetIdentifier serverId;
	
	/**
	 * 
	 * @param threadReceiver The {@link PacketReceiver} that will receive incoming {@link Packet}s from all clients on a separate {@link Thread}
	 * @param localId The {@link TargetIdentifier} of the network target represented by this {@link NetworkManagerClient}
	 * @param serverId The {@link TargetIdentifier} of the server that the client should connect to
	 */
	public NetworkManagerClient(PacketReceiver threadReceiver, TargetIdentifier localId, TargetIdentifier serverId) {
		super(threadReceiver, localId);
		this.serverId = serverId;
		serverConnection = NetworkConnection.createConnection(localId, serverId, this);
	}

	/**
	 * Sends a packet to the specified target. The {@link Packet} will not be sent when the {@link TargetIdentifier} does
	 * not equal the {@link TargetIdentifier} of the server, which is available through {@link #getSenderID()}.
	 * @param packet The packet that should be sent
	 * @param id The {@link TargetIdentifier} of the target
	 */
	@Override
	public void sendPacketTo(Packet packet, TargetIdentifier id) {
		if(id.equals(serverId)) {
			sendPacketToServer(packet);
		}
	}
	
	/**
	 * Sends the {@link Packet} to the connected server, and returns whether the packet was sent successfully.
	 * @param packet The Packet that should be sent
	 * @return Whether the {@link Packet} was sent successfully
	 */
	public boolean sendPacketToServer(Packet packet) {
		try {
			serverConnection.sendPacketToTarget(packet);
		} catch (ConnectionStateException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Tries to open the connection to the server.
	 * If the connection is already open, or could not be made, <code>false</code> is returned.
	 * @return Whether the connection was sent successfully
	 */
	public boolean openConnectionToServer() {
		try {
			serverConnection.connect();
		} catch (ConnectionStateException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Whether the connection to the server is open, that means that packets can be sent to the server.
	 * @return Whether the connection to the server is open
	 */
	public boolean isServerConnectionOpen() {
		return serverConnection.isConnectionOpen();
	}
	
	/**
	 * The {@link NetworkConnection} to the server that is used to send packets.
	 * @return The {@link NetworkConnection} to the server
	 */
	public NetworkConnection getServerConnection() {
		return serverConnection;
	}
	
	/**
	 * The {@link ConnectionState} of the connection to the server.
	 * @return The {@link ConnectionState} of the connection to the server
	 */
	public ConnectionState getServerConnectionState() {
		return serverConnection.getState();
	}
	
	/**
	 * The {@link TargetIdentifier} of the server that this {@link NetworkManagerClient} is connected to
	 * @return The {@link TargetIdentifier} of the server
	 */
	public TargetIdentifier getServerId() {
		return serverId;
	}

	/**
	 * do nothing 
	 */
	@Override
	protected void notifyConnectionClosed(NetworkConnection connection) {}

	/**
	 * Closes the connection to the server. After the connection to the server has been closed, no more
	 * {@link Packet}s can be sent through the connection, and this client is removed from the server's list of clients.
	 */
	@Override
	public void close() {
		serverConnection.close();
	}

}
