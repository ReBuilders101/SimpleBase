package lb.simplebase.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;

public class LanServerDiscovery {
	
	private final DatagramSocket broadcastSocket;
	private final InetAddress broadcastAddress;
	private final byte[] requestData;
	private final BiConsumer<InetSocketAddress, Boolean> updater;
	private final LanServerDiscoveryReceiverThread receiverThread;
	
	protected static final byte REQUEST = 0;
	protected static final byte SUCCESS = 1;
	protected static final byte DENIED  = 2;
	
	private LanServerDiscovery(final byte[] header, final BiConsumer<InetSocketAddress, Boolean> updater) throws SocketException, UnknownHostException {
		//Prepare Socket
		broadcastSocket = new DatagramSocket();
		broadcastSocket.setBroadcast(true);
		broadcastAddress = InetAddress.getByName("255.255.255.255");
		
		//Prepare data to send: clamp(byte[], 7) & REQUEST
		requestData = new byte[8];
		System.arraycopy(header, 0, requestData, 0, Math.min(7, header.length));
		requestData[7] = REQUEST;
		
		this.updater = updater;
		
		this.receiverThread = new LanServerDiscoveryReceiverThread(this);
		this.receiverThread.start();
	}
	
	public void updateLanServers(int...ports) {
		try {
			for(int port : ports) {
				NetworkManager.NET_LOG.debug("Sending Request for address: " + broadcastAddress + " port: " + port);
				broadcastSocket.send(new DatagramPacket(requestData, 8, broadcastAddress, port));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected DatagramSocket getSocket() {
		return broadcastSocket;
	}
	
	protected void updateFromThread(InetSocketAddress addresses, boolean success) {
		updater.accept(addresses, success);
	}
	
	protected byte[] getHeaderSequence() {
		return requestData;
	}
	
	protected boolean isValid(byte[] data) {
		return isValid(data, requestData);
	}
	
	/**
	 * Compares the first 7 bytes from the arrays
	 * @param data
	 * @param template
	 * @return
	 */
	protected static boolean isValid(byte[] data, byte[] template) {
		for(int i = 0; i < 7; i++) {
			if(data[i] != template[i]) return false;
		}
		return true;
	}
	
	public static LanServerDiscovery create(byte[] template, BiConsumer<InetSocketAddress, Boolean> updateReceiver) {
		try {
			return new LanServerDiscovery(template, updateReceiver);
		} catch (SocketException | UnknownHostException e) {
			NetworkManager.NET_LOG.error("Error while creating LanServerDiscovery", e);
			return null;
		}
	}
	
}
