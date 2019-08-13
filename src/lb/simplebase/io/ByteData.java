package lb.simplebase.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * All data contained in an implementing object can be represented as a byte array, available through the {@link #getAsArray()} method.
 * The {@link ByteData}-object is neither readable or writable directly, but it contains methods to be converted into a readable or
 * writable IO Stream.<br>
 * Not threadsafe.
 */
public interface ByteData {
	
	/**
	 * Gets all (relevant) data of the object as a byte array.<br>
	 * <b>If only read access is required, use {@link #getAsArrayFast()} instead.</b><br>
	 * The created array may change form call to call, depending on the state of the object,
	 * however the returned array should never change, so it must be copied before returning it.
	 * In case of nested arrays, a deep copy is not necessary.<br>
	 * <b>Changes to this array must not affect this ByteData object.</b>
	 * @return The byte array
	 */
	public byte[] getAsArray();
	
	/**
	 * Gets all (relevant) data of the object as a byte array.<br>
	 * <b>If writing to the array is required, use {@link #getAsArray()} instead.</b><br>
	 * The created array may change form call to call, depending on the state of the object,
	 * and can, depending on the implementation, be either the array backing this ByteData implementation, or be a flat copy of this array.
	 * <b>Changes to this array may affect this ByteData object, but this behavior is not guaranteed.</b>
	 * @return The byte array
	 */
	public byte[] getAsArrayFast();
	
	/**
	 * Creates a new {@link ByteArrayInputStream} backed by the byte array returned by {@link #getAsArray()} at the time this
	 * method is called. Because {@link #getAsArray()} <i>should</i> return a new copy of the array for every call, the data in
	 * the returned {@link ByteArrayInputStream} never changes (If {@link #getAsArray()} follows implementation rules).
	 * @return A {@link ByteArrayInputStream} containing the same data as currently returned by {@link #getAsArray()}.
	 */
	public default ByteArrayInputStream getAsReadableIOStream() {
		return new ByteArrayInputStream(getAsArray()); //Cannot use getAsReadOnlyArray, because param is stored in BAIS object
	}
	
	/**
	 * Creates a writeable {@link ByteArrayOutputStream} that contains the byte data returned byt {@link #getAsArray()} at the time
	 * this method is called. By writing the current byte data to the initially empty {@link ByteArrayOutputStream}, all values are
	 * copied.<br><b>This means that changes to the returned stream will not cause changes to this object.</b>
	 * @return A {@link ByteArrayOutputStream} containing the byte data currently returned by {@link #getAsArray()};
	 */
	public default ByteArrayOutputStream getAsWriteableIOStream(){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(getAsArrayFast()); //Data is copied in BAOS
		} catch (IOException e) {
			e.printStackTrace(); //This cannot happen. Really. I promise (see VVV). Added a log call anyways.
			//Not happening because:
			//Called method: 			OutputStream#write(byte[]) throws IOException
			//This method calls:		OutputStream#write(byte[], int, int) throws IOException
			//This is reimplemented in type
			//ByteArrayOutputStream:	BAOS#write(byte[], int, int) WITHOUT THROWING A CHECKED EXCEOTION.
		}
		return baos;
	}
	
	/**
	 * The length of the byte data, which is the length of the byte array returned by {@link #getAsArray()}. 
	 * @return The length of the byte data
	 */
	public default int getLength() {
		return getAsArrayFast().length;
	}
	
//	
//	public void writeByte(byte b);
//	public void writeChar(char b);
//	public void writeInt(int b);
//	public void writeLong(long b);
//	public void writeFloat(float b);
//	public void writeDouble(double b);
//	public void write(byte[] data);
//	
//	public void writeString(CharSequence cs);
//	public void writeStringWithLength(CharSequence cs);
//	public String readString(int length);
//	public String readStringWithLength();
}
