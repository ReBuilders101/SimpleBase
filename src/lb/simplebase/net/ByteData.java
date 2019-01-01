package lb.simplebase.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * All data contained in an implementing object can be represented as a byte array, available through the {@link #getAsArray()} method.
 * The {@link ByteData}-object is neither readable or writable directly, but it contains methods to be converted into a readable or
 * writable IO Stream.<br><b>Changes to these streams, including writing byte data, are not reflected in changes to this object.</b>
 * @see #getAsReadableIOStream()
 * @see #getAsWriteableIOStream()
 */
public interface ByteData {
	
	/**
	 * Gets all (relevant) data of the object as a byte array.
	 * The created array may change form call to call, depending on the state of the object,
	 * however the returned array should never change, so it must be copied before returning it.
	 * In case of nested arrays, a depp copy is not necessary. 
	 * @return The byte array
	 */
	public byte[] getAsArray();
	
	/**
	 * Creates a new {@link ByteArrayInputStream} backed by the byte array returned by {@link #getAsArray()} at the time this
	 * method is called. Because {@link #getAsArray()} <i>should</i> return a new copy of the array for every call, the data in
	 * the returned {@link ByteArrayInputStream} never changes (If {@link #getAsArray()} follows implementation rules).
	 * @return A {@link ByteArrayInputStream} containing the same data as currently returned by {@link #getAsArray()}.
	 */
	public default ByteArrayInputStream getAsReadableIOStream() {
		return new ByteArrayInputStream(getAsArray());
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
			baos.write(getAsArray());
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
		return getAsArray().length;
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
