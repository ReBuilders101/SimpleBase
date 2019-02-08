package lb.simplebase.binfile;

import lb.simplebase.net.Packet;
import lb.simplebase.net.ReadableByteData;
import lb.simplebase.net.WriteableByteData;

public class ByteDataNodeType implements Packet{

	public ByteDataNodeType(int length) {
		this.length = length;
		this.data = new byte[length];
	}
	
	private byte[] data;
	private int length;
	
	@Override
	public void writeData(WriteableByteData data) {
		data.write(this.data);
	}

	@Override
	public void readData(ReadableByteData data) {
		this.data = data.read(length);
	}

}
