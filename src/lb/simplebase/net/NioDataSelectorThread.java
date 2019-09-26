package lb.simplebase.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lb.simplebase.net.ClosedConnectionEvent.Cause;

public class NioDataSelectorThread extends Thread {

	private static final AtomicInteger threadIds = new AtomicInteger(0);
	private static final int BULK_READ_BUFFER_SIZE = 2048; //This buffer is allocated once for the entire application, so it should be large enought for big packets
	
//	private final NioNetworkManagerServer server;
	private final Selector selector;
	private final ByteBuffer tempBuffer;
	private final Lock selectorRegisterLock;
	
	public NioDataSelectorThread(NioNetworkManagerServer server, Selector selector) {
		super("Selector-DataReceiver-" + threadIds.getAndIncrement());
		setDaemon(true);
		
		this.selector = selector;
		this.selectorRegisterLock = new ReentrantLock(true);
		this.tempBuffer = ByteBuffer.allocate(BULK_READ_BUFFER_SIZE);
	}

	protected Lock getSelectorRegisterLock() {
		return selectorRegisterLock;
	}
	
	protected Selector getSelector() {
		return selector;
	}
	
	@Override
	public void run() {
		NetworkManager.NET_LOG.info("Started Selector Data Receiver");
		while(selector.isOpen()) {
			if(Thread.interrupted()) {
				Thread.currentThread().interrupt();
				NetworkManager.NET_LOG.info("Data Receiver: Closing: Thread was interrupted");
				terminateChannels(Cause.INTERRUPTED);
				return;
			}
			try {
				//Lock checkpoint before select() call
				//Do we need try/finally here? (On one line to be compact)
				try {selectorRegisterLock.lock();} finally {selectorRegisterLock.unlock();}
				//Select all readable keys
				if(selector.select() == 0) continue; //Skip empty selected sets
				final Set<SelectionKey> keys = selector.selectedKeys();
				for(SelectionKey key : keys) {
					if(key.isReadable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						int received;
						do {
							tempBuffer.clear();	//Clear the buffer for the new data
							received = channel.read(tempBuffer);
							final NioPacketFactory factory = (NioPacketFactory) key.attachment();
							factory.feed(tempBuffer);
						} while(received > 0);
					}
				}
				keys.clear();
			} catch (PacketMappingNotFoundException e) {
				NetworkManager.NET_LOG.warn("Data Receiver: Packet mapping not found for received packet", e);
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Data Receiver: Closing: Selector/Channel IO Exception", e);
				terminateChannels(Cause.IOEXCEPTION);
				break; //Closed externally
			}
		}
		NetworkManager.NET_LOG.info("Data Receiver: Stopped listening for data, closing connections");
		terminateChannels(Cause.EXTERNAL); //The API-external Selector object was closed
		return;
	}
	
	private void terminateChannels(ClosedConnectionEvent.Cause cause) {
		for(SelectionKey key : selector.keys()) {
			if(key.isValid()) {
				NioPacketFactory revMap = (NioPacketFactory) key.attachment(); //reverse mapping channel->connection here
				revMap.getConnection().closeWithReason(cause);
			}
		}
	}
	
}
