package lb.simplebase.net;

import java.net.Socket;

public class ClientConfig extends SocketConfiguration {
	
	private Object customData;
	
	private ClientConfig(boolean net) {
		super(net ? new Socket() : null);
		customData = null;
	}
	
	public Object getCustomObject() {
		return customData;
	}
	
	public ClientConfig setCustomObject(Object data) {
		this.customData = data;
		return this;
	}
	
	protected Socket configuredSocket() {
		return socket;
	}
	
	public static ClientConfig forConnectionTo(TargetIdentifier serverId) {
		return new ClientConfig(!serverId.isLocalOnly());
	}
}
