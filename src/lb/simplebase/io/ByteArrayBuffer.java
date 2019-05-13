package lb.simplebase.io;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * An object that stores byte data and is both readable and writeable.
 * All read methods throw {@link ArrayIndexOutOfBoundsException}s when the read pointer is larger than the size of the array.
 * <br><b>Not Threadsafe.</b>
 */
public class ByteArrayBuffer implements WritableByteData, ReadableByteData{

	private ByteArrayOutputStream writeData;
	private byte[] readData;
	private int readPointer;
	private boolean readInvalid;
	
	/**
	 * Creates a new {@link ByteArrayBuffer} without any data
	 */
	public ByteArrayBuffer() {
		writeData = new ByteArrayOutputStream();
		readPointer = 0;
		readInvalid = true; //Invalid, so readData will be initialized on first access
	}
	
	/**
	 * Creates a new {@link ByteArrayBuffer} that initially contains the byte data in the parameter
	 * @param data The data that will be initially contained
	 */
	public ByteArrayBuffer(ByteData data) {
		writeData = data.getAsWriteableIOStream(); //Use stream with data already contained
		readPointer = 0;
		readInvalid = true; //Invalid, so readData will be initialized on first access
	}
	
	/**
	 * Creates a new {@link ByteArrayBuffer} that initially contains the byte data in the parameter
	 * @param data The data that will be initially contained
	 */
	public ByteArrayBuffer(byte[] data) {
		this();
		write(data);
	}
	
	/**
	 * Creates a new byte array containing the byte data of this {@link ByteArrayBuffer}.<br>
	 * Changes to the returned array will <b>not</b> change this {@link ByteArrayBuffer}, and changes
	 * to this {@link ByteArrayBuffer} will <b>not</b> affect the returned array.
	 * @return An array containing the byte data of this {@link ByteArrayBuffer}
	 */
	@Override
	public byte[] getAsArray() {
		updateRead();
		return Arrays.copyOf(readData, readData.length);
	}

	/**
	 * Reads a single byte at the position of the read pointer and increases the read pointer by 1.
	 * @return The next <code>byte</code>
	 * @see #getReadPointer()
	 */
	@Override
	public byte readByte() {
		if(!canRead()) { //Test against write size, so in case of error array isn't copied
			throw new IndexOutOfBoundsException("Read pointer: " + readPointer + ", byte data length: " + writeData.size());
		} else {
			updateRead();
			return readData[readPointer++]; //Increment after passing pointer
		}
	}

	/**
	 * Increases the read pointer by <i>amount</i>.
	 * @param amount The amount that the read pointer should be increased by
	 * @see #getReadPointer()
	 */
	@Override
	public void skip(int amount) {
		readPointer += amount;
	}

	/**
	 * Sets the read pointer to zero; the beginning of the array.
	 * @see #getReadPointer()
	 */
	public void resetReadPointer() {
		readPointer = 0;
	}
	
	/**
	 * The read pointer is the index of the byte in the array returned by {@link #getAsArray()} that will be read on the next
	 * call of {@link #readByte()}. The read pointer increases by one after reading one byte, so consecutive calls read consecutive bytes.
	 * @return The position of the read pointer
	 * @see #resetReadPointer()
	 * @see #skip(int)
	 */
	public int getReadPointer() {
		return readPointer;
	}
	
	/**
	 * Writes a single <code>byte</code> value at the end of the current byte sequence. 
	 * @param b The <code>byte</code> that should be written
	 */
	@Override
	public void writeByte(byte b) {
		writeData.write(b);
		readInvalid = true;
	}

	/**
	 * The length of the byte data, which is equal to the length of the byte array returned by {@link #getAsArray()}.
	 * @return The length of the byte data
	 */
	@Override
	public int getLength() {
		//Reimplementation does not require a call to updateRead()
		return writeData.size();
	}

	/**
	 * Update the read array when new data has been written 
	 */
	private void updateRead() {
		if(readInvalid) {
			readData = writeData.toByteArray();
		}
		readInvalid = false;
	}
	
	/**
	 * Writes all byte data from another {@link ByteArrayBuffer} into this {@link ByteArrayBuffer}.
	 * @param buf The {@link ByteArrayBuffer} containing the data
	 */
	public void write(ByteData buf) {
		final byte[] data = buf.getAsReadOnlyArray();
		write(data);
	}

	@Override
	public boolean canRead() {
		return readPointer < writeData.size();
	}

	@Override
	public byte[] getAsReadOnlyArray() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
