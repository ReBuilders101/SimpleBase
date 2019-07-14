package lb.simplebase.net.simple;

import java.net.UnknownHostException;
import java.util.function.Consumer;

import lb.simplebase.net.ConnectionState;
import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.NetworkManagerClient;
import lb.simplebase.net.ObjectPacket;
import lb.simplebase.net.Packet;
import lb.simplebase.net.TargetIdentifier;

public abstract class Client {
	
	private final NetworkManagerClient client;
	
	public Client(String remoteAddress, int port) {
		try {
			client = NetworkManager.createClient(TargetIdentifier.createLocal("client"),
					TargetIdentifier.createNetwork("server", remoteAddress, port));
			client.addMapping(ObjectPacket.getMapping(1));
			client.addIncomingPacketHandler(this::receive0);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Remote Address not found", e);
		}
	}
	
	public NetworkManagerClient getClientManager() {
		return client;
	}
	
	public void send(String message) {
		if(client.getConnectionState() == ConnectionState.UNCONNECTED) {
			client.openConnectionToServer().trySync();
			client.sendPacketToServer(new ObjectPacket(message)).trySync();
		} else if(client.getConnectionState() == ConnectionState.CLOSED) {
			throw new RuntimeException("Connection to server is closed");
		} else {
			client.sendPacketToServer(new ObjectPacket(message)).trySync();
		}
	}
	
	public void close() {
		client.closeConnectionToServer().trySync();
	}
	
	public boolean isClosed() {
		return client.getConnectionState() == ConnectionState.CLOSED;
	}
	
	public boolean isOpen() {
		return client.getConnectionState() == ConnectionState.OPEN;
	}
	
	private void receive0(Packet packet, TargetIdentifier source) {
		final ObjectPacket p = (ObjectPacket) packet;
		receive(p.getObject(String.class));
	}
	
	public abstract void receive(String message);
	
	public static Client create(final String remoteAddress, final int port, final Consumer<String> handler) {
		return new Client(remoteAddress, port) {
			@Override
			public void receive(String message) {
				handler.accept(message);
			}
		};
	}
	
}
