package lb.simplebase.net;

import lb.simplebase.net.todo.PacketSendFuture;

/**
 * This interface represents an object that can send packets to other network targets (mostly {@link PacketReceiver}s)
 * and has its own {@link TargetIdentifier}.
 */
@Deprecated
public interface PacketSender {
	
	/**
	 * Sends a packet to the specified target.
	 * @param packet The packet that should be sent
	 * @param id The {@link TargetIdentifier} of the target
	 * @return 
	 */
	public PacketSendFuture sendPacketTo(Packet packet, TargetIdentifier id);
	
	/**
	 * Sends a packet to all specified targets.
	 * @param packet The packet that should be sent
	 * @param ids The {@link TargetIdentifier}s of all targets
	 */
	public default void sendPacketTo(Packet packet, TargetIdentifier...ids) {
		for(TargetIdentifier id : ids) {
			sendPacketTo(packet, id);
		}
	}
	
	/**
	 * The {@link TargetIdentifier} associated with this network target, that should be used as a packet
	 * source when a packet is sent from this object.
	 * @return The {@link TargetIdentifier} of this {@link PacketSender}
	 */
	public TargetIdentifier getSenderID();
	
}
