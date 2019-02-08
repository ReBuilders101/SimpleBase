package lb.simplebase.binfile;

import lb.simplebase.net.ReadableByteData;

/**
 * A class that can be used to read different data types from a byte array
 */
public class ByteArrayReader implements ReadableByteData{

	public ByteArrayReader(byte[] data) {
		this(data, 0);
	}
	
	public ByteArrayReader(byte[] data, int startIndex) {
		this.data = data;
		this.index = 0;
	}
	
	private byte[] data;
	private int index;
	
	@Override
	public byte[] getAsArray() {
		return data;
	}

	@Override
	public byte readByte() {
		return data[index++];
	}

	@Override
	public void skip(int amount) {
		index += amount;
	}
	
	public void jump(int index) {
		this.index = index;
	}

}
