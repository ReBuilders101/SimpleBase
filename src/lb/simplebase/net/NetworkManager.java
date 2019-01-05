package lb.simplebase.net;

/**
 * The {@link NetworkManager} handles all {@link NetworkConnection}s in a network for one target.
 * In case if clients, this is only the connection to the server, but in case of a server, there
 * are multiple connections to clients.<br>
 * Because the implementation depends heavily on whether the {@link NetworkManager} represents a server or client,
 * the subclasses {@link NetworkManagerServer} and {@link NetworkManagerClient} should be used.
 */
public abstract class NetworkManager extends PacketThreadReceiver implements PacketSender{
	
	protected NetworkManager(PacketReceiver threadReceiver, TargetIdentifier localId) {
		super(threadReceiver, 500, (r, s) -> System.out.println("Overflow!"), "NetworkManager"); //TODO change sysout to other log //Increase overflow, because multiple packet sources
		local = localId;
	}

	private TargetIdentifier local; //every manager represents one party
	
	/**
	 * Sends a packet to the specified target. Packet sending may be restricted depending on the implementation,
	 * for example a {@link NetworkManagerClient} can only send {@link Packet}s to the connceted server.
	 * Because this method does not allow to return a success indicator or throw a (checked) {@link Exception},
	 * {@link Packet}s that cannot be sent are silently discarded. Both {@link NetworkManagerClient} and {@link NetworkManagerServer}
	 * provide better methods for sending packets to a network target.
	 * @param packet The packet that should be sent
	 * @param id The {@link TargetIdentifier} of the target
	 */
	@Override
	public abstract void sendPacketTo(Packet packet, TargetIdentifier id);

	@Override
	public TargetIdentifier getSenderID() {
		return local;
	}
	
	/**
	 * Closes all contained network connections.
	 */
	public abstract void close();
	
	protected abstract void notifyConnectionClosed(NetworkConnection connection);
}
