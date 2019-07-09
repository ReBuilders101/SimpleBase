package lb.simplebase.net;

import java.util.function.BiConsumer;

/**
 * This interface represents an object that can receive packets.
 */
@Deprecated
public interface PacketReceiver extends BiConsumer<Packet, TargetIdentifier>{

	/**
	 * Default implementation of the {@link BiConsumer#accept(Object, Object)} method, 
	 * so this interface can be used as a {@link BiConsumer}.
	 * @param received The packet that should be processed by this {@link PacketReceiver}
	 * @param source The source that sent the packet
	 */
	@Override
	public default void accept(Packet received, TargetIdentifier source) {
		processPacket(received, source);
	}

	/**
	 * Implementations should handle the received packet, possibly depending on the source of the packet
	 * @param received The packet that should be processed by this {@link PacketReceiver}
	 * @param source The source that sent the packet
	 */
	public void processPacket(Packet received, TargetIdentifier source);
	
	/**
	 * Creates a {@link PacketReceiver} that does nothing when receiving a packet.
	 * Can be used as a dummy default packet handler.
	 * @return The empty packet receiver
	 */
	public static PacketReceiver createEmptyReceiver() {
		return (r, s) -> {};
		//return new PacketReceiverEmptyImpl();
	}
}
