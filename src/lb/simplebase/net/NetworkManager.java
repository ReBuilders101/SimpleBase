package lb.simplebase.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lb.simplebase.log.CurrentThreadNameFormat;
import lb.simplebase.log.LogHelper;
import lb.simplebase.log.LogLevel;
import lb.simplebase.log.Logger;

/**
 * The {@link NetworkManager} handles all {@link AbstractNetworkConnection}s in a network for one target.
 * In case if clients, this is only the connection to the server, but in case of a server, there
 * are multiple connections to clients.<br>
 * Because the implementation depends heavily on whether the {@link NetworkManager} represents a server or client,
 * the subclasses {@link NetworkManagerServer} and {@link NetworkManagerClient} should be used.
 */
public abstract class NetworkManager implements PacketIdMappingContainer, PacketReceiver {
	
	public static final Logger NET_LOG = LogHelper.create("SimpleBase-NetAPI", LogLevel.DEBUG, new CurrentThreadNameFormat());
	
	private static final List<Runnable> cleanupTasks = new ArrayList<>();
	
	private final Set<PacketIdMapping> mappings;
	
	protected NetworkManager(TargetIdentifier localId) {
		NetworkManager.createNetworkParty();
		NetworkManager.addCleanupTask(this::shutdown);
		local = localId;
		mappings = new HashSet<>();
	}

	private final TargetIdentifier local; //every manager represents one party
	
	public TargetIdentifier getLocalID() {
		return local;
	}
	
	protected abstract void notifyConnectionClosed(AbstractNetworkConnection connection);

	protected abstract void shutdown();
	
	@Override
	public Set<PacketIdMapping> getAllMappings() {
		return mappings;
	}

	/**
	 * Adds a new {@link PacketIdMapping} to the list of mappings
	 * @param mapping The {@link PacketIdMapping} to add
	 */
	@Override
	public void addMapping(PacketIdMapping mapping) {
		mappings.add(mapping);
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
	
	protected static void createNetworkParty() {
		if(currentState == Lifecycle.INIT) {
			currentState = Lifecycle.RUNNING;
			return;
		} else if (currentState == Lifecycle.RUNNING) {
			return;
		} else { //already stopped
			throw new RuntimeException("Cannot create new Network Components after the cleanup has happened");
		}
	}
	
	public static NetworkManagerServer createServer(TargetIdentifier localId, ServerConfiguration config) {
		if(localId.isLocalOnly()) {
			return new LocalNetworkManagerServer(localId, config.copy());
		} else {
			try {
				return new SocketNetworkManagerServer(config.copy(), localId);
			} catch (IOException e) {
				NET_LOG.error("Could not create SocketNetworkManagerServer", e);
				return null;
			}
		}
	}
	
	public static NetworkManagerClient createClient(TargetIdentifier localId, TargetIdentifier serverId) {
		return new SocketNetworkManagerClient(localId, serverId);
	}
	
	public static Set<TargetIdentifier> getLocalServerIds() {
		return Collections.unmodifiableSet(LocalConnectionManager.getServers().keySet());
	}
	
	
	private static Lifecycle currentState = Lifecycle.INIT;
	
	private static enum Lifecycle {
		INIT, RUNNING, STOPPED;
	}
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() ->  {
			if(currentState != Lifecycle.STOPPED) {
				NetworkManager.NET_LOG.warn("Clean Up tasks will be run by a shutdown hook. It is recommended to manually call NetworkManager.cleanUp() before the program exits.");
				NetworkManager.cleanUp();
			}
		}));
	}
}
