package lb.simplebase.io;

import java.util.Arrays;

/**
 * A byte buffer that has a fixed capacity. Writing over the capacity produces an {@link IndexOutOfBoundsException}
 * Reading further than data has been written will return zeros
 */
public class FixedByteDataBuffer implements ReadableByteData, WritableByteData{

	private final byte[] data;
	private int readPointer;
	private int writePointer;
	
	public FixedByteDataBuffer(int capacity) {
		data = new byte[capacity];
		readPointer = 0;
		writePointer = 0;
	}
	
	@Override
	public byte[] getAsArray() {
		return Arrays.copyOf(data, data.length);
	}

	@Override
	public byte[] getAsArrayFast() {
		return data;
	}

	@Override
	public void writeByte(byte b) {
		data[writePointer++] = b;
	}

	@Override
	public byte readByte() {
		return data[readPointer++]; //post-increment
	}

	@Override
	public void skip(int amount) {
		readPointer+=amount;
	}

	@Override
	public boolean canRead() {
		return readPointer < data.length;
	}

}
