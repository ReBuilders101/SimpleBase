package lb.simplebase.net.localimpl;

import lb.simplebase.function.EventHandlerMap;
import lb.simplebase.net.ByteBuffer;
import lb.simplebase.net.Packet;
import lb.simplebase.net.PacketReceiver;
import lb.simplebase.net.PacketSender;
import lb.simplebase.net.TargetIdentifier;

/**
 * This class acts as the packet distribution point for network-like comminication in one program.
 * This can be used for singleplayer modes in games that have a client / server model.
 * The normal networking code can be used, while no network communication is happening. 
 */
public abstract class LocalNetworkRegistry {

	private EventHandlerMap<TargetIdentifier, PacketReceiver> receivers;
	private PacketReceiver defaultReceiver;
	
	protected LocalNetworkRegistry(PacketReceiver defaultReceiver) {
		this.receivers  = new EventHandlerMap<>();
		this.defaultReceiver = defaultReceiver;
	}
	
	/**
	 * Registers a {@link PacketReceiver} for a {@link TargetIdentifier}. All {@link Packet}s sent to this target will be
	 * sent to this receiver. One target can have more than one receiver.
	 * @param id The {@link TargetIdentifier} of the target that the receiver should listen to
	 * @param receiver The {@link PacketReceiver} that should listen for packets at this target
	 */
	public void registerReceiver(TargetIdentifier id, PacketReceiver receiver) {
		receivers.addHandler(id, receiver);
	}
	
	/**
	 * Unregisters a {@link PacketReceiver} instance from a {@link TargetIdentifier}. The receiver will no
	 * longer get packets that are sent to this target.
	 * @param id The {@link TargetIdentifier} of the target that the receiver should stop listening to
	 * @param receiver The {@link PacketReceiver} that should stop listening for packets at this target
	 */
	public void unregisterReceiver(TargetIdentifier id, PacketReceiver receiver) {
		receivers.removeHandler(id, receiver);
	}
	
	/**
	 * Unregisters a {@link PacketReceiver} from all targets that it is currently registered to.
	 * @param receiver The {@link PacketReceiver} that should stop listening for packets
	 */
	public void unregisterReceiver(PacketReceiver receiver) {
		receivers.values().forEach((c) -> c.remove(receiver));
	}
	
	/**
	 * Unregisters all {@link PacketReceiver}s for this {@link TargetIdentifier}.
	 * If no new receiver is registered for this target, all packets sent to the target will be
	 * sent to the default packet receiver
	 * @param id The {@link TargetIdentifier} from which all receivers should be removed
	 * @see LocalNetworkRegistry#getDefaultReceiver()
	 */
	public void unregisterReceivers(TargetIdentifier id) {
		receivers.removeAllHandlers(id);
	}
	
	/**
	 * Creates a new {@link PacketSender}. All packets sent through this packet sender will be processed by
	 * this {@link LocalNetworkRegistry}. The target identifer will act as the source of the packets.
	 * @param id The {@link TargetIdentifier} of the {@link PacketSender}
	 * @return A {@link PacketSender} that will send its {@link Packet}s through this {@link LocalNetworkRegistry}
	 */
	public PacketSender createPacketSender(TargetIdentifier id) {
		return new RegistryPacketSender(id, this);
	}

	protected EventHandlerMap<TargetIdentifier, PacketReceiver> getReceiverMap() {
		return receivers;
	}

	/**
	 * The default packet receiver will receive all packets that are sent to a {@link TargetIdentifier}
	 * that has no registered {@link PacketReceiver}s. This is done to prevent {@link Packet}s from getting
	 * lost, but the implementation may ignore the packet.
	 * @return The default packet receiver
	 */
	public PacketReceiver getDefaultReceiver() {
		return defaultReceiver;
	}
	
	/**
	 * The code that sends a packet from source to target.
	 * Depends on the implementation. If no {@link PacketReceiver} is found for the {@link TargetIdentifier}, the
	 * packet should be sent to the default receiver.
	 * @param packet The packet that should be sent
	 * @param target The target that the packet should be sent to
	 * @param source The source of the packet
	 */
	protected abstract void sendPacketTo(Packet packet, TargetIdentifier target, PacketSender source);
	
	/**
	 * Creates a new {@link LocalNetworkRegistry} instance. The second parameter determines the implementation that should be used to
	 * send the packets from source to target.<p>
	 * If the second parameter is <code>true</code>, the data from the packet will be converted to bytes, and then a ne Packet instance will be constructed
	 * and filled with the data. This is slower than the other implementation, but better simulates network traffic.<br>
	 * If the second parameter is <code>false</code>, the packet instance will be passed directly to the {@link PacketReceiver}. This
	 * approach is faster, but if changes are made to the packet after it has been sent, these changes will also appear at the
	 * reciver.
	 * @param defaultReceiver The {@link PacketReceiver} that should be used when no other {@link PacketReceiver} is found for a {@link TargetIdentifier}
	 * @param requireCopying If <code>true</code>, the packet data will be copied bytewise
	 * @return The created {@link LocalNetworkRegistry} instance
	 */
	public static LocalNetworkRegistry createLocalNetworkRegistry(PacketReceiver defaultReceiver, boolean requireCopying) {
		return requireCopying ? new LocalNetworkRegistryCopying(defaultReceiver) : new LocalNetworkRegistryPassing(defaultReceiver);
	}
	
	/**
	 * An implementation that passes the packet instance directly to the receivers
	 */
	private static class LocalNetworkRegistryPassing extends LocalNetworkRegistry {
		
		protected LocalNetworkRegistryPassing(PacketReceiver defaultReceiver) {
			super(defaultReceiver);
		}

		@Override
		public void sendPacketTo(Packet packet, TargetIdentifier target, PacketSender source) {
			getReceiverMap().forEachHandler(target, (h) -> h.processPacket(packet, source.getSenderID()), getDefaultReceiver());
		}
	}
	
	/**
	 * An implementation that copies the bytes from the packet to a new instance and then passes the
	 * newly created packet th the receiver
	 */
	private static class LocalNetworkRegistryCopying extends LocalNetworkRegistry {
		
		protected LocalNetworkRegistryCopying(PacketReceiver defaultReceiver) {
			super(defaultReceiver);
		}

		@Override
		public void sendPacketTo(Packet packet, TargetIdentifier target, PacketSender source) {
			ByteBuffer buf = new ByteBuffer();
			packet.writeData(buf);
			Packet pak = packet.createEmptyInstance();
			pak.readData(buf);
			//Send pak instead of packet
			getReceiverMap().forEachHandler(target, (h) -> h.processPacket(pak, source.getSenderID()), getDefaultReceiver());
		}
	}
}
