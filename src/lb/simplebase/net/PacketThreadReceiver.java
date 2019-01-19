package lb.simplebase.net;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link PacketReceiver} that processes received {@link Packet}s in another thread.<br>
 * Every instance has its own thread, but all {@link Packet}s for one instance are processed on the same thread.
 */
public class PacketThreadReceiver implements PacketReceiver{

	/**
	 * The {@link ThreadGroup} that contains all threads used to process packets with a {@link PacketThreadReceiver}
	 */
	public static final ThreadGroup RECEIVER_THREAD_GROUP = new ThreadGroup("Packet-Processing");
	public static final ThreadFactory RECEIVER_THREAD_FACTORY = new GroupThreadFactory(RECEIVER_THREAD_GROUP, "Packet-Processing-Executor-"); 
	
	private final ExecutorService packetHandlerExecutor;
	private final PacketReceiver receiver;
	private boolean stopFlag;
	private final boolean singleThread; 
	
	/**
	 * Creates a new {@link PacketThreadReceiver} with an overflow limit of 100 and an empty overflow handler.
	 * @param threadReceiver The {@link PacketReceiver} that will be called on the processing thread
	 * @param singleThread Whether the used {@link ExecutorService} should use only one thread or a dynamic amount
	 */
	public PacketThreadReceiver(PacketReceiver threadReceiver, boolean singleThread) {
		this.receiver = threadReceiver;
		this.stopFlag = false;
		this.singleThread = singleThread;
		
		if(singleThread) {
			this.packetHandlerExecutor = Executors.newSingleThreadExecutor(RECEIVER_THREAD_FACTORY);
		} else {
			this.packetHandlerExecutor = Executors.newCachedThreadPool(RECEIVER_THREAD_FACTORY);
		}
	}
	
	/**
	 * Handles the packet by putting it on the waiting list to be handled by the
	 * processing thread, or calling the overflow handler in case of an overflow.
	 * An overflow can happen if too many packets are unprocessed and waiting for the processing thread.
	 * @param received The packet that should be processed by this {@link PacketReceiver}
	 * @param source The source that sent the packet
	 */
	@Override
	public void processPacket(Packet received, TargetIdentifier source) {
		if(stopFlag) return; //if stopped, don't even add new elements
		packetHandlerExecutor.execute(() -> receiver.processPacket(received, source)); //Submit a new task to the executorservice
	}
	
	/**
	 * Stops the processing {@link ExecutorService}. This means that no new packets will be accepted, but the packets that
	 * were already submitted will still be processed.
	 * @see ExecutorService#shutdown()
	 */
	public void stopProcessingExecutor() {
		stopFlag = true;
		packetHandlerExecutor.shutdown();
	}
	
	/**
	 * Stops the processing {@link ExecutorService}. Packet processing tasks that have already been submitted to the {@link ExecutorService}#
	 * will not be processed but returned.
	 * @return The list of unprocessed packet tasks.
	 * @see ExecutorService#shutdownNow()
	 */
	public List<Runnable> forceStopProcessingExecutor() {
		stopFlag = true;
		return packetHandlerExecutor.shutdownNow();
	}
	
	/**
	 * Whether the procesing {@link ExecutorService} has stopped accepting new processing tasks.<br>
	 * If <code>true</code>, all packets sent to this {@link PacketThreadReceiver} will not be processed at all.
	 * @return Whether the processing executor is stopped
	 */
	public boolean isProcessingThreadStopped() {
		return stopFlag || packetHandlerExecutor.isShutdown() || packetHandlerExecutor.isTerminated();
	}
	
	/**
	 * The {@link PacketReceiver} that will receive {@link Packet}s on the processing thread.
	 * @return The packet processing receiver
	 */
	public PacketReceiver getProcessingThreadReceiver() {
		return receiver;
	}
	
	/**
	 * Whether the {@link ExecutorService} uses a single thread or a dynamic amount.
	 * If only a single thread is used, it is not guaranteed that the receiving thread will be the same thread.
	 * There will be only one thread at a time that processes {@link Packet}s, but if a thread fails with an uncaught
	 * exception, a new thread may take its place.
	 * @return Whether the {@link ExecutorService} uses a single thread or a dynamic amount
	 */
	public boolean hasSingleThread() {
		return singleThread;
	}
	
	/**
	 * The {@link ThreadFactory} that creates new threads for a threadGroup
	 * The returned threads will be daemon threads
	 */
	private static class GroupThreadFactory implements ThreadFactory {
		
		private final AtomicInteger ID = new AtomicInteger(0);
		
		private final ThreadGroup group;
		private final String namePrefix;
		
		private GroupThreadFactory(ThreadGroup group, String namePrefix) {
			this.group = group;
			this.namePrefix = namePrefix;
		}
		
		@Override
		public Thread newThread(Runnable paramRunnable) {
			int id = ID.getAndIncrement();
			String name = namePrefix + id;
			Thread thread = new Thread(group, paramRunnable, name);
			thread.setDaemon(true);
			return thread;
		}
	}
	
}
