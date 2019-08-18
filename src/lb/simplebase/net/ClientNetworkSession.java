package lb.simplebase.net;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A class that manages the client side in an application and supports connections to multiple servers.
 * Created {@link NetworkManagerClient}s are cached and can be reused.
 * Should be used to create a {@link NetworkManagerClient} with the same mappings and handlers for every connection to a server.
 */
public class ClientNetworkSession implements PacketIdMappingContainer{

	private final TargetIdentifier localId;
	private final Map<TargetIdentifier, NetworkManagerClient> clients;
	private final Function<TargetIdentifier, NetworkManagerClient> newClient;
	
	private final Set<PacketIdMapping> mappings;
	private final Set<PacketReceiver> receivers;
	
	/**
	 * Creates a new {@link ClientNetworkSession} that creates new {@link NetworkManagerClient}s by calling {@link NetworkManager#createClient(TargetIdentifier, TargetIdentifier)}.
	 * @param localId The local {@link TargetIdentifier} that will represent all created {@link NetworkManagerClient}s.
	 */
	public ClientNetworkSession(final TargetIdentifier localId) {
		this(localId, NetworkManager::createClient);
	}
	
	/**
	 * Creates a new {@link ClientNetworkSession} that creates new {@link NetworkManagerClient}s by the <code>createClient</code> {@link BiFunction}.
	 * @param localId The local {@link TargetIdentifier} that will represent all created {@link NetworkManagerClient}s.
	 * @param createClient The function that should be used to create a new {@link NetworkManagerClient} 
	 */
	public ClientNetworkSession(final TargetIdentifier localId, final BiFunction<TargetIdentifier, TargetIdentifier, NetworkManagerClient> createClient) {
		this.localId = localId;
		this.clients = Collections.synchronizedMap(new HashMap<>());
		this.newClient = (t) -> createClient.apply(localId, t);
		
		this.mappings = Collections.synchronizedSet(new HashSet<>());
		this.receivers = Collections.synchronizedSet(new HashSet<>());
	}
	
	/**
	 * Returns a {@link NetworkManagerClient} that connects to the remote id. If possible, it will use a cached manager
	 * that was previously created for this target, otherwise, a new manager will be created
	 * @param remoteId The {@link TargetIdentifier} to connect to
	 * @return A {@link NetworkManagerClient} for this target
	 * @see #createConnection(TargetIdentifier)
	 */
	public synchronized NetworkManagerClient getOrCreateConnection(final TargetIdentifier remoteId) {
		if(hasConnection(remoteId)) {
			return clients.get(remoteId);
		} else {
			return createConnection(remoteId);
		}
	}
	
	/**
	 * Returns a {@link NetworkManagerClient} that connects to the remote id. A new manager will be created for every call.
	 * @param remoteId The {@link TargetIdentifier} to connect to
	 * @return A {@link NetworkManagerClient} for this target
	 * @see #getOrCreateConnection(TargetIdentifier)
	 */
	public synchronized NetworkManagerClient createConnection(final TargetIdentifier remoteId) {
		final NetworkManagerClient con = newClient.apply(remoteId);
		//Init start
		con.addAllMappings(this);
		receivers.forEach((h) -> con.addIncomingPacketHandler(h));
		//Init end
		clients.put(remoteId, con);
		return con;
	}
	
	/**
	 * Checks whether a manager that connects to the target is cached.
	 * @param target The {@link TargetIdentifier} that the manager should connect to
	 * @return Whether there is a cached connection to the target
	 */
	public synchronized boolean hasConnection(final TargetIdentifier target) {
		return clients.containsKey(target) && clients.get(target) != null;
	}
	
	/**
	 * A set containing all cached {@link NetworkManagerClient}s.
	 * @return A set of all cached managers
	 */
	public synchronized Set<NetworkManagerClient> getClientConnections() {
		return Collections.unmodifiableSet(new HashSet<>(clients.values()));
	}
	
	/**
	 * A set containing the remote {@link TargetIdentifier}s of all cached managers.
	 * @return A set of all cached targets
	 */
	public synchronized Set<TargetIdentifier> getClientConnectionIds() {
		return Collections.unmodifiableSet(new HashSet<>(clients.keySet()));
	}
	
	/**
	 * Closes all cached {@link NetworkManagerClient}s by calling {@link NetworkManagerClient#closeConnectionToServer()}.
	 * @see #closeAllConnectionsSync()
	 */
	public synchronized void closeAllConnections() {
			clients.forEach((t, c) -> c.closeConnectionToServer());
	}
	
	/**
	 * Closes all cached {@link NetworkManagerClient}s by calling {@link NetworkManagerClient#closeConnectionToServer()} and 
	 * waits until all of them are closed. If waiting is interrupted, the next manager will be closed normally.
	 * @see #closeAllConnections()
	 */
	public synchronized void closeAllConnectionsSync() {
		clients.forEach((t, c) -> c.closeConnectionToServer().trySync());
	}
	
	/**
	 * The {@link TargetIdentifier} that will be the local id of all created managers.
	 * @return The local {@link TargetIdentifier}
	 */
	public TargetIdentifier getLocalId() {
		return localId;
	}

	/**
	 * Returns all {@link PacketIdMapping}s in this container. Set is immutable
	 * @return All {@link PacketIdMapping}s
	 */
	@Override
	public Set<PacketIdMapping> getAllMappings() {
		return Collections.unmodifiableSet(mappings);
	}

	/**
	 * Adds a single {@link PacketIdMapping} to this container.
	 * @param mapping The new {@link PacketIdMapping}
	 */
	@Override
	public void addMapping(PacketIdMapping mapping) {
		mappings.add(mapping);
	}
	
	/**
	 * Adds a {@link PacketReceiver} that will be called when a packet is received by a created {@link NetworkManagerClient}.
	 * @param handler The new {@link PacketReceiver}
	 */
	public void addIncomingPacketHandler(PacketReceiver handler) {
		receivers.add(handler);
	}
	
}
