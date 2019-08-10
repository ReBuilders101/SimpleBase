package lb.simplebase.net;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ClientNetworkSession implements PacketIdMappingContainer{

	private final TargetIdentifier localId;
	private final Map<TargetIdentifier, NetworkManagerClient> clients;
	private final Function<TargetIdentifier, NetworkManagerClient> newClient;
	private final Set<PacketIdMapping> mappings;
	
	public ClientNetworkSession(final TargetIdentifier localId) {
		this(localId, NetworkManager::createClient);
	}
	
	public ClientNetworkSession(final TargetIdentifier localId, final BiFunction<TargetIdentifier, TargetIdentifier, NetworkManagerClient> createClient) {
		this.localId = localId;
		this.mappings = new HashSet<>();
		this.clients = Collections.synchronizedMap(new HashMap<>());
		this.newClient = (t) -> createClient.apply(localId, t);
	}
	
	
	public synchronized NetworkManagerClient getOrCreateConnection(final TargetIdentifier remoteId) {
		if(hasConnection(remoteId)) {
			return clients.get(remoteId);
		} else {
			final NetworkManagerClient con = newClient.apply(remoteId);
			con.addAllMappings(this); //Add all the mappings
			clients.put(remoteId, con);
			return con;
		}
	}
	
	public synchronized boolean hasConnection(final TargetIdentifier target) {
		return clients.containsKey(target) && clients.get(target) != null;
	}
	
	public synchronized Set<NetworkManagerClient> getClientConnections() {
		return Collections.unmodifiableSet(new HashSet<>(clients.values()));
	}
	
	public synchronized Set<TargetIdentifier> getClientConnectionIds() {
		return Collections.unmodifiableSet(new HashSet<>(clients.keySet()));
	}
	
	public synchronized void closeAllConnections() {
			clients.forEach((t, c) -> c.closeConnectionToServer());
	}
	
	public synchronized void closeAllConnectionsSync() {
		clients.forEach((t, c) -> c.closeConnectionToServer().trySync());
	}
	
	public TargetIdentifier getLocalId() {
		return localId;
	}

	@Override
	public Set<PacketIdMapping> getAllMappings() {
		return Collections.unmodifiableSet(mappings);
	}

	@Override
	public void addMapping(PacketIdMapping mapping) {
		mappings.add(mapping);
	}
	
}
