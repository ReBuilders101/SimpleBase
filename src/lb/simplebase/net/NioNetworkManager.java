package lb.simplebase.net;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.util.Objects;

import lb.simplebase.util.Validate;

public final class NioNetworkManager {

	private NioNetworkManager() {}
	
	public static NetworkManagerServer createServer(TargetIdentifier localId) {
		return createServer(localId, NioNetworkManager.createServerConfig(localId));
	}
	
	public static NetworkManagerServer createServer(TargetIdentifier localId, ServerConfig config) {
		
		Objects.requireNonNull(localId, "Server local TargetIdentifier must not be null");
		Objects.requireNonNull(config,  "ServerConfig must not be null");
		Validate.requireType(config, NioServerConfig.class, "ServerConfig must be created through NioNetworkManager.createServerConfig()");
		
		if(localId.isLocalOnly()) {
			return new LocalNetworkManagerServer(localId, config.getThreadCount());
		} else {
			NioServerConfig nioConfig = (NioServerConfig) config;
			if(nioConfig.configuredChannel() == null) {
				NetworkManager.NET_LOG.warn("Error while creating ServerSocketChannel. Using local server");
				return new LocalNetworkManagerServer(localId, config.getThreadCount());
			} else {
				try {
					return new NioNetworkManagerServer(localId, nioConfig.configuredChannel(), config.getThreadCount());
				} catch (IOException e) {
					NetworkManager.NET_LOG.error("Error while opening server selector", e);
					return null;
				}
			}
		}
	}
	
	public static ServerConfig createServerConfig(TargetIdentifier serverId) {
		try {
			return new NioServerConfig(serverId.isLocalOnly() ? null : ServerSocketChannel.open());
		} catch (IOException e) {
			NetworkManager.NET_LOG.error("ServerConfig: Could not create ServerSocketChannel");
			try {
				return new NioServerConfig(null); //With false, it can't throw the exception
			} catch (IOException e1) {
				e1.printStackTrace(); //So this will not happen
				throw new RuntimeException(e1);
			}
		}
	}
	
}
