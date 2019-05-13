package lb.simplebase.io;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteNioBuffer implements ReadableByteData, WritableByteData{

	private ByteBuffer buf;
	
	@Override
	public byte[] getAsArray() {
		if(buf.hasArray()) {
			byte[] array = buf.array();
			return Arrays.copyOf(array, array.length);
		}
		return directBufferData();
	}

	@Override
	public void writeByte(byte b) {
		buf.put(b);
	}

	@Override
	public byte readByte() {
		return buf.get();
	}

	@Override
	public void skip(int amount) {
		buf.position(buf.position() + amount);
	}

	@Override
	public boolean canRead() {
		return buf.hasRemaining();
	}

	@Override
	public byte[] getAsReadOnlyArray() {
		if(buf.hasArray()) return buf.array();
		return directBufferData();
	}
	
	public ByteBuffer getNioBuffer() {
		return buf;
	}
	
	private byte[] directBufferData() {
		ByteBuffer copy = (ByteBuffer) buf.duplicate().clear();
		byte[] data = new byte[copy.remaining()];
		copy.get(data);
		return data;
	}

}
