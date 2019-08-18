package lb.simplebase.net;

public class GenericPacketContext<T> extends PacketContext {

	protected GenericPacketContext(boolean isServer, NetworkManagerCommon manager, AbstractNetworkConnection connection, T payload) {
		super(isServer, manager, connection, payload);
	}
	
	@SuppressWarnings("unchecked")
	public T getCustomObject() {
		return (T) super.getCustomObject();
	}
	
}
