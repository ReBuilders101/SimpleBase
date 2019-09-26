package lb.simplebase.net;

import java.util.Objects;
import java.util.function.Consumer;

import lb.simplebase.io.ReadableArrayData;
import lb.simplebase.io.WritableFixedData;
import lb.simplebase.io.WritableFixedData.WritableArrayData;
import lb.simplebase.io.WritableStreamData;


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
	
	public static final byte[] PACKETHEADER = {(byte) 0xFF, (byte) 0xF0, (byte) 0x0F, (byte) 0x00};
	
	private final PacketIdMappingContainer mapCon;
	private final Consumer<Packet> finishedPacketReceiver;
	
	protected Mode mode = Mode.SEARCH_HEADER; 
	protected int accStep = 0;
	protected byte[] tempData = new byte[4];
	private int packetId = 0;
	
	/**
	 * Creates a new {@link PacketFactory} for this {@link NetworkConnection}.
	 * @param mapCon The {@link PacketIdMappingContainer} that contains all packet &lt;-&gt; id mappings
	 * @param finishedPacketReceiver The {@link NetworkConnection} that will receive finished {@link Packet}s
	 */
	public PacketFactory(PacketIdMappingContainer mapCon, Consumer<Packet> finishedPacketReceiver) {
		Objects.requireNonNull(mapCon);
		Objects.requireNonNull(finishedPacketReceiver);
		this.mapCon = mapCon;
		this.finishedPacketReceiver = finishedPacketReceiver;
	}
	
	/**
	 * Feed a byte of data to construct the packet from.
	 * @param data The new byte that was received
	 * @throws PacketMappingNotFoundException If a packet was completed, and the id was not found
	 */
	//Sync -> everybody has to wait their turn to give a byte
	public synchronized void feed(byte data) throws PacketMappingNotFoundException { //I hate decoding bytes
		//First save data and increase accumulation counter
		tempData[accStep] = data; //save data in current step
		if(mode == Mode.SEARCH_HEADER) { //Special case for header, because ti doesnt accept any data
			if(data == PACKETHEADER[accStep]) {
				accStep++; //Only increase header finding if data is correct
			} else {
				accStep = 0; //If one byte is not correct, completely reset
			}
		} else { //for other modes increase counter
			accStep++; 
		}
		
		updateState();
	}
	
	protected int getRemainingBytes() {
		return mode.getAccumulateLimit() - accStep;
	}
	
	protected void updateState() throws PacketMappingNotFoundException {
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
		final PacketIdMapping mapping = mapCon.getMappingFor(packetId); //Mapping for id
		if(mapping == null)
			throw new PacketMappingNotFoundException("mapping not found for id while constructing packet", packetId);
		final Packet newPacket = mapping.getNewInstance(); //make a packet
		final ReadableArrayData packetData = new ReadableArrayData(tempData, false);
		newPacket.readData(packetData); //read the packet data
		finishedPacketReceiver.accept(newPacket); //send the packet to the connection
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
	 * @return The bytes repesenting the packet. The array must not be modified
	 * @throws PacketMappingNotFoundException When the packet class cloud not be converted into an id
	 */
	public byte[] createPacketData(Packet packet) throws PacketMappingNotFoundException {
		//First, check for a mapping for the packet class
		if(!mapCon.hasMappingFor(packet.getClass()))
			throw new PacketMappingNotFoundException("No mapping was found when trying to send packet", packet);
		final int packetId = mapCon.getMappingFor(packet.getClass()).getPacketId(); //The mapping must exist, otherwise ^^
		//Then move write packet data to a buffer
		final WritableStreamData packetData = new WritableStreamData();
		packet.writeData(packetData); //Write packet data
		final int packetDataLength = packetData.getLength();
		//Write data to buffer
		final WritableFixedData allData = new WritableArrayData(packetDataLength + 12);
		allData.write(PACKETHEADER);
		allData.writeInt(packetId);
		allData.writeInt(packetDataLength);
		allData.write(packetData.internalArray());
		//create array
		return allData.internalArray();
	}
	
	protected void notifyConnectionClosed() {
		//Nothing really
	}
	
	public static int parseInt(byte[] bytes) {
		if(bytes.length != 4) throw new ArrayIndexOutOfBoundsException("Array must have legth 4");
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
