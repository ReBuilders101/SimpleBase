package lb.simplebase.net;

import java.util.Set;

/**
 * A container for {@link PacketIdMapping}s. The mappings are available through the {@link #getAllMappings()} method.
 * Additionally, this interface provides methods to find mappings by class or id.
 */
public interface PacketIdMappingContainer {
	
	/**
	 * Returns all {@link PacketIdMapping}s in this container.
	 * @return All {@link PacketIdMapping}s
	 */
	public Set<PacketIdMapping> getAllMappings();
	
	/**
	 * Finds a {@link PacketIdMapping} by the class of the {@link Packet} implementation.
	 * @param clazz The class of the {@link Packet} implementation
	 * @return The found {@link PacketIdMapping}, or <code>null</code>
	 */
	public default PacketIdMapping getMappingFor(Class<? extends Packet> clazz) {
		return getMappingFor(clazz, null);
	}
	
	/**
	 * Finds a {@link PacketIdMapping} by the class of the {@link Packet} implementation.
	 * @param clazz The class of the {@link Packet} implementation
	 * @param defaultMapping The default {@link PacketIdMapping} that should be returned when no mapping is found
	 * @return The found {@link PacketIdMapping}, or <i>defaultMapping</i>
	 */
	public default PacketIdMapping getMappingFor(Class<? extends Packet> clazz, PacketIdMapping defaultMapping) {
		return getAllMappings().stream().filter(PacketIdMapping.classMatcher(clazz)).findFirst().orElse(defaultMapping);
	}
	
	/**
	 * Finds a {@link PacketIdMapping} by the id of the {@link Packet} implementation.
	 * @param id The id of the {@link Packet} implementation
	 * @return The found {@link PacketIdMapping}, or <code>null</code>
	 */
	public default PacketIdMapping getMappingFor(int id) {
		return getMappingFor(id, null);
	}
	
	/**
	 * Finds a {@link PacketIdMapping} by the id of the {@link Packet} implementation.
	 * @param id The id of the {@link Packet} implementation
	 * @param defaultMapping The default {@link PacketIdMapping} that should be returned when no mapping is found
	 * @return The found {@link PacketIdMapping}, or <i>defaultMapping</i>
	 */
	public default PacketIdMapping getMappingFor(int id, PacketIdMapping defaultMapping) {
		return getAllMappings().stream().filter(PacketIdMapping.idMatcher(id)).findFirst().orElse(defaultMapping);
	}
	
	/**
	 * Tests if a {@link PacketIdMapping} for this class is present in the {@link Set} returned by {@link #getAllMappings()}
	 * @param clazz The class of the {@link Packet} implementation
	 * @return Whether a {@link PacketIdMapping} was found
	 */
	public default boolean hasMappingFor(Class<? extends Packet> clazz) {
		return getAllMappings().stream().anyMatch(PacketIdMapping.classMatcher(clazz));
	}
	
	/**
	 * Tests if a {@link PacketIdMapping} for this id is present in the {@link Set} returned by {@link #getAllMappings()}
	 * @param id The id of the {@link Packet} implementation
	 * @return Whether a {@link PacketIdMapping} was found
	 */
	public default boolean hasMappingFor(int id) {
		return getAllMappings().stream().anyMatch(PacketIdMapping.idMatcher(id));
	}
	
}
