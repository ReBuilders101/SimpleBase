package lb.simplebase.net.done;

/**
 * This {@link Exception} is thrown when the {@link PacketIdMapping} for a packet id or packet could not be found.
 */
public class PacketMappingNotFoundException extends Exception{
	
	private static final long serialVersionUID = 1737199470976685720L;
	
	private final Packet packet;
	private final Integer id;
	
	/**
	 * Creates a new {@link PacketMappingNotFoundException}. The id ({@link #getId()}) will be <code>null</code>.
	 * @param message The error message
	 * @param packet The packet for which the {@link PacketIdMapping} was not found
	 */
	public PacketMappingNotFoundException(String message, Packet packet) {
		super(message);
		this.packet = packet;
		this.id = null;
	}
	
	/**
	 * Creates a new {@link PacketMappingNotFoundException}. The packet ({@link #getPacket()}) will be <code>null</code>.
	 * @param message The error message
	 * @param id The packet id for which the {@link PacketIdMapping} was not found
	 */
	public PacketMappingNotFoundException(String message, int id) {
		super(message);
		this.packet = null;
		this.id = id;
	}
	
	/**
	 * The class of the {@link Packet} implementation for which the {@link PacketIdMapping} was not found.
	 * @return The class of the {@link Packet}
	 * @throws NullPointerException When the packet for this exception is <code>null</code>
	 * @see #hasPacket()
	 */
	public Class<? extends Packet> getPacketClass() throws NullPointerException {
		return packet.getClass();
	}
	
	/**
	 * The packet for this exception, or <code>null</code> if no packet was set
	 * @return The packet for this exception
	 * @see #hasPacket()
	 */
	public Packet getPacket() {
		return packet;
	}
	
	/**
	 * Whether this exception contains a {@link Packet}. {@link PacketMappingNotFoundException}s contain either the
	 * {@link Packet} for which a {@link PacketIdMapping} was not found, or the id for which the mapping was not found. 
	 * @return Whether this exception contains a {@link Packet} 
	 * @see #hasPacket()
	 * @see #hasId()
	 */
	public boolean hasPacket() {
		return packet != null;
	}
	
	/**
	 * The packet id for this exception, or <code>null</code> if no id was set
	 * @return The packet id for this exception
	 * @see #hasId()
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * Whether this exception contains a packet id. {@link PacketMappingNotFoundException}s contain either the
	 * {@link Packet} for which a {@link PacketIdMapping} was not found, or the id for which the mapping was not found. 
	 * @return Whether this exception contains a {@link Packet} 
	 * @see #hasPacket()
	 * @see #hasId()
	 */
	public boolean hasId() {
		return id != null;
	}
	
	/**
	 * The packet id for this exception as a primitive type
	 * @return The packet id for this exception
	 * @throws NullPointerException If no packet id was set for this exception
	 * @see #hasId()
	 */
	public int getIdAsInt() throws NullPointerException {
		return id;
	}
	
}
