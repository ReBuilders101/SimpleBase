package lb.simplebase.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Consumer;

public class ServerConfig {

	private int threads;
	private byte[] udpDiscovery;
	public Consumer<ServerSocket> modSocket;
	
	protected ServerConfig() {
		threads = 0; //Zero means unlimited threads
		udpDiscovery = null;
	}
	
	public void setDatagramDiscovery(byte[] headerSequence) {
		udpDiscovery = headerSequence;
	}
	
	public void setDatagramDiscovery(LanServerDiscovery headerSequence) {
		udpDiscovery = headerSequence.getHeaderSequence();
	}
	
	public void removeDatagramDiscovery() {
		udpDiscovery = null;
	}
	
	protected boolean getDatagramDiscovery() {
		return udpDiscovery != null;
	}
	
	protected byte[] getDatagramDiscoverySequence() {
		return udpDiscovery;
	}
	
	public ServerConfig setProcessingThreadCount(int count) {
		threads = count;
		return this;
	}
	
	public void addSocketModifier(Consumer<ServerSocket> modifier) {
		if(modSocket == null) {
			modSocket = modifier;
		} else {
			modSocket = modSocket.andThen(modifier);
		}
	}
	
	/**
	 * Internal use only
	 * @return
	 * @throws IOException 
	 */
	protected ServerSocket configuredSocket() {
		try {
			ServerSocket soc = new ServerSocket();
			if(modSocket != null) modSocket.accept(soc);
			return soc;
		} catch (IOException e) {
			NetworkManager.NET_LOG.warn("Error while creating socket: ", e);
			return null;
		}
	}
	
	protected int getThreadCount() {
		return threads;
	}
}
