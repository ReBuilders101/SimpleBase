package lb.simplebase.net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * The {@link PacketDistributor} acts as {@link PacketReceiver} that  distributes every received {@link Packet} to all {@link PacketReceiver}s
 * in the distribution list.
 */
public class PacketDistributor implements PacketReceiver{
	
	private List<PacketReceiver> receivers;

	/**
	 * Creates a new {@link PacketDistributor} with multiple {@link PacketReceiver}s.
	 * @param receivers All {@link PacketReceiver}s that should get {@link Packet}s from this {@link PacketDistributor}
	 */
	public PacketDistributor(PacketReceiver...receivers) {
		this.receivers = new ArrayList<>(Arrays.asList(receivers)); //Wrap in a new list because Arrays.asList is immutable
	}
	/**
	 * Creates a new {@link PacketDistributor} with multiple {@link PacketReceiver}s.
	 * @param receivers All {@link PacketReceiver}s that should get {@link Packet}s from this {@link PacketDistributor}
	 */
	public PacketDistributor(Collection<PacketReceiver> receivers) {
		this.receivers = receivers == null ? new ArrayList<>() : new ArrayList<>(receivers);
	}
	
	/**
	 * Adds a {@link PacketReceiver} to the list of receivers that get {@link Packet}s from this {@link PacketDistributor}.
	 * @param receiver The {@link PacketReceiver} that should be added to the list
	 */
	public void addPacketReceiver(PacketReceiver receiver) {
		receivers.add(receiver);
	}
	
	/**
	 * Adds all {@link PacketReceiver}s to the list of receivers that get {@link Packet}s from this {@link PacketDistributor}.
	 * @param receivers The {@link PacketReceiver}s that should be added to the list
	 */
	public void addPacketReceivers(PacketReceiver...receivers) {
		//List.addAll() reqires collection, don't want to create extra object
		for(PacketReceiver reciver : receivers) {
			this.receivers.add(reciver);
		}
	}
	
	/**
	 * Removes a {@link PacketReceiver} from the list of receivers that get {@link Packet}s from this {@link PacketDistributor}.
	 * @param receiver The {@link PacketReceiver} that should be removed from the list
	 */
	public void removePacketReciver(PacketReceiver receiver) {
		receivers.remove(receiver);
	}
	
	/**
	 * Removes all {@link PacketReceiver}s from the list.<br>
	 * <b>If no new {@link PacketReceiver} is added, {@link Packet}s that go to this {@link PacketDistributor} are not processed.</b> 
	 */
	public void removeAllPacketReceivers() {
		receivers.clear();
	}
	
	/**
	 * Handles a received packet by sending it to all {@link PacketReceiver}s in the receiver list
	 * @param received The packet that should be processed by this {@link PacketDistributor}
	 * @param source The source that sent the packet
	 */
	@Override
	public void processPacket(Packet received, TargetIdentifier source) {
		receivers.forEach((r) -> r.processPacket(received, source));
	}

	/**
	 * A special type of {@link PacketDistributor} that has only two {@link PacketReceiver} that can get {@link Packet}s.<br>
	 * This implementation might be faster in special cases, beacuse it doesn not use a dynamic {@link List} to store the {@link PacketReceiver}s.<br>
	 * This class does <b>not</b> extend {@link PacketDistributor}.
	 */
	public static class TwoWayPacketDistributor implements PacketReceiver {

		private final PacketReceiver r1;
		private final PacketReceiver r2;
		
		/**
		 * Creates a new {@link TwoWayPacketDistributor}. The {@link PacketReceiver}s cannot be changed once the instance has been created.
		 * @param receiver1 The first {@link PacketReceiver}
		 * @param receiver2 The second {@link PacketReceiver}
		 */
		public TwoWayPacketDistributor(PacketReceiver receiver1, PacketReceiver receiver2) {
			r1 = receiver1;
			r2 = receiver2;
		}
		
		/**
		 * Handles a received packet by sending it both {@link PacketReceiver}s
		 * @param received The packet that should be processed by this {@link PacketDistributor}
		 * @param source The source that sent the packet
		 */
		@Override
		public void processPacket(Packet received, TargetIdentifier source) {
			r1.accept(received, source);
			r2.accept(received, source);
		}
		
	}
	
}
