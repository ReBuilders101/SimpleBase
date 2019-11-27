package lb.simplebase.net;

import java.net.InetAddress;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

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
	
	public static Consumer<AttemptedConnectionEvent> createHandler(BiPredicate<InetAddress, NetworkManagerServer> condition) {
		return (e) -> {
			if(!condition.test(e.address, e.server)) e.tryCancel();
		};
	}
}
