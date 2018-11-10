package lb.simplebase.net;

import java.io.ByteArrayInputStream;

public interface IByteData {
	
	public byte[] getAsArray();
	public ByteArrayInputStream getAsStream();
	
	public byte readByte();
	public char readChar();
	public int readInt();
	public long readLong();
	public float readFloat();
	public double readDouble();
	public void skip(int bytes);
	public byte[] read(int length);
	public void read(byte[] toFill);
	public int getLength();
	
	public void writeByte(byte b);
	public void writeChar(char b);
	public void writeInt(int b);
	public void writeLong(long b);
	public void writeFloat(float b);
	public void writeDouble(double b);
	public void write(byte[] data);
	
	public void writeString(CharSequence cs);
	public void writeStringWithLength(CharSequence cs);
	public String readString(int length);
	public String readStringWithLength();
}
