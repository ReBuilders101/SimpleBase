package lb.simplebase.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

class DataReceiverThread extends Thread {

	private final Socket socket;
	private final AbstractNetworkConnection connection;
	private final PacketFactory factory;
	
	private static final AtomicInteger threadId = new AtomicInteger(0);
	
	public DataReceiverThread(Socket socket, PacketFactory factory, AbstractNetworkConnection connection) {
		this.socket = socket;
		this.connection = connection;
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
				break;
			}
			try {
				int i = socket.getInputStream().read();
				if(i == -1) {
					NetworkManager.NET_LOG.info("Data Receiver: Socket was closed remotely");
					break;
				}
				byte b = (byte) i;
				factory.feed(b);
			} catch (SocketException e) {
				NetworkManager.NET_LOG.info("Data Receiver: Closing: Socket was closed");
				break; //Socket closed
			} catch (PacketMappingNotFoundException e) {
				NetworkManager.NET_LOG.warn("Data Receiver: Packet mapping not found for received packet", e);
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Data Receiver: Closing: Socket IO Exception", e);
				e.printStackTrace(); //Closed with error
				break; //Closed externally
			}
		}
		NetworkManager.NET_LOG.info("Data Receiver: Stopped listening for data, closing connection");
		connection.close();//Close when socket is closed
		return;
	}

	public PacketFactory getFactory() {
		return factory;
	}
	
}
