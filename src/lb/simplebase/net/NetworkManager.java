package lb.simplebase.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lb.simplebase.log.LogHelper;
import lb.simplebase.log.Logger;

/**
 * The {@link NetworkManager} handles all {@link AbstractNetworkConnection}s in a network for one target.
 * In case if clients, this is only the connection to the server, but in case of a server, there
 * are multiple connections to clients.<br>
 * Because the implementation depends heavily on whether the {@link NetworkManager} represents a server or client,
 * the subclasses {@link NetworkManagerServer} and {@link SocketNetworkManagerClient} should be used.
 */
public abstract class NetworkManager implements PacketIdMappingContainer, PacketReceiver {
	
	public static final Logger NET_LOG = LogHelper.create("SimpleBase-NetAPI");
	
	private static List<Runnable> cleanupTasks = new ArrayList<>();
	
	private Set<PacketIdMapping> mappings;
	
	protected NetworkManager(TargetIdentifier localId) {
		NetworkManager.createNetworkParty();
		NetworkManager.addCleanupTask(this::shutdown);
		local = localId;
		mappings = new HashSet<>();
	}

	private TargetIdentifier local; //every manager represents one party
	
	public TargetIdentifier getLocalID() {
		return local;
	}
	
	public abstract void notifyConnectionClosed(AbstractNetworkConnection connection);

	protected abstract void shutdown();
	
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
	
	/**
	 * Anything that has to be done before the program ends
	 */
	public static synchronized void cleanUp() {
		if(currentState == Lifecycle.STOPPED) return;
		cleanupTasks.forEach(Runnable::run);
		FutureState.shutdownExecutor();
		LocalConnectionManager.shutdownExecutor();
		currentState = Lifecycle.STOPPED;
	}
	
	public static void addCleanupTask(Runnable task) {
		cleanupTasks.add(task);
	}
	
	public static void createNetworkParty() {
		if(currentState == Lifecycle.INIT) {
			currentState = Lifecycle.RUNNING;
			return;
		} else if (currentState == Lifecycle.RUNNING) {
			return;
		} else { //already stopped
			throw new RuntimeException("Cannot create new Network Components after the cleanup has happened");
		}
	}
	
	public static NetworkManagerServer createServer(ServerConfiguration config, TargetIdentifier localId) {
		try {
			return new SocketNetworkManagerServer(config.copy(), localId);
		} catch (IOException e) {
			NET_LOG.error("Could not create SocketNetworkManagerServer", e);
			return null;
		}
	}
	
	public static NetworkManagerClient createClient(TargetIdentifier localId, TargetIdentifier serverId) {
		return new SocketNetworkManagerClient(localId, serverId);
	}
	
	private static Lifecycle currentState = Lifecycle.INIT;
	
	private static enum Lifecycle {
		INIT, RUNNING, STOPPED;
	}
}
