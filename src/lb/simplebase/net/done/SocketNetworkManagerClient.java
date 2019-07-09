package lb.simplebase.net.done;

import lb.simplebase.net.ConnectionState;
import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.Packet;
import lb.simplebase.net.PacketReceiver;
import lb.simplebase.net.TargetIdentifier;
import lb.simplebase.net.todo.ConnectionStateFuture;
import lb.simplebase.net.todo.PacketSendFuture;

/**
 * A {@link NetworkManager} that  represents the client side of the application. It only
 * supports one connectiont to the server.
 */
@ClientSide
public class SocketNetworkManagerClient extends NetworkManager implements NetworkManagerClient{

	AbstractNetworkConnection serverConnection;
	TargetIdentifier serverId;
	
	/**
	 * 
	 * @param threadReceiver The {@link PacketReceiver} that will receive incoming {@link Packet}s from all clients on a separate {@link Thread}
	 * @param localId The {@link TargetIdentifier} of the network target represented by this {@link SocketNetworkManagerClient}
	 * @param serverId The {@link TargetIdentifier} of the server that the client should connect to
	 */
	public SocketNetworkManagerClient(PacketReceiver threadReceiver, TargetIdentifier localId, TargetIdentifier serverId) {
		super(threadReceiver, localId, true);
		this.serverId = serverId;
		serverConnection = AbstractNetworkConnection.createConnection(localId, serverId, this);
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
		return serverConnection.close();
	}

	@Override
	public TargetIdentifier getServerIndentifier() {
		return serverId;
	}

}
