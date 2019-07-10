package lb.simplebase.net;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A {@link PacketIdMapping} maps a class that extends {@link Packet} to an integer id,
 * and provides a method to create empty instances of the Packet subclass without using reflection.
 */
public interface PacketIdMapping {
	
	/**
	 * A blank instance of the {@link Packet} sublass available through {@link #getPacketClass()}.<br>
	 * {@link #getPacketClass()} should be equal to {@link #getNewInstance()}<code>.getClass()</code>
	 * @return A blank instance of the {@link Packet} sublass available through {@link #getPacketClass()}
	 */
	public Packet getNewInstance();
	
	/**
	 * The integer id of this {@link Packet} subclass. The id us used in network traffic to transmit the class of the packet.
	 * @return The integer id of this {@link Packet} subclass
	 */
	public int getPacketId();
	
	/**
	 * The class of the {@link Packet} implemenation that this mapping is for.
	 * @return The class of the {@link Packet} implemenation
	 */
	public Class<? extends Packet> getPacketClass();
	
	/**
	 * Creates a new {@link PacketIdMapping} with these values. The id should be unique to this packet implementation.
	 * The {@link Supplier} <b>must</b> return a new instance for every call.
	 * @param <T> The type of the {@link Packet} implementation
	 * @param id The integer id of this {@link Packet} type
	 * @param clazz The class of this {@link Packet} implementation
	 * @param newInstance A {@link Supplier} that generates new instances of this packet implementation
	 * @return The created {@link PacketIdMapping}
	 */
	public static <T extends Packet> PacketIdMapping create(final int id, final Class<T> clazz, final Supplier<T> newInstance) {
		return new PacketIdMapping() {
			
			@Override
			public int getPacketId() {
				return id;
			}
			
			@Override
			public Class<? extends Packet> getPacketClass() {
				return clazz;
			}
			
			@Override
			public Packet getNewInstance() {
				return newInstance.get();
			}
			
		};
	}
	
	/**
	 * Creates a {@link Predicate} for {@link PacketIdMapping}s that test if the packet id is equal to the id in the parameter.
	 * @param id The packet id that should be matched
	 * @return The {@link Predicate} that matches the packet id;
	 */
	public static Predicate<? super PacketIdMapping> idMatcher(final int id) {
		return (m) -> m.getPacketId() == id;
	}
	
	/**
	 * Creates a {@link Predicate} for {@link PacketIdMapping}s that test if the packet {@link Class} is equal to the class in the parameter.
	 * @param clazz The packet class that should be matched
	 * @return The {@link Predicate} that matches the packet class;
	 */
	public static Predicate<? super PacketIdMapping> classMatcher(final Class<? extends Packet> clazz) {
		return (m) -> m.getPacketClass() == clazz;
	}
}
