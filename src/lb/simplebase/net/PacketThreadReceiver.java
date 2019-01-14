package lb.simplebase.net;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A {@link PacketReceiver} that processes received {@link Packet}s in another thread.<br>
 * Every instance has its own thread, but all {@link Packet}s for one instance are processed on the same thread.
 */
public class PacketThreadReceiver implements PacketReceiver{

	/**
	 * The {@link ThreadGroup} that contains all threads used to process packets with a {@link PacketThreadReceiver}
	 */
	public static final ThreadGroup RECEIVER_THREAD_GROUP = new ThreadGroup("Packet-Processing");
	
	private ExecutorService packetHandlerExecutor;
	
	private static volatile int ID = 0;
	
	private final Thread processingThread;
	private final BlockingQueue<PacketInformation> packetsToProcess;
	private final PacketReceiver receiver;
	private final PacketReceiver overflowHandler;
	
	private final int id;
	private volatile boolean stopFlag;
	
	/**
	 * Creates a new {@link PacketThreadReceiver} with an overflow limit of 100 and an empty overflow handler.
	 * @param threadReceiver The {@link PacketReceiver} that will be called on the processing thread
	 */
	public PacketThreadReceiver(PacketReceiver threadReceiver) {
		this(threadReceiver, 100, PacketReceiver.createEmptyReceiver());
	}
	
	/**
	 * Creates a new {@link PacketThreadReceiver} with an empty overflow handler.
	 * @param threadReceiver The {@link PacketReceiver} that will be called on the processing thread
	 * @param overflowLimit The capacity of the underlying queue, this means the amount of packets that can be waiting for processing
	 * before packets are handled by the overflow receiver
	 */
	public PacketThreadReceiver(PacketReceiver threadReceiver, int overflowLimit) {
		this(threadReceiver, overflowLimit, PacketReceiver.createEmptyReceiver());
	}
	
	/**
	 * Creates a new {@link PacketThreadReceiver} with an overflow limit of 100.
	 * @param threadReceiver The {@link PacketReceiver} that will be called on the processing thread
	 * @param overflowReceiver The {@link PacketReceiver} that will receive packets in case of an overflow.
	 * This receiver <b>not</b> will receive the packets on a separate thread and should mostly used for
	 * logging the overflow
	 */
	public PacketThreadReceiver(PacketReceiver threadReceiver, PacketReceiver overflowReceiver) {
		this(threadReceiver, 100, overflowReceiver);
	}
	
	/**
	 * Creates a new {@link PacketThreadReceiver}.
	 * @param threadReceiver The {@link PacketReceiver} that will be called on the processing thread
	 * @param overflowLimit The capacity of the underlying queue, this means the amount of packets that can be waiting for processing
	 * before packets are handled by the overflow receiver
	 * @param overflowReceiver The {@link PacketReceiver} that will receive packets in case of an overflow.
	 * This receiver <b>not</b> will receive the packets on a separate thread and should mostly used for
	 * logging the overflow
	 */
	public PacketThreadReceiver(PacketReceiver threadReceiver, int overflowLimit, PacketReceiver overflowReceiver) {
		this(threadReceiver, overflowLimit, overflowReceiver, null);
	}
	
	protected PacketThreadReceiver(PacketReceiver threadReceiver, int overflowLimit, PacketReceiver overflowReceiver, String specialThreadName) {
		processingThread = new Thread(RECEIVER_THREAD_GROUP, this::threadRunnable);
		packetsToProcess = new ArrayBlockingQueue<>(overflowLimit);
		receiver = threadReceiver;
		stopFlag = false;
		overflowHandler = overflowReceiver;
		id = ID++; //Set Id and increment
		//setup thread
//		if(specialThreadName == null) specialThreadName = "PacketThreadReceiver";
//		processingThread.setName(specialThreadName + "-" + id + "-Processing");
//		processingThread.setDaemon(true);
//		processingThread.start();
		packetHandlerExecutor = Executors.newSingleThreadExecutor();
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
		PacketInformation info = new PacketInformation(received, source);
		if(!packetsToProcess.offer(info)) { //put is blocking, so offer
			overflowHandler.processPacket(received, source);
		}
	}

	/**
	 * The method that is executed in the processing thread
	 */
	private void threadRunnable(){
		try {
			while(!stopFlag) { //Just repeat taking packets
				PacketInformation next = packetsToProcess.take();
				receiver.accept(next.packet, next.source);
			}
		} catch (InterruptedException e) { //Someone wants this thread to terminate, so we will do so
			Thread.currentThread().interrupt(); //Restore flag
		} //Method then returns, the thread ends
		stopFlag = true; //Before terminating, also set the stop flag
	}
	
	/**
	 * The thread in which the {@link Packet}s are processed. Every instance of {@link PacketThreadReceiver} has
	 * its own processing thread. The processing thread is a daemon {@link Thread}, which means it will not prevent the program
	 * from exiting even if this thread is still running.<br>
	 * If the processing thread should be stopped, the method {@link #stopProcessingThread()} should be used instead of using the
	 * returned {@link Thread} object.
	 * @return The processing thread
	 */
	public Thread getProcessingThread() {
		return processingThread;
	}
	
	/**
	 * Stops the processing thread. Packets in the queue will not be processed, and newly received packets will not be
	 * added to the queue.
	 * @return The remaining {@link Queue} of packets that could not be processed
	 * @see #getProcessingThread()
	 */
	public Queue<PacketInformation> stopProcessingThread() {
		stopFlag = true;
		return packetsToProcess;
	}
	
	/**
	 * Whether the procesing thread has stopped processing packets from the queue.<br>
	 * If <code>true</code>, all packets sent to this {@link PacketThreadReceiver} will not be processed at all.
	 * @return Whether the processing thread is stopped
	 * @see #getProcessingThread()
	 */
	public boolean isProcessingThreadStopped() {
		return stopFlag || !processingThread.isAlive();
	}
	
	/**
	 * The {@link PacketReceiver} that will receive {@link Packet}s on the processing thread.
	 * @return The packet processing receiver
	 */
	public PacketReceiver getProcessingThreadReceiver() {
		return receiver;
	}
	
	/**
	 * The {@link PacketReceiver} that will receive overflowing packets on the 'normal' thread
	 * (The same thread that this {@link PacketThreadReceiver} received the packets on).
	 * @return The packet overflow receiver
	 */
	public PacketReceiver getOverflowReceiver() {
		return overflowHandler;
	}
	
	/**
	 * The amount of packets that have been received by this {@link PacketThreadReceiver}, but not yet processed on the processing thread.
	 * If this number exceeds the overflow limit, the {@link Packet}s are sent to the overflow receiver instead.
	 * @return The amount of packets waiting to be processed
	 * @see #getProcessingThreadReceiver()
	 * @see #getOverflowReceiver()
	 */
	public int getWaitingPacketsCount() {
		return packetsToProcess.size();
	}
	
	/**
	 * The id is unique to this instance and is used in the name of the processing thread.
	 * @return The id of this {@link PacketThreadReceiver} instance
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Utility class that packs information about a {@link Packet} and its source ({@link TargetIdentifier}) into a single
	 * object that can be used in collections.
	 */
	public static class PacketInformation {
		
		private final Packet packet;
		private final TargetIdentifier source;
		
		private PacketInformation(Packet packet, TargetIdentifier source) {
			this.packet = packet;
			this.source = source;
		}
		
		/**
		 * The {@link Packet} that was sent form the source ({@link #getSource()}).
		 * @return The {@link Packet}
		 */
		public Packet getPacket() {
			return packet;
		}
		
		/**
		 * The {@link TargetIdentifier} of the sorce that the {@link Packet} ({@link #getPacket()}) was sent from.
		 * @return The {@link TargetIdentifier} of the source
		 */
		public TargetIdentifier getSource() {
			return source;
		}
	}
	
}
