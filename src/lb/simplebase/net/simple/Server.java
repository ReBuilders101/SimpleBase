package lb.simplebase.net.simple;

import java.util.function.Consumer;

import lb.simplebase.net.ConfigureConnectionEvent;
import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.NetworkManagerServer;
import lb.simplebase.net.TargetIdentifier;

public abstract class Server extends ReceiveSide {

	private final NetworkManagerServer server;

	public Server(int port) {
		server = NetworkManager.createServer(TargetIdentifier.createNetwork("server-internal", "localhost", port).getValue());
		server.addMapping(StringMessagePacket.getMapping(1));
		server.addIncomingPacketHandler(this::receive0);
		server.getEventBus().register((e) -> this.newConnection(e.getRemoteTargetId().getId()), ConfigureConnectionEvent.class);
//			server.addNewConnectionHandler((t) -> this.newConnection(t.getConnectionAddress().getHostString(), t.getConnectionAddress().getPort()));
		server.startServer();
	}
	
	public final NetworkManagerServer getServerManager() {
		return server;
	}
	
	protected abstract void newConnection(String name);
	
	public final void sendToAll(String message) {
		server.sendPacketToAllClients(new StringMessagePacket(message)).sync();
	}
	
	public final void sendTo(String name, String message) {
		server.getClients().withStateDo((clients) -> {
			for(TargetIdentifier client : clients) {
				if(client.getId().equals(name)) {
					server.sendPacketToClient(new StringMessagePacket(message), client).sync();
					return;
				}
			}
			System.err.println("Client not found");
		});
	}
	
	public final void close() {
		server.stopServer();
	}
	
	
	public static Server create(final int port, final Consumer<String> handler, final Consumer<String> conHandler) {
		return new Server(port) {
			@Override
			public void receive(String message) {
				handler.accept(message);
			}

			@Override
			protected void newConnection(String name) {
				conHandler.accept(name);
			}
		};
	}
	
}
