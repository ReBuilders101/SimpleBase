package test.simplebase.log;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.log.LogHelper;
import lb.simplebase.log.Logger;

class LogTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		List<String> list = new ArrayList<>();
		list.add("String1");
		Stream<String> stream1 = list.stream();
		list.add("String2");
		Stream<String> stream2 = list.stream();
		System.out.println(stream1.count());
		System.out.println(stream2.count());
		
		Logger testLogger = LogHelper.create(getClass());
		Exception ex = new Exception("TestEx");
		ex.fillInStackTrace();
		//ex.printStackTrace();
		testLogger.info("Hi");
		testLogger.warn("TestWarning");
		testLogger.error("TestError", ex);
		
		assertTrue(true);
	}

}
