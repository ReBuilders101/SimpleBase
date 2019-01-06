package lb.simplebase.test;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import lb.simplebase.net.NetworkManagerClient;
import lb.simplebase.net.NetworkManagerServer;
import lb.simplebase.net.Packet;
import lb.simplebase.net.TargetIdentifier;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class NetworkTest {

	static TargetIdentifier server;
	static TargetIdentifier client;
	
	NetworkManagerServer serverManager;
	NetworkManagerClient clientManager;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		server = new TargetIdentifier.LocalTargetIdentifier("server");
		client = new TargetIdentifier.LocalTargetIdentifier("client");
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		serverManager = new NetworkManagerServer(this::getPacket, server, System.out::println);
		clientManager = new NetworkManagerClient(this::getPacket, client, server);
	}

	@AfterEach
	void tearDown() throws Exception {
		clientManager.close();
		serverManager.close();
		serverManager.removeLocalServer();
		serverManager = null;
		clientManager = null;
	}

	@Test
	void connectTest() {
		clientManager.openConnectionToServer();
		Assertions.assertTrue(clientManager.isServerConnectionOpen());
	}
	
	@Test
	void sendTest() {
		clientManager.openConnectionToServer();
		
	}

	void getPacket(Packet packet, TargetIdentifier source) {
		System.out.println(source + " | " + packet);
	}
	
}
