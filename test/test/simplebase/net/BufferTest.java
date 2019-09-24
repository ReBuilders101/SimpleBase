package test.simplebase.net;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.io.ReadableArrayData;
import lb.simplebase.io.WritableStreamData;

class BufferTest {

	WritableStreamData writeBuffer;
	ReadableArrayData readBuffer;
	
	@BeforeEach
	void setUp() throws Exception {
		writeBuffer = new WritableStreamData();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testBufferReadWrite() {
		byte b1 = 123;
		short s1 = -4;
		int i1 = 57234986;
		long l1 = 342986344738L;
		
		writeBuffer.writeByte(b1);
		writeBuffer.writeShort(s1);
		writeBuffer.writeInt(i1);
		writeBuffer.writeLong(l1);
		
		readBuffer = new ReadableArrayData(writeBuffer.getAsArray(), false);
		
		byte b2 = readBuffer.readByte();
		short s2 = readBuffer.readShort();
		int i2 = readBuffer.readInt();
		long l2 = readBuffer.readLong();
		
		assertEquals(b1, b2, "Bytes not equal");
		assertEquals(s1, s2, "Shorts not equal");
		assertEquals(i1, i2, "Ints not equal");		
		assertEquals(l1, l2, "Longs not equal");
	}
	
	@Test
	void testBufferToArray() {
		final byte[] data = new byte[] { 2, 34, (byte) 255, 96};
		writeBuffer.write(data);
		
		byte[] newData = writeBuffer.internalArray();
		assertArrayEquals(data, newData, "Arrays not equal");
	}

}
