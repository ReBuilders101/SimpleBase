package lb.simplebase.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

class LanServerDiscoveryReceiverThread extends Thread {

	private static final AtomicInteger threadIds = new AtomicInteger();
	
	private final LanServerDiscovery discovery;
	private boolean killThread;
	
	protected LanServerDiscoveryReceiverThread(LanServerDiscovery discovery) {
		super("LanServerDiscoveryReceiverThread-" + threadIds.getAndIncrement());
		this.discovery = discovery;
		this.killThread = false;
	}
	
	public void killThread() {
		this.killThread = true;
		discovery.getSocket().close();
	}
	
	@Override
	public void run() {
		NetworkManager.NET_LOG.info("LAN Discovery Receiver Thread: Started");
		while (!killThread && !discovery.getSocket().isClosed()) {
			try {
				DatagramPacket packet = new DatagramPacket(new byte[8], 8);
				discovery.getSocket().receive(packet);
				NetworkManager.NET_LOG.debug("LAN Receiver: Datagram received: " + Arrays.toString(packet.getData()));
				byte[] data = packet.getData();
				if(discovery.isValid(data)) {
					discovery.updateFromThread(new InetSocketAddress(packet.getAddress(), packet.getPort()), data[7] == LanServerDiscovery.SUCCESS);// 8th byte is success flag
				}
			} catch (SocketException e) { // closed by killthread
				NetworkManager.NET_LOG.info("LAN Discovery Receiver Thread: Closed Socket");
				break;
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("LAN Discovery Receiver Thread: Error while waiting for Socket", e);
			}
		}
		NetworkManager.NET_LOG.info("LAN Discovery Receiver Thread: Closed");
	}
	
}
