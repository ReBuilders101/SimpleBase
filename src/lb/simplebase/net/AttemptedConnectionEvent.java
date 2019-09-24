package lb.simplebase.net;

import java.net.InetAddress;

import lb.simplebase.event.Event;

public final class AttemptedConnectionEvent extends Event{

	private final InetAddress address;
	private final NetworkManagerServer server;
	
	public AttemptedConnectionEvent(InetAddress address, NetworkManagerServer server) {
		super(true);
		this.address = address;
		this.server = server;
	}

	public InetAddress getRemoteConnectionAddress() {
		return address;
	}
	
	public NetworkManagerServer getServerManager() {
		return server;
	}
	
}
