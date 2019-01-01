package lb.simplebase.net;

/**
 * A packet represents data that can be sent through a network connection.<br>
 * To allow deserialization to happen, every implementing class <b>must</b> have a constructor with no parameters.
 * @see PacketReceiver
 * @see PacketSender
 */
public interface Packet {
	
	/**
	 * Write all of the packet's data into the {@link WriteableByteData} instance.
	 * @param data The {@link WriteableByteData} that accepts the data
	 */
	public void writeData(WriteableByteData data);
	
	/**
	 * Read all of this packet's data from the provided {@link ReadableByteData} instance.
	 * @param data The {@link ReadableByteData} that provides the data
	 */
	public void readData(ReadableByteData data);
	
}
