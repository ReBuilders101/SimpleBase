package lb.simplebase.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

class LanServerDiscoveryEchoThread extends Thread {

	private static final AtomicInteger threadIds = new AtomicInteger();
	
	private final SocketNetworkManagerServer server;
	private final byte[] sequence;
	private final DatagramSocket socket;
	
	public LanServerDiscoveryEchoThread(SocketNetworkManagerServer server, byte[] sequence) {
		super("LanServerDiscoveryEchoThread-" + threadIds.getAndIncrement());
		setDaemon(true);
		this.server = server;
		this.sequence = sequence;
		this.socket = server.getDatagramSocket();
	}

	@Override
	public void run() {
		NetworkManager.NET_LOG.info("LAN Discovery Echo Thread: Started");
		while (!socket.isClosed()) {
			try {
				DatagramPacket packet = new DatagramPacket(new byte[8], 8);
				NetworkManager.NET_LOG.debug("Listening bound to address: " + socket.getLocalAddress() + " port: " + socket.getLocalPort());
				socket.receive(packet);
				NetworkManager.NET_LOG.debug("LAN Echo: Datagram received: " + Arrays.toString(packet.getData()));
				byte[] data = packet.getData();
				if(LanServerDiscovery.isValid(data, sequence) && data[7] == LanServerDiscovery.REQUEST) { //We have a request for the connnection state
					//Fire the event
					data[7] = server.attemptUdpConnection(packet.getAddress()); //Change data to success or denied
					DatagramPacket reply = new DatagramPacket(data, 8, packet.getAddress(), packet.getPort());
					socket.send(reply); //reply
				}
			} catch (SocketException e) { // closed by killthread
				NetworkManager.NET_LOG.info("LAN Discovery Echo Thread: Closed Socket");
				break;
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("LAN Discovery Echo Thread: Error while waiting for Socket", e);
			}
		}
		NetworkManager.NET_LOG.info("LAN Discovery Echo Thread: Closed");
	}

}
