package test.simplebase.net;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.net.ConnectionState;
import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.NetworkManagerClient;
import lb.simplebase.net.NetworkManagerServer;
import lb.simplebase.net.Packet;
import lb.simplebase.net.PacketIdMapping;
import lb.simplebase.net.ServerConfiguration;
import lb.simplebase.net.ServerState;
import lb.simplebase.net.TargetIdentifier;

class NetworkTest {

	static TargetIdentifier server;
	static TargetIdentifier clientFromClient;
	static TargetIdentifier clientFromServer;
	
	NetworkManagerServer serverManager;
	NetworkManagerClient clientManager;
	
	CyclicBarrier barrier = new CyclicBarrier(2);
	
	volatile Packet assertionPacket;
	
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		NetworkManager.cleanUp();
	}
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		server = 			TargetIdentifier.createNetwork("server", "localhost", 1234);
		clientFromClient =  TargetIdentifier.createNetwork("client", "localhost", 1234);
	}

	@BeforeEach
	void setUp() throws Exception {
		serverManager = NetworkManager.createServer(ServerConfiguration.create(), server);
		serverManager.addIncomingPacketHandler(this::getPacket);
		clientManager = NetworkManager.createClient(clientFromClient, server);
		clientManager.addIncomingPacketHandler(this::getPacket);
		serverManager.startServer().sync();
	}

	@AfterEach
	void tearDown() throws Exception {
		clientManager.closeConnectionToServer();
		serverManager.stopServer();
		clientManager = null;
		serverManager = null;
	}

	@Test
	void connectTest() throws InterruptedException {
		assertTrue(serverManager.getServerState() == ServerState.STARTED);
		assertTrue(clientManager.openConnectionToServer().ensureState(ConnectionState.OPEN), "Could not open connection");
		assertTrue(clientManager.getConnectionState() == ConnectionState.OPEN, "Connection not open");
	}
	
	@Test
	void sendTest() throws InterruptedException, BrokenBarrierException {
		System.out.println("test0");
		serverManager.addMapping(PacketIdMapping.create(5, TestPacket.class, TestPacket::new));
		clientManager.addAllMappings(serverManager); //ensure the mappings are always the same
		
		assertTrue(serverManager.getServerState() == ServerState.STARTED);
		assertTrue(clientManager.openConnectionToServer().ensureState(ConnectionState.OPEN), "Could not open connection");
		assertTrue(clientManager.getConnectionState() == ConnectionState.OPEN, "Connection not open");
		
		byte[] dataArray = new byte[50];
		new Random().nextBytes(dataArray);
		Packet data = new TestPacket(dataArray);
		clientManager.sendPacketToServer(data).ensurePacketSent();
		System.out.println("sent!");
		
		//Now try the other dircetion
		assertEquals(serverManager.getCurrentClientCount(), 1, "More or less than one client");
		clientFromServer = serverManager.getCurrentClients().iterator().next(); //Get the only connection
		assertNotNull(clientFromServer, "Client connection from server side not found");
		
		barrier.await(); //Wait for packet to be received
		assertEquals(data, assertionPacket, "Packets are not equal");
		
		//Now try the other dircetion
		assertEquals(serverManager.getCurrentClients().size(), 1, "More or less than one client");
		clientFromServer = serverManager.getCurrentClients().iterator().next(); //Get the only connection
		assertNotNull(clientFromServer, "Client connection from server side not found");
		
		new Random().nextBytes(dataArray);
		data = new TestPacket(dataArray);
		
		assertTrue(serverManager.sendPacketToClient(data, clientFromServer).ensurePacketSent(), "Could not send Packet");
		
		barrier.await(); //wait for received packet
		assertEquals(data, assertionPacket, "Packets are not equal (2)");
		System.out.println("test");
	}

	void getPacket(Packet packet, TargetIdentifier source) {
//		assertEquals(assertionPacket, packet);
		System.out.println("Received");
		assertionPacket = packet;
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
	
}
