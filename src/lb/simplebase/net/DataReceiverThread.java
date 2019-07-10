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
		while(ConnectionState.fromSocket(socket).canSendData()) {
			if(Thread.interrupted()) {
				Thread.currentThread().interrupt();
				break;
			}
			try {
				byte b = (byte) socket.getInputStream().read();
				factory.feed(b);
			} catch (SocketException e) {
				return; //Socket closed
			} catch (PacketMappingNotFoundException e) {
				e.printStackTrace(); //TODO Log
			} catch (IOException e) {
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
