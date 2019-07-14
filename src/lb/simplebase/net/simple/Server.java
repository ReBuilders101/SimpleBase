package lb.simplebase.net.simple;

import java.net.UnknownHostException;

import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.NetworkManagerServer;
import lb.simplebase.net.ObjectPacket;
import lb.simplebase.net.Packet;
import lb.simplebase.net.ServerConfiguration;
import lb.simplebase.net.TargetIdentifier;

public abstract class Server {

	private final NetworkManagerServer server;

	public Server(int port) {
		try {
			server = NetworkManager.createServer(TargetIdentifier.createNetwork("server-internal", "localhost", port), ServerConfiguration.create());
			server.addMapping(ObjectPacket.getMapping(1));
			server.addIncomingPacketHandler(this::receive0);
			server.startServer().trySync();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Server Address not found", e);
		}
	}
	
	public NetworkManagerServer getServerManager() {
		return server;
	}
	
	public void sendToAll(String message) {
		server.sendPacketToAllClients(new ObjectPacket(message)).trySync();
	}
	
	public void sendTo(String address, int port, String message) {
		for(TargetIdentifier client : server.getCurrentClients()) {
			if(client.getConnectionAddress().getHostString().equals(address) &&
					client.getConnectionAddress().getPort() == port) {
				server.sendPacketToClient(new ObjectPacket(message), client).trySync();
				return;
			}
		}
		System.err.println("Client not found");
	}
	
	public void close() {
		server.stopServer().trySync();
	}
	
	private void receive0(Packet packet, TargetIdentifier source) {
		final ObjectPacket p = (ObjectPacket) packet;
		receive(p.getObject(String.class));
	}
	
	public abstract void receive(String message);
	
}
