package lb.simplebase.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

public class DataReceiverThread extends Thread {

	private Socket socket;
	private PacketFactory factory;
	
	private static final AtomicInteger threadId = new AtomicInteger(0);
	
	public DataReceiverThread(Socket socket, PacketFactory factory) {
		this.socket = socket;
		this.factory = factory;
		setDaemon(true);
		setName("Socket-DataReceiverThread-" + threadId.getAndIncrement());
	}
	
	@Override
	public void run() {
		NetworkManager.NET_LOG.info("Started Data Receiver");
		while(ConnectionState.fromSocket(socket).canSendData()) {
			if(Thread.interrupted()) {
				Thread.currentThread().interrupt();
				NetworkManager.NET_LOG.info("Data Receiver: Closing: Thread was interrupted");
				return;
			}
			try {
				byte b = (byte) socket.getInputStream().read();
				factory.feed(b);
			} catch (SocketException e) {
				NetworkManager.NET_LOG.info("Data Receiver: Closing: Socket was closed");
				return; //Socket closed
			} catch (PacketMappingNotFoundException e) {
				NetworkManager.NET_LOG.warn("Data Receiver: Packet mapping not found for received packet", e);
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Data Receiver: Closing: Socket IO Exception", e);
				e.printStackTrace(); //Closed with error
				return; //Closed externally
			}
		}
		return;
	}

	public PacketFactory getFactory() {
		return factory;
	}
	
}
