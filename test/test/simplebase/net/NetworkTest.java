package test.simplebase.net;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.net.NetworkManagerClient;
import lb.simplebase.net.NetworkManagerServer;
import lb.simplebase.net.Packet;
import lb.simplebase.net.PacketIdMapping;
import lb.simplebase.net.TargetIdentifier;

class NetworkTest {

	static TargetIdentifier server;
	static TargetIdentifier clientFromClient;
	static TargetIdentifier clientFromServer;
	
	NetworkManagerServer serverManager;
	NetworkManagerClient clientManager;
	
	CyclicBarrier barrier = new CyclicBarrier(2);
	
	volatile Packet assertionPacket;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		server = new TargetIdentifier.NetworkTargetIdentifier("server", "localhost", 1234);
		clientFromClient = new TargetIdentifier.NetworkTargetIdentifier("client", "localhost", 1234);
	}

	@BeforeEach
	void setUp() throws Exception {
		serverManager = new NetworkManagerServer(this::getPacket, server, System.out::println);
		clientManager = new NetworkManagerClient(this::getPacket, clientFromClient, server);
	}

	@AfterEach
	void tearDown() throws Exception {
		clientManager.close();
		serverManager.shutdown();
		clientManager = null;
		serverManager = null;
	}

	@Test
	void connectTest() {
		assertTrue(clientManager.openConnectionToServer(), "Could not open connection");
		assertTrue(clientManager.isServerConnectionOpen(), "Connection not open");
	}
	
	@Test
	void sendTest() throws InterruptedException, BrokenBarrierException {
		serverManager.addMapping(PacketIdMapping.create(5, TestPacket.class, TestPacket::new));
		clientManager.addMapping(PacketIdMapping.create(5, TestPacket.class, TestPacket::new));
		
		assertTrue(clientManager.openConnectionToServer(), "Could not open connection");
		assertTrue(clientManager.isServerConnectionOpen(), "Connection not open");
		
		byte[] dataArray = new byte[50];
		new Random().nextBytes(dataArray);
		Packet data = new TestPacket(dataArray);
		clientManager.sendPacketToServer(data);
		
		barrier.await(); //Wait for packet to be received
		assertEquals(data, assertionPacket, "Packets are not equal");
		
		//Now try the other dircetion
		assertEquals(serverManager.getClients().size(), 1, "More or less than one client");
		clientFromServer = serverManager.getClients().iterator().next(); //Get the only connection
		assertNotNull(clientFromServer, "Client connection from server side not found");
		
		new Random().nextBytes(dataArray);
		data = new TestPacket(dataArray);
		
		assertTrue(serverManager.sendPacketToClient(data, clientFromServer), "Could not send Packet");
		
		barrier.await(); //wait for received packet
		assertEquals(data, assertionPacket, "Packets are not equal (2)");
	}

	void getPacket(Packet packet, TargetIdentifier source) {
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
