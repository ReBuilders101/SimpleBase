package lb.simplebase.net;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link NetworkManager} handles all {@link NetworkConnection}s in a network for one target.
 * In case if clients, this is only the connection to the server, but in case of a server, there
 * are multiple connections to clients.<br>
 * Because the implementation depends heavily on whether the {@link NetworkManager} represents a server or client,
 * the subclasses {@link NetworkManagerServer} and {@link NetworkManagerClient} should be used.
 */
public abstract class NetworkManager extends PacketThreadReceiver implements PacketSender, PacketIdMappingContainer {
	
	private Set<PacketIdMapping> mappings;
	
	protected NetworkManager(PacketReceiver threadReceiver, TargetIdentifier localId, boolean singleThread) {
		super(threadReceiver, singleThread); //only one thread
		local = localId;
		mappings = new HashSet<>();
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

	@Override
	public Set<PacketIdMapping> getAllMappings() {
		return Collections.unmodifiableSet(mappings);
	}

	/**
	 * Adds a new {@link PacketIdMapping} to the list of mappings
	 * @param mapping The {@link PacketIdMapping} to add
	 */
	public void addMapping(PacketIdMapping mapping) {
		mappings.add(mapping);
	}
	
	/**
	 * Adds all {@link PacketIdMapping}s that are contained in an <code>enum</code>-Structure (which also extends {@link PacketIdMapping}). 
	 * @param <T> The type of the <code>enum</code>-implementation
	 * @param e The {@link Enum} containing all mappings
	 */
	public <T extends Enum<T> & PacketIdMapping> void addMappings(Class<T> e) {
		EnumSet<T> es = EnumSet.allOf(e);
		mappings.addAll(es);
	}
	
	/**
	 * Adds all {@link PacketIdMapping}s from another container
	 * @param con The {@link PacketIdMappingContainer} containing all Mappings
	 */
	public void addAllMappings(PacketIdMappingContainer con) {
		mappings.addAll(con.getAllMappings());
	}
}
