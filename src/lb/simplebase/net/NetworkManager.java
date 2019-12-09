package lb.simplebase.net;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lb.simplebase.event.EventBus;
import lb.simplebase.event.EventBusRegistry;
import lb.simplebase.log.CurrentThreadNameFormat;
import lb.simplebase.log.LogHelper;
import lb.simplebase.log.LogLevel;
import lb.simplebase.log.Logger;
import lb.simplebase.reflect.QuickReflectionUtils;

/**
 * The {@link NetworkManager} provides static methods to create servers and clients <br>
 * The {@link #cleanUp()} method should be called before the program exits
 */
public abstract class NetworkManager implements PacketReceiver, NetworkManagerCommon {
	
	public static final Logger NET_LOG = LogHelper.create("SimpleBase-NetAPI", LogLevel.DEBUG, new CurrentThreadNameFormat());
	
	private static final List<Runnable> cleanupTasks = new ArrayList<>();
	
	private final Set<PacketIdMapping> mappings;
	protected final EventBus bus;
	
	protected NetworkManager(TargetIdentifier localId) {
		NetworkManager.createNetworkParty();
		NetworkManager.addCleanupTask(this::shutdown);
		local = localId;
		mappings = Collections.synchronizedSet(new HashSet<>());
		this.bus = EventBus.create();
	}

	@Override
	public EventBusRegistry getEventBus() {
		return bus;
	}

	private final TargetIdentifier local; //every manager represents one party
	
	@Override
	public TargetIdentifier getLocalID() {
		return local;
	}
	
	protected void notifyConnectionClosed(NetworkConnection connection, ClosedConnectionEvent.Cause cause) {
		bus.post(new ClosedConnectionEvent(connection, cause));
	}

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
		AsyncNetTask.shutdownExecutor();
		LocalConnectionManager.shutdownExecutor();
		currentState = Lifecycle.STOPPED;
	}
	
	public static synchronized void cleanUpAndExit() {
		cleanUp();
		System.exit(0);
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
	
	public static NetworkManagerServer createServer(TargetIdentifier localId) {
		return createServer(localId, NetworkManager.createServerConfig());
	}
	
	public static NetworkManagerServer createServer(TargetIdentifier localId, LanServerDiscovery discovery) {
		ServerConfig sc = createServerConfig();
		sc.setDatagramDiscovery(discovery);
		return createServer(localId, sc);
	}
	
	public static NetworkManagerServer createServer(TargetIdentifier localId, ServerConfig config) {
		
		Objects.requireNonNull(localId, "Server local TargetIdentifier must not be null");
		Objects.requireNonNull(config,  "ServerConfig must not be null");
		
		if(localId.isLocalOnly()) {
			return new LocalNetworkManagerServer(localId, config.getThreadCount());
		} else {
			if(config.configuredSocket() == null) {
				NetworkManager.NET_LOG.warn("Error while creating ServerSocket. Using local server.");
				return new LocalNetworkManagerServer(localId, config.getThreadCount());
			} else {
				try {
					return new SocketNetworkManagerServer(localId, config.configuredSocket(), config.getThreadCount(), config.getDatagramDiscovery(), config.getDatagramDiscoverySequence());
				} catch (SocketException e) {
					NetworkManager.NET_LOG.warn("Error while creating Server Datagram Socket. Continuing without Datagram Discovery Features");
					try {
						return new SocketNetworkManagerServer(localId, config.configuredSocket(), config.getThreadCount(), false, null); //Exc only happens when lat param is true
					} catch (SocketException e1) {
						NetworkManager.NET_LOG.fatal("Fatal error: Exception was thrown on a code path that it was not expected on", e1);
						throw new Error("Invalid Codepath???", e);
					}
				}
			}
		}
	}
	
	public static void setLogLevel(LogLevel level) {
		Objects.requireNonNull(level, "Log Level can't be null");
		QuickReflectionUtils.Fields.setField(Logger.class, "minimalLevel", NetworkManager.NET_LOG, level);
	}
	
	public static void setAsyncMode(boolean enabled) {
		AsyncNetTask.RUN_ASYNC = enabled;
	}
	
	public static NetworkManagerClient createClient(TargetIdentifier localId, TargetIdentifier serverId) {
		return new SocketNetworkManagerClient(localId, serverId, ClientConfig.forConnectionTo(serverId));
	}
	
	public static NetworkManagerClient createClient(TargetIdentifier localId, TargetIdentifier serverId, ClientConfig config) {
		return new SocketNetworkManagerClient(localId, serverId, config);
	}
	
	public static Set<TargetIdentifier> getLocalServerIds() {
		return Collections.unmodifiableSet(LocalConnectionManager.getServers().keySet());
	}
	
	public static ServerConfig createServerConfig() {
		return new ServerConfig();
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
