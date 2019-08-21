package lb.simplebase.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import lb.simplebase.util.NamedThreadFactory;

public class InboundPacketThreadHandler implements PacketReceiver{

	private static final AtomicInteger ibhID = new AtomicInteger(0);
	
	private final PacketReceiver delegateThreadReceiver;
	private final ExecutorService threadExecutor;
	private final NamedThreadFactory fac;
	
	public InboundPacketThreadHandler(PacketReceiver delegate, int threadCount) {
		fac = new NamedThreadFactory("InboundPacketThreadHandler-" + ibhID.getAndIncrement() + "-Thread-");
		delegateThreadReceiver = delegate;
		if(threadCount <= 0) {
			threadExecutor = Executors.newCachedThreadPool(fac);
		} else {
			threadExecutor = Executors.newFixedThreadPool(threadCount, fac);
		}
	}
	
	@Override
	public void processPacket(Packet received, PacketContext source) {
		try {
			threadExecutor.execute(() -> delegateThreadReceiver.processPacket(received, source));
		} catch (RejectedExecutionException e) {
			NetworkManager.NET_LOG.warn("Rejected Packet handler execution: Service might be shut down already - Packet dropped", e);
		}
	}
	
	public void shutdownExecutor() {
		threadExecutor.shutdown();
	}
	
}
