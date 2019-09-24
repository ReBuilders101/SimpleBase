package lb.simplebase.net;

import java.net.InetSocketAddress;
import java.net.Socket;
import lb.simplebase.event.Event;

public final class ConfigureConnectionEvent extends Event{

//	private final Socket socket;
	private final TargetIdentifier remoteTarget;
	private final NetworkManagerServer server;
	private final SocketConfiguration config;
	private final boolean local;
	
	private Object data;
	
	public ConfigureConnectionEvent(final Socket socket, final TargetIdentifier remoteTarget, final NetworkManagerServer server) {
		super(false);
		this.data = null;
		this.config = new SocketConfiguration(socket);
//		this.socket = socket;
		this.remoteTarget = remoteTarget;
		this.server = server;
		this.local = false;
	}

	public ConfigureConnectionEvent(final TargetIdentifier remoteTarget, final NetworkManagerServer server) {
		super(false);
		this.data = null;
		this.config = null;
//		this.socket = null;
		this.remoteTarget = remoteTarget;
		this.server = server;
		this.local = true;
	}
	
	public TargetIdentifier getRemoteTargetId() {
		return remoteTarget;
	}
	
	public InetSocketAddress getRemoteAddress() {
		return remoteTarget.getConnectionAddress();
	}
	
	public TargetIdentifier getLocalTargetId() {
		return server.getLocalID();
	}
	
	public NetworkManagerServer getServerManager() {
		return server;
	}
	
	public Object getCustomObject() {
		return data;
	}
	
	public boolean hasCustomObject() {
		return data != null;
	}
	
	public void setCustomObject(Object data) {
		this.data = data;
	}
	
	public SocketConfiguration getSocketConfiguration() {
		return config;
	}
	
	public boolean isLocalConnection() {
		return local;
	}
}
