package lb.simplebase.net;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link PacketTypeDistributor} acts as a {@link PacketReceiver} that distributes the handled {@link Packet}s
 * to other {@link PacketReceiver}s, depending on the type of the handled {@link Packet}.<p>
 * 
 * For example, this allows you to hand an <code>ButtonPressedPacket</code> (example class) that implements {@link Packet} to
 *  the <code>UIHandler</code> (example class) that implements {@link PacketReceiver}, while other Packet types are handed to other
 *  {@link PacketReceiver}s.
 */
public class PacketTypeDistributor implements PacketReceiver{

	private final Map<Class<? extends Packet>, PacketReceiver> map;
	
	/**
	 * Creates a new instance of a {@link PacketTypeDistributor}. No handlers are registered for this newly created {@link PacketTypeDistributor},
	 * except for the class <code>IPacket.class</code>, which points to the default receiver in the parameter. This default receiver will only be called when
	 * no handler was found for a Packet type.
	 * @param defaultReceiver The default {@link PacketReceiver}
	 */
	public PacketTypeDistributor(PacketReceiver defaultReceiver) {
		map = new HashMap<>();
		map.put(Packet.class, defaultReceiver); //default fallback entry
	}
	
	/**
	 * Registers a new {@link PacketReceiver} as a handler for a certain type of packet.
	 * Previously created entries for a type can not be changed. Trying to override an existing entry
	 * with this method will fail.<br>
	 * Note: {@link PacketReceiver} can be used as a <i>functional interface</i>.
	 * @param packetClass The class of the {@link Packet} implementation for which the handler should be registered
	 * @param handler The {@link PacketReceiver} that handles this type of packet
	 * @return Whether the registration was successful or not
	 */
	public boolean registerTypeHandler(Class<? extends Packet> packetClass, PacketReceiver handler) {
		if(map.containsKey(packetClass)) {
			return false; //Don't overwrite anything (including default handler)
		} else {
			map.put(packetClass, handler); //add entry
			return true;
		}
	}
	
	/**
	 * The default {@link PacketReceiver} for this {@link PacketTypeDistributor}. The default packet handler will receive
	 * packets when no handler could be found for the type of the packet.
	 * @return The default {@link PacketReceiver}
	 */
	public PacketReceiver getDefaultReceiver() {
		return map.get(Packet.class);
	}
	
	/**
	 * Processes a packet. The packet will be handed to another {@link PacketReceiver} registered for the type of the packet if possible,
	 * or to the default {@link PacketReceiver}.
	 * @param received The packet that was received and should be handled.
	 * @param source The source of the packet
	 * @see #getDefaultReceiver()
	 */
	@Override
	public void processPacket(Packet received, PacketContext source) {
		PacketReceiver receiver = map.get(received.getClass()); //class of the packet implementation, not necessarily IPacket.class
		if(receiver == null) {
			getDefaultReceiver().processPacket(received, source); //default if no mapping is found
		} else {
			receiver.processPacket(received, source);
		}
	}
}
