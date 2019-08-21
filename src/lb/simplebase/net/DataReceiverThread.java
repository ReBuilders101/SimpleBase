package lb.simplebase.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

import lb.simplebase.net.ClosedConnectionEvent.Cause;

class DataReceiverThread extends Thread {

	private final Socket socket;
	private final NetworkConnection connection;
	private final PacketFactory factory;
	
	private static final AtomicInteger threadId = new AtomicInteger(0);
	
	public DataReceiverThread(Socket socket, PacketFactory factory, NetworkConnection connection) {
		this.socket = socket;
		this.connection = connection;
		this.factory = factory;
		setDaemon(true);
		setName("Socket-DataReceiverThread-" + threadId.getAndIncrement());
	}
	
	@Override
	public void run() {
		NetworkManager.NET_LOG.info("Started Data Receiver");
		ClosedConnectionEvent.Cause threadEndCause = Cause.UNKNOWN;
		while(ConnectionState.fromSocket(socket).canSendData()) {
			if(Thread.interrupted()) {
				Thread.currentThread().interrupt();
				NetworkManager.NET_LOG.info("Data Receiver: Closing: Thread was interrupted");
				threadEndCause = Cause.INTERRUPTED;
				break;
			}
			try {
				int i = socket.getInputStream().read();
				if(i == -1) {
					NetworkManager.NET_LOG.info("Data Receiver: Socket was closed remotely");
					threadEndCause = Cause.REMOTE;
					break;
				}
				byte b = (byte) i;
				factory.feed(b);
			} catch (SocketException e) {
				NetworkManager.NET_LOG.info("Data Receiver: Closing: Socket was closed");
				threadEndCause = Cause.EXTERNAL;
				break; //Socket closed
			} catch (PacketMappingNotFoundException e) {
				NetworkManager.NET_LOG.warn("Data Receiver: Packet mapping not found for received packet", e);
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Data Receiver: Closing: Socket IO Exception", e);
				threadEndCause = Cause.IOEXCEPTION;
				break; //Closed externally
			}
		}
		NetworkManager.NET_LOG.info("Data Receiver: Stopped listening for data, closing connection");
		connection.closeWithReason(threadEndCause);//Close when socket is closed
		return;
	}

	public PacketFactory getFactory() {
		return factory;
	}
	
}
