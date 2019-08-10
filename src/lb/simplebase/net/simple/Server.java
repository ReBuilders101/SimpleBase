package lb.simplebase.net.simple;

import java.net.UnknownHostException;
import java.util.function.Consumer;

import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.NetworkManagerServer;
import lb.simplebase.net.ServerConfiguration;
import lb.simplebase.net.TargetIdentifier;

public abstract class Server extends ReceiveSide {

	private final NetworkManagerServer server;

	public Server(int port) {
		try {
			server = NetworkManager.createServer(TargetIdentifier.createNetwork("server-internal", "localhost", port), ServerConfiguration.create());
			server.addMapping(StringMessagePacket.getMapping(1));
			server.addIncomingPacketHandler(this::receive0);
			server.startServer().trySync();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Server Address not found", e);
		}
	}
	
	public final NetworkManagerServer getServerManager() {
		return server;
	}
	
	public final void sendToAll(String message) {
		server.sendPacketToAllClients(new StringMessagePacket(message)).trySync();
	}
	
	public final void sendTo(String address, int port, String message) {
		for(TargetIdentifier client : server.getCurrentClients()) {
			if(client.matches(address, port)) {
				server.sendPacketToClient(new StringMessagePacket(message), client).trySync();
				return;
			}
		}
		System.err.println("Client not found");
	}
	
	public final void close() {
		server.stopServer().trySync();
	}
	
	
	public static Server create(final int port, final Consumer<String> handler) {
		return new Server(port) {
			@Override
			public void receive(String message) {
				handler.accept(message);;
			}
		};
	}
	
}
