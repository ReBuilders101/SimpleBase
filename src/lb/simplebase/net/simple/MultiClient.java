package lb.simplebase.net.simple;

import java.net.UnknownHostException;

import lb.simplebase.net.ClientNetworkSession;
import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.NetworkManagerClient;
import lb.simplebase.net.TargetIdentifier;

public abstract class MultiClient extends ReceiveSide{

	private final ClientNetworkSession session;

	public MultiClient() {
		session = NetworkManager.createMultiClient(TargetIdentifier.createLocal("multiClient"));
	}
	
	public final void sendToAll(String message) {
		session.getClientConnections().forEach((c) -> {
			c.sendPacketToServer(new StringMessagePacket(message)).trySync();
		});
	}
	
	public final void sendTo(String address, int port, String message) {
		NetworkManagerClient foundClient = null;
		for(NetworkManagerClient client : session.getClientConnections()) {
			if(client.getLocalID().matches(address, port)) {
				foundClient = client;
				break;
			}
		}
		if(foundClient == null) {
			TargetIdentifier newId = null;
			try {
				newId = TargetIdentifier.createNetwork("NewTargetTo:" + address, address, port);
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
			foundClient = session.getOrCreateConnection(newId);
		}
		
		foundClient.sendPacketToServer(new StringMessagePacket(message)).trySync();
	}
	
	public final ClientNetworkSession getSessionManager() {
		return session;
	}
}
