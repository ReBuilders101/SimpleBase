package lb.simplebase.net.simple;

import java.net.UnknownHostException;
import java.util.function.Consumer;

import lb.simplebase.net.ConnectionState;
import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.NetworkManagerClient;
import lb.simplebase.net.ObjectPacket;
import lb.simplebase.net.TargetIdentifier;

public abstract class Client extends ReceiveSide {
	
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
			client.sendPacketToServer(new StringMessagePacket(message)).trySync();
		} else if(client.getConnectionState() == ConnectionState.CLOSED) {
			throw new RuntimeException("Connection to server is closed");
		} else {
			client.sendPacketToServer(new StringMessagePacket(message)).trySync();
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
	
	public static Client create(final String remoteAddress, final int port, final Consumer<String> handler) {
		return new Client(remoteAddress, port) {
			@Override
			public void receive(String message) {
				handler.accept(message);
			}
		};
	}
	
}
