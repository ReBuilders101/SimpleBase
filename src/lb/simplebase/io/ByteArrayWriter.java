package lb.simplebase.io;

import java.io.ByteArrayOutputStream;

public class ByteArrayWriter implements WritableByteData{

	private ByteArrayOutputStream baos;
	
	public ByteArrayWriter() {
		baos = new ByteArrayOutputStream();
	}
	
	@Override
	public byte[] getAsArray() {
		return baos.toByteArray();
	}

	@Override
	public void writeByte(byte b) {
		baos.write(b);
	}

	@Override
	public byte[] getAsArrayFast() {
		return baos.toByteArray(); //BAOS internal buf variable is not accessible
	}

}
