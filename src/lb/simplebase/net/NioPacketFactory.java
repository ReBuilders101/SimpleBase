package lb.simplebase.net;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class NioPacketFactory extends PacketFactory {

	protected NioPacketFactory(PacketIdMappingContainer mapCon, Consumer<Packet> finishedPacketReceiver, NioNetworkConnection connection) {
		super(mapCon, finishedPacketReceiver);
		this.connection = connection;
	}

	protected void feed(ByteBuffer tempBuffer) throws PacketMappingNotFoundException {
		tempBuffer.flip(); //make it ready for reading
		while(tempBuffer.hasRemaining()) {
			feed(tempBuffer.get()); //TODO bulk copy
		}
	}
	
	
	
	//This does not logically belong here, but because NioPacketFactory is the SelectionKey attachment, it is a good way to reverse map SelectionKey -> Connection
	private final NioNetworkConnection connection;
	
	public NioNetworkConnection getConnection() {
		return connection;
	}
	
}
