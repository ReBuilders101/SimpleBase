package lb.simplebase.net;

/**
 * A packet represents data that can be sent through a network connection.<br>
 * @see PacketReceiver
 * @see PacketSender
 */
public interface Packet {
	
	/**
	 * Write all of the packet's data into the {@link WritableByteData} instance.
	 * The maximum length of a packet is 2147483647 bytes ({@link Integer#MAX_VALUE})
	 * @param data The {@link WritableByteData} that accepts the data
	 */
	public void writeData(WritableByteData data);
	
	/**
	 * Read all of this packet's data from the provided {@link ReadableByteData} instance.
	 * @param data The {@link ReadableByteData} that provides the data
	 */
	public void readData(ReadableByteData data);
	
	/**
	 * Creates a new Instance of this packet type without any content.
	 * @return A new Packet instance
	 */
//	public Packet createEmptyInstance();
}
