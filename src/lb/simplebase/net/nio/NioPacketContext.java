package lb.simplebase.net.nio;

import lb.simplebase.net.PacketContext;

public class NioPacketContext extends PacketContext {
	
	protected NioPacketContext(boolean isServer, NioNetworkManagerServer manager, NioNetworkConnection connection, Object payload) {
		super(isServer, manager, connection);
	}

	@Override
	public Object getCustomObject() {
		return getConnection().getServerSelectionKey().attachment();
	}

	@Override
	public boolean hasCustomObject() {
		return getConnection().getServerSelectionKey().attachment() != null;
	}

	//Override to remove extra typecasting in get/hasCustomObject()
	@Override
	public NioNetworkConnection getConnection() {
		return (NioNetworkConnection) super.getConnection();
	}
	
}
