package lb.simplebase.net;

/**
 * A client side manager for a single network session.
 * Used to send packets to a server and to handle incoming packets
 */
@ClientSide
public interface NetworkManagerClient extends NetworkManagerCommon{
	
	/**
	 * Sends a packet to a server.
	 * @param packet The packet to send
	 * @param target The target server. Normally, a client can only send data to a single server in one session, which must match the target parameter
	 * @return Information about the sending process, which is updated asynchounously.
	 */
	public default PacketSendFuture sendPacketTo(Packet packet, TargetIdentifier target) {
		if(target.equals(getServerIndentifier())) {
			return sendPacketToServer(packet);
		} else {
			return PacketSendFuture.quickFailed("Target id does not match server id");
		}
	}
	
	/**
	 * Sends a packet to a server. Uses the default server for this session.
	 * @param packet The packet to send
	 * @return Information about the sending process, which is updated asynchounously.
	 */
	public PacketSendFuture sendPacketToServer(Packet packet);
	
	/**
	 * @return {@link ConnectionState} of the connection to the server
	 */
	public ConnectionState getConnectionState();
	
	/**
	 * Opens the connection to the server. No data can be sent or received before the connection has been opened
	 * @return Information about the connection.
	 */
	public ConnectionStateFuture openConnectionToServer();
	
	/**
	 * Closes the connection to the server. No data can be sent or received after the connection has been closed
	 * @return Information about the connection.
	 */
	public ConnectionStateFuture closeConnectionToServer();
	
	public void onConnectionClosed(Runnable task);
	
	/**
	 * @return The {@link TargetIdentifier} for the server that this client connects to
	 */
	public TargetIdentifier getServerIndentifier();
}
