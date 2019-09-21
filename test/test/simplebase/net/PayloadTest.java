package test.simplebase.net;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.net.ConfigureConnectionEvent;
import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.NetworkManagerClient;
import lb.simplebase.net.NetworkManagerServer;
import lb.simplebase.net.Packet;
import lb.simplebase.net.PacketContext;
import lb.simplebase.net.TargetIdentifier;

class PayloadTest {

	NetworkManagerServer server;
	NetworkManagerClient client;
	
	
	@BeforeEach
	void setUp() throws Exception {
		TargetIdentifier serverId = TargetIdentifier.createNetwork("Server", "localhost", 12345);
		TargetIdentifier clientId = TargetIdentifier.createLocal("Client");
		server = NetworkManager.createServer(serverId);
		client = NetworkManager.createClient(clientId, serverId);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		server.startServer();
		server.getEventBus().register((e) -> {
			e.setCustomObject(new Player());
		}, ConfigureConnectionEvent.class);
		server.addIncomingPacketHandler(this::handlePacket);
		client.openConnectionToServer();
	}
	
	void handlePacket(Packet packet, PacketContext context) {
		
	}

	
	static class Player {
		
		String name;
		
	}
	
}
