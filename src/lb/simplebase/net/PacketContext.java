package lb.simplebase.net;

import java.util.Objects;

import lb.simplebase.action.AsyncResult;

public abstract class PacketContext {

	private final boolean isServer;
	private final NetworkManagerCommon manager;
	private final NetworkConnection connection;
	
	protected PacketContext(boolean isServer, NetworkManagerCommon manager, NetworkConnection connection) {
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
	
	public abstract Object getCustomObject();
	
	@SuppressWarnings("unchecked")
	public <T> T getCustomObject(Class<T> type) {
		return (T) getCustomObject();
	}
	
	public abstract boolean hasCustomObject();
	
	public NetworkConnection getConnection() {
		return connection;
	}
	
	public TargetIdentifier getSenderId() {
		return getConnection().getRemoteTargetId();
	}
	
	public TargetIdentifier getReceiverId() {
		return getCommonManager().getLocalID();
	}
	
	public AsyncResult replyPacket(Packet packet) {
		return getConnection().sendPacketToTarget(packet);
	}
	
	
	protected static final class PayloadPacketContext extends PacketContext {

		private final Object payload;
		
		protected PayloadPacketContext(boolean isServer, NetworkManagerCommon manager, NetworkConnection connection, Object payload) {
			super(isServer, manager, connection);
			this.payload = payload;
		}

		@Override
		public Object getCustomObject() {
			return payload;
		}

		@Override
		public boolean hasCustomObject() {
			return payload != null;
		}
		
	}
	
}
