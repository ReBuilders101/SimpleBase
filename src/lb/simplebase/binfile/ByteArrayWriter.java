package lb.simplebase.binfile;

import java.io.ByteArrayOutputStream;

import lb.simplebase.net.WriteableByteData;

public class ByteArrayWriter implements WriteableByteData{

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

}