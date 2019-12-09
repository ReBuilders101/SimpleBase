package lb.simplebase.net.simple;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lb.simplebase.net.ConfigureConnectionEvent;
import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.NetworkManagerServer;
import lb.simplebase.net.TargetIdentifier;

public abstract class Server extends ReceiveSide {

	private final NetworkManagerServer server;

	public Server(int port) {
		server = NetworkManager.createServer(TargetIdentifier.createNetwork("server-internal", "localhost", port));
		server.addMapping(StringMessagePacket.getMapping(1));
		server.addIncomingPacketHandler(this::receive0);
		server.getEventBus().register((e) -> this.newConnection(e.getRemoteAddress().getHostString(), e.getRemoteAddress().getPort()), ConfigureConnectionEvent.class);
//			server.addNewConnectionHandler((t) -> this.newConnection(t.getConnectionAddress().getHostString(), t.getConnectionAddress().getPort()));
		server.startServer();
	}
	
	public final NetworkManagerServer getServerManager() {
		return server;
	}
	
	protected abstract void newConnection(String hostname, int port);
	
	public final void sendToAll(String message) {
		server.sendPacketToAllClients(new StringMessagePacket(message)).sync();
	}
	
	public final void sendTo(String address, int port, String message) {
		for(TargetIdentifier client : server.getCurrentClients()) {
			if(client.matches(address, port)) {
				server.sendPacketToClient(new StringMessagePacket(message), client).sync();
				return;
			}
		}
		System.err.println("Client not found");
	}
	
	public final void close() {
		server.stopServer();
	}
	
	
	public static Server create(final int port, final Consumer<String> handler, final BiConsumer<String, Integer> conHandler) {
		return new Server(port) {
			@Override
			public void receive(String message) {
				handler.accept(message);
			}

			@Override
			protected void newConnection(String hostname, int port) {
				conHandler.accept(hostname, port);
			}
		};
	}
	
}
