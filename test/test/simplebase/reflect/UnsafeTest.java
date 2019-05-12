package test.simplebase.reflect;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.reflect.FixedSizeObject;
import lb.simplebase.reflect.MemoryNotAllocatedException;
import lb.simplebase.reflect.OffHeapArray;
import lb.simplebase.reflect.UnsafeUtils;

class UnsafeTest {
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void getUnsafeTest() {
		assertTrue(UnsafeUtils.hasUnsafe());
	}
	
	@Test
	void byteArrayTest() {
		OffHeapArray<Byte> data = null;
		try {
			data = UnsafeUtils.createOffHeapArray(5, FixedSizeObject.BYTE);
			data.set(3, (byte) 23);
			data.set(4, (byte) -45);
			data.set(0, (byte) 11);
			
			assertEquals((byte) 23, data.get(3).byteValue());
			assertEquals((byte) -45, data.get(4).byteValue());
			assertEquals((byte) 11, data.get(0).byteValue());
			
			final OffHeapArray<Byte> dataF = data;
			assertThrows(ArrayIndexOutOfBoundsException.class, () -> dataF.set(67, (byte) 56));
			assertThrows(ArrayIndexOutOfBoundsException.class, () -> dataF.get(-45));
			
			data.freeMemory();
			assertFalse(data.isAllocated());
			assertThrows(MemoryNotAllocatedException.class, () -> dataF.get(3));
			
		} finally {
			if(data != null) data.freeMemory();
		}	
	}

	static final int arraytestsize = 10000;
	
	@Test
	void largeIntArrayTest() {
		OffHeapArray<Integer> data = null;
		try {
			data = UnsafeUtils.createOffHeapArray(arraytestsize, FixedSizeObject.INTEGER);
			int[] dataCopy = new int[arraytestsize];
			
			Random random = new Random();
			
			//Fill with data
			for(int i = 0; i < arraytestsize; i++) {
				int val = random.nextInt(100);
				data.set(i, val);
				dataCopy[i] = val;
			}
			
			for(int j = 0; j < arraytestsize; j++) {
				assertEquals(dataCopy[j], (int) data.get(j), "Element " + j + " not equal");
			}
			
		} finally {
			if(data != null) data.freeMemory();
		}
	}
}
