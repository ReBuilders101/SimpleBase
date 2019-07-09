package lb.simplebase.net;

import java.util.Arrays;

import lb.simplebase.io.ByteArrayBuffer;
import lb.simplebase.net.done.AbstractNetworkConnection;

/**
 * Encodes and decodes packets from / to bytes
 * <p>
 * A {@link Packet} will be converted to:
 * <ul>
 * <li>packet header ({@link #PACKET_HEADER}): 4 bytes</li>
 * <li>packet id ({@link PacketIdMapping#getPacketId()}): 4 bytes</li>
 * <li>packet data length: 4 bytes</li>
 * <li>packet data: custom length</li>
 * </ul>
 */
public class PacketFactory {
	
	private static final byte[] PACKET_HEADER_PRIV = {(byte) 0xFF, (byte) 0xF0, (byte) 0x0F, (byte) 0x00};
	
	/**
	 * The 4 byte header at the start of a packet:<br>
	 * <code>
	 * 0xFF,
	 * 0xF0,
	 * 0x0F,
	 * 0x00
	 * </code>
	 * <br>
	 * <b>These values must not be changed!</b><br>
	 * Changes to these values will have no effect on packet encoding, but may cause errors with other classes depending on these values.
	 */
	public static final byte[] PACKET_HEADER = Arrays.copyOf(PACKET_HEADER_PRIV, PACKET_HEADER_PRIV.length);
	
	private final PacketIdMappingContainer mapCon;
	private final AbstractNetworkConnection finishedPacketReceiver;
	
	private Mode mode = Mode.SEARCH_HEADER; 
	private int accStep = 0;
	private byte[] tempData = new byte[4];
	private int packetId = 0;
	
	/**
	 * Creates a new {@link PacketFactory} for this {@link AbstractNetworkConnection}.
	 * @param mapCon The {@link PacketIdMappingContainer} that contains all packet &lt;-&gt; id mappings
	 * @param finishedPacketReceiver The {@link AbstractNetworkConnection} that will receive finished {@link Packet}s
	 */
	public PacketFactory(PacketIdMappingContainer mapCon, AbstractNetworkConnection finishedPacketReceiver) {
		this.mapCon = mapCon;
		this.finishedPacketReceiver = finishedPacketReceiver;
	}
	
	/**
	 * Feed a byte of data to construct the packet from.
	 * @param data The new byte that was received
	 * @throws PacketMappingNotFoundException If a packet was completed, and the id was not found
	 */
	public void feed(byte data) throws PacketMappingNotFoundException { //I hate decoding bytes
		
		//First save data and increase accumulation counter
		tempData[accStep] = data; //save data in current step
		if(mode == Mode.SEARCH_HEADER) { //Special case for header, because ti doesnt accept any data
			if(data == PACKET_HEADER_PRIV[accStep]) {
				accStep++; //Only increase header finding if data is correct
			} else {
				accStep = 0; //If one byte is not correct, completely reset
			}
		} else { //for other modes increase counter
			accStep++; 
		}
		
		if(accStep >= mode.getAccumulateLimit()) { //If this step is done
			//The next step depends on the current mode
			switch (mode) {
			case SEARCH_HEADER: //header found
				mode = Mode.ACC_PACKETID; //read packetId next, nothing must be saved
				break;
			case ACC_PACKETID: //packetId done
				//save the packetId
				packetId = parseInt(tempData); //Read it from tempdata
				mode = Mode.ACC_DATALEN; //read datalength next
				break;
			case ACC_DATALEN: //datalength has been read
				final int datalen = parseInt(tempData); //read data legth from tempData
				Mode.setDataLimit(datalen); //set the limit for Mode.ACC_DATA
				tempData = new byte[datalen]; //reDim the data array
				mode = Mode.ACC_DATA; //read data next
				break;
			case ACC_DATA: //data has been read
				makePacket(); //make a packet and send it
				tempData = new byte[4]; //ReDim array
				mode = Mode.SEARCH_HEADER; //start searching for packets again
				break;
			default: //That's not gonna happen
				mode = Mode.SEARCH_HEADER; //complete reset anyways
				break;
			}
			
			accStep = 0; //definitely reset counter, because this is a new step/mode
		}
	}
	
	/**
	 * Create a packet from the current tempData and id
	 * and send it to the connection
	 * @throws PacketMappingNotFoundException When the packetId mapping was not found (duh)
	 */
	private void makePacket() throws PacketMappingNotFoundException {
		ByteArrayBuffer packetData = new ByteArrayBuffer(tempData); //copy packet data
		PacketIdMapping mapping = mapCon.getMappingFor(packetId); //Mapping for id
		if(mapping == null)
			throw new PacketMappingNotFoundException("mapping not found for id while constructing packet", packetId);
		Packet newPacket = mapping.getNewInstance(); //make a packet
		newPacket.readData(packetData); //read the packet data
		finishedPacketReceiver.handleReceivedPacket(newPacket); //send the packet to the connection
	}
	
	/**
	 * The {@link PacketIdMappingContainer} used by this {@link PacketFactory}.
	 * @return The {@link PacketIdMappingContainer} used by this {@link PacketFactory}
	 */
	public PacketIdMappingContainer getMappingContainer() {
		return mapCon;
	}
	
	/**
	 * Converts a {@link Packet} into bytes, including header and metadata
	 * @param packet The {@link Packet} to convert
	 * @return The bytes repesenting the packet
	 * @throws PacketMappingNotFoundException When the packet class cloud not be converted into an id
	 */
	public byte[] createPacketData(Packet packet) throws PacketMappingNotFoundException {
		final ByteArrayBuffer packetData = new ByteArrayBuffer();
		packet.writeData(packetData); //Write packet data
		//create new buffer that also has metadata
		final int packetDataLength = packetData.getLength();
		if(!mapCon.hasMappingFor(packet.getClass()))
			throw new PacketMappingNotFoundException("No mapping was found when trying to send packet", packet);
		final int packetId = mapCon.getMappingFor(packet.getClass()).getPacketId(); //The mapping must exist, otherwise ^^
		final ByteArrayBuffer allData = new ByteArrayBuffer();
		//Write data to buffer
		allData.write(PACKET_HEADER_PRIV);
		allData.writeInt(packetId);
		allData.writeInt(packetDataLength);
		allData.write(packetData);
		//create array
		return allData.getAsReadOnlyArray();
	}
	
	protected void notifyConnectionClosed() {
		//Nothing really
	}
	
	private static int parseInt(byte[] bytes) {
		//Code copied from ReadableByteData
		//No enclosing typecast needed, result is already int
		return 	(( (int) bytes[3]) << 24) | //MSB has most left shift
				(( (int) bytes[2]) << 16) |
				(( (int) bytes[1]) << 8 ) |
				(  (int) bytes[0]);		  //LSB not shifted
	}
	
	private static enum Mode {
		SEARCH_HEADER(4), ACC_PACKETID(4), ACC_DATALEN(4), ACC_DATA(0);
		
		private int accumulateLimit;
		
		private Mode(int accLim) {
			accumulateLimit = accLim;
		}
		
		private void setLimit(int limit) {
			accumulateLimit = limit;
		}
		
		public int getAccumulateLimit() {
			return accumulateLimit;
		}
		
		//Mutable enums!!!!
		public static void setDataLimit(int limit) {
			ACC_DATA.setLimit(limit);
		}
	}
}
