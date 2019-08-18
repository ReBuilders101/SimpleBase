package lb.simplebase.net;

import java.util.Objects;

public abstract class PacketContext {

	private final boolean isServer;
	private final NetworkManagerCommon manager;
	private final AbstractNetworkConnection connection;
	
	private final Object payload;
	
	
	protected PacketContext(boolean isServer, NetworkManagerCommon manager, AbstractNetworkConnection connection, Object payload) {
		Objects.requireNonNull(manager, "Network manager must not be null");
		Objects.requireNonNull(connection, "Network connection must not be null");
		
		if(isServer) {
			if(!(manager instanceof NetworkManagerServer)) throw new IllegalArgumentException("When PacketContext represents server side, the manager must implement NetworkManagerServer");
		} else {
			if(!(manager instanceof NetworkManagerClient)) throw new IllegalArgumentException("When PacketContext represents client side, the manager must implement NetworkManagerClient");
		}
		
		this.isServer = isServer;
		this.manager = manager;
		this.connection = connection;
		this.payload = payload;
	}
	
	
	
	public boolean isServerSide() {
		return isServer;
	}
	
	public boolean isClientSide() {
		return !isServer;
	}
	
	public NetworkManagerServer getServerManager() {
		if(isServer) {
			return (NetworkManagerServer) manager;
		} else {
			return null;
		}
	}
	
	public NetworkManagerClient getClientManager() {
		if(isServer) {
			return null;
		} else {
			return (NetworkManagerClient) manager;
		}
	}
	
	public NetworkManagerCommon getCommonManager() {
		return manager;
	}
	
	public Object getCustomObject() {
		return payload;
	}
	
	public boolean hasCustomObject() {
		return payload != null;
	}
	
	public AbstractNetworkConnection getConnection() {
		return connection;
	}
	
	public TargetIdentifier getSenderId() {
		return getConnection().getRemoteTargetId();
	}
	
	public TargetIdentifier getReceiverId() {
		return getCommonManager().getLocalID();
	}
	
	public PacketSendFuture replyPacket(Packet packet) {
		return getConnection().sendPacketToTarget(packet);
	}
}
