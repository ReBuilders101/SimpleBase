package test.simplebase.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.NetworkManagerClient;
import lb.simplebase.net.NetworkManagerServer;
import lb.simplebase.net.Packet;
import lb.simplebase.net.PacketContext;
import lb.simplebase.net.PacketIdMapping;
import lb.simplebase.net.TargetIdentifier;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class LocalNetworkTest {

	static TargetIdentifier server;
	static TargetIdentifier client;
	
	NetworkManagerServer serverManager;
	NetworkManagerClient clientManager;
	
	CyclicBarrier barrier = new CyclicBarrier(2);
	
	volatile Packet assertionPacket;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		server = TargetIdentifier.createLocal("server");
		client = TargetIdentifier.createLocal("client");
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		NetworkManager.cleanUp();
	}
	
	@BeforeEach
	void setUp() throws Exception {
		serverManager = NetworkManager.createServer(server);
		serverManager.addIncomingPacketHandler(this::getPacket);
		clientManager = NetworkManager.createClient(client, server);
		clientManager.addIncomingPacketHandler(this::getPacket);
		serverManager.startServer();
	}

	@AfterEach
	void tearDown() throws Exception {
		clientManager.closeConnectionToServer();
		serverManager.stopServer();
		serverManager = null;
		clientManager = null;
	}

	@Test
	void connectTest() throws InterruptedException {
		clientManager.openConnectionToServer();
		Assertions.assertTrue(clientManager.isConnectionOpen());
	}
	
	@Test
	void sendTest() throws InterruptedException, BrokenBarrierException {
		serverManager.addMapping(PacketIdMapping.create(5, TestPacket.class, TestPacket::new));
		clientManager.addAllMappings(serverManager);
		
		clientManager.openConnectionToServer();
		
		byte[] data = new byte[50];
		new Random().nextBytes(data);
		Packet test = new TestPacket(data);
		assertTrue(clientManager.sendPacketToServer(test).sync().isSuccess(), "Cannot send packet");
		
		//Wait with assertion until packet has been processed 
		barrier.await();
		assertEquals(test, assertionPacket);
		
		new Random().nextBytes(data);
		Packet test2 = new TestPacket(data);
		assertTrue(serverManager.isCurrentClient(client), "No client connection");
		assertTrue(serverManager.isCurrentClient(client), "No open client connection");
		assertTrue(serverManager.sendPacketToClient(test2, client).sync().isSuccess(), "Cannot send packet");
		
		//Wait with assertion until packet has been processed 
		barrier.await();
		assertEquals(test2, assertionPacket);
	}

	void getPacket(Packet packet, PacketContext source) {
//		assertEquals(assertionPacket, packet);
		assertionPacket = packet;
		System.out.println(source + " | " + packet);
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
	
}
