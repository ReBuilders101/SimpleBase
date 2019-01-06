package test.simplebase.net;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.net.ByteBuffer;

class BufferTest {

	ByteBuffer buffer;
	
	@BeforeEach
	void setUp() throws Exception {
		buffer = new ByteBuffer();
	}

	@AfterEach
	void tearDown() throws Exception {
		buffer = null;
	}

	@Test
	void testBufferReadWrite() {
		byte b1 = 123;
		short s1 = -4;
		int i1 = 57234986;
		long l1 = 342986344738L;
		
		buffer.writeByte(b1);
		buffer.writeShort(s1);
		buffer.writeInt(i1);
		buffer.writeLong(l1);
		
		byte b2 = buffer.readByte();
		short s2 = buffer.readShort();
		int i2 = buffer.readInt();
		long l2 = buffer.readLong();
		
		assertEquals(b1, b2, "Bytes not equal");
		assertEquals(s1, s2, "Shorts not equal");
		assertEquals(i1, i2, "Ints not equal");		
		assertEquals(l1, l2, "Longs not equal");
	}
	
	@Test
	void testBufferToArray() {
		final byte[] data = new byte[] { 2, 34, (byte) 255, 96};
		buffer.write(data);
		
		byte[] newData = buffer.getAsArray();
		assertArrayEquals(data, newData, "Arrays not equal");
	}
	
	@Test
	void testBufferReadWriteMixed() {
		int write1 = 651237;
		int write2 = 327;
		byte write3 = (byte) 194;
		
		int read1;
		int read2;
		byte read3;
		
		buffer.writeInt(write1);
		buffer.writeInt(write2);
		
		read1 = buffer.readInt();
		
		buffer.writeByte(write3);
		
		read2 = buffer.readInt();
		read3 = buffer.readByte();
		
		assertEquals(write1, read1, "First ints not equal");
		assertEquals(write2, read2, "Second ints not equal");
		assertEquals(write3, read3, "Third bytes not equal");
	}

}
