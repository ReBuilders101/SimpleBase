package test.simplebase.net;

import java.util.Arrays;

import lb.simplebase.net.Packet;
import lb.simplebase.net.ReadableByteData;
import lb.simplebase.net.WriteableByteData;

public class TestPacket implements Packet{

	private byte[] byteData;
	private int length;
	
	public TestPacket(byte[] data) {
		byteData = data;
		length = data.length;
	}
	
	public TestPacket() {}
	
	@Override
	public void writeData(WriteableByteData data) {
		data.writeInt(length);
		data.write(byteData);
	}

	@Override
	public void readData(ReadableByteData data) {
		length = data.readInt();
		byteData = data.read(length);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(byteData);
		result = prime * result + length;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestPacket other = (TestPacket) obj;
		if (!Arrays.equals(byteData, other.byteData))
			return false;
		if (length != other.length)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestPacket [byteData=" + Arrays.toString(byteData) + ", length=" + length + "]";
	}

}
