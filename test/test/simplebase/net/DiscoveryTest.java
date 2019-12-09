package test.simplebase.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.log.LogLevel;
import lb.simplebase.net.AttemptedConnectionEvent;
import lb.simplebase.net.LanServerDiscovery;
import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.NetworkManagerServer;
import lb.simplebase.net.TargetIdentifier;

class DiscoveryTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	public static void main(String[] args) throws Exception {
		new DiscoveryTest().test();
	}

	@Test
	void testEcho() throws Exception {
		EchoServer server = new EchoServer();
		server.start();
		
		EchoClient client = new EchoClient();
		System.out.println("sending");
		System.out.println(client.sendEcho("test"));
	}
	
	
	
	@Test
	void testUDP() throws Exception {
		final int port = 12334;
		DatagramSocket receiver = new DatagramSocket(null);
		DatagramSocket sender = new DatagramSocket();
		DatagramPacket sendData = new DatagramPacket(new byte[] {1, 2, 3, 4}, 4, InetAddress.getLocalHost(), port);
		
		receiver.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
		DatagramPacket receiveData = new DatagramPacket(new byte[4], 4);
		
		Executors.newSingleThreadExecutor().submit(() -> {
			Thread.sleep(3000);
			System.out.println("sending");
			sender.send(sendData);
			return null;
		});
		
		System.out.println("waiting");
		receiver.receive(receiveData);
		System.out.println(Arrays.toString(receiveData.getData()));
		
		sender.close();
		receiver.close();
	}
	
	
	
	@Test
	void test() throws InterruptedException {
		NetworkManager.setLogLevel(LogLevel.DEBUG);
		
		TargetIdentifier serverTI1 = TargetIdentifier.createNetworkServer("server1", 12345);
		TargetIdentifier serverTI2 = TargetIdentifier.createNetworkServer("server2", 12346);
		TargetIdentifier serverTI3 = TargetIdentifier.createNetworkServer("server3", 12347);
		
		LanServerDiscovery lsd = LanServerDiscovery.create(new byte[] {89, 45, 76, 23, 54, 77, 12}, DiscoveryTest::updates); // 7 random bytes
		
		NetworkManagerServer server1 = NetworkManager.createServer(serverTI1, lsd);
		NetworkManagerServer server2 = NetworkManager.createServer(serverTI2, lsd);
		NetworkManagerServer server3 = NetworkManager.createServer(serverTI3, lsd);
		
		server1.getEventBus().register(DiscoveryTest::deny,    AttemptedConnectionEvent.class);
		server2.getEventBus().register(DiscoveryTest::variant, AttemptedConnectionEvent.class);
		server3.getEventBus().register(DiscoveryTest::accept,  AttemptedConnectionEvent.class);
		
		server1.startServer();
		server2.startServer();
		server3.startServer();
		
		Thread.sleep(5000);
		
		System.out.println("Sending request");
		lsd.updateLanServers(12345, 12346, 12347);
		
		Thread.sleep(5000);
//		NetworkManager.cleanUpAndExit();
	}

	public static void updates(InetSocketAddress address, boolean success) {
		System.out.println(address + " -> " + success);
	}
	
	public static void deny(AttemptedConnectionEvent e) {
		System.out.println("Attempt: " + e.getRemoteConnectionAddress() + " denied");
		e.tryCancel(); //always cancel
	}
	
	public static void accept(AttemptedConnectionEvent e) {
		System.out.println("Attempt: " + e.getRemoteConnectionAddress() + " accepted");
		//don't cancel
	}
	
	private static volatile boolean cancelFlag = true;
	public static void variant(AttemptedConnectionEvent e) {
		System.out.println("Attempt: " + e.getRemoteConnectionAddress() + " variant -> cancelFlag: " + cancelFlag);
		if(cancelFlag) e.tryCancel(); //sometimes cancel
	}
	
}
