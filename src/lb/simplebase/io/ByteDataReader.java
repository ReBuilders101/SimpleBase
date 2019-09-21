package lb.simplebase.io;

import java.util.Arrays;

/**
 * A class that can be used to read different data types from a byte array
 */
public class ByteDataReader implements ReadableByteData{

	public ByteDataReader(byte[] data) {
		this(data, 0);
	}
	
	public ByteDataReader(byte[] data, int startIndex) {
		this.data = data;
		this.index = 0;
	}
	
	private byte[] data;
	private int index;
	
	@Override
	public byte[] getAsArray() {
		return Arrays.copyOf(data, data.length);
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

	@Override
	public boolean canRead() {
		return index < data.length;
	}

	@Override
	public byte[] getAsArrayFast() {
		return data;
	}

}
