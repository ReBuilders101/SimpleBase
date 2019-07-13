package lb.simplebase.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class InboundPacketThreadHandler implements PacketReceiver{

	private static final AtomicInteger ibhID = new AtomicInteger(0);
	
	private final PacketReceiver delegateThreadReceiver;
	private final ExecutorService threadExecutor;
	private final ThreadNameFactory fac;
	
	public InboundPacketThreadHandler(PacketReceiver delegate, int threadCount) {
		fac = new ThreadNameFactory(ibhID.getAndIncrement());
		delegateThreadReceiver = delegate;
		if(threadCount <= 0) {
			threadExecutor = Executors.newCachedThreadPool(fac);
		} else {
			threadExecutor = Executors.newFixedThreadPool(threadCount, fac);
		}
	}
	
	@Override
	public void processPacket(Packet received, TargetIdentifier source) {
		threadExecutor.execute(() -> delegateThreadReceiver.processPacket(received, source));
	}
	
	public static class ThreadNameFactory implements ThreadFactory{
		
		private final AtomicInteger threadId = new AtomicInteger(0);
		private final int handlerId;
		
		public ThreadNameFactory(int hid) {
			handlerId = hid;
		}
		
		@Override
		public Thread newThread(Runnable var1) {
			Thread t = new Thread(var1);
			t.setName("InboundPacketThreadHandler-" + handlerId + "-Thread-" + threadId.getAndIncrement());
			return t;
		}	
	}
	
	public void shutdownExecutor() {
		threadExecutor.shutdown();
	}
	
}
