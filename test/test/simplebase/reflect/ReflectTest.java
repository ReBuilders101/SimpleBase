package test.simplebase.reflect;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.function.ReflectedMethodNE;
import lb.simplebase.reflect.ReflectionUtils;
import lb.simplebase.reflect.Signature;

class ReflectTest {

	TestClass test;
	int priv1;
	String ret1;
	Random random;
	
	@BeforeEach
	void setUp() throws Exception {
		random = new Random();
		priv1 = random.nextInt(1000);
		int pub1 = random.nextInt(1000);
		byte[] strDat = new byte[20];
		random.nextBytes(strDat);
		ret1 = new String(strDat);
		test = new TestClass(priv1, pub1, ret1);
	}

	@AfterEach
	void tearDown() throws Exception {
		test = null;
		ret1 = null;
		priv1 = 0;
		random = null;
	}

	@Test
	void readTest() {
		int refPriv = ReflectionUtils.getDeclaredField(TestClass.class, "priv1", test, int.class);
		assertEquals(priv1, refPriv);
		int refPub = ReflectionUtils.getField(TestClass.class, "pub1", test, int.class);
		assertEquals(test.pub1, refPub);
		String result = ReflectionUtils.executeDeclaredMethod(TestClass.class, "getTestString", test, String.class, Signature.empty());
		assertEquals(ret1, result);
		ReflectedMethodNE testMethod = ReflectionUtils.getStaticMethodExecutor(TestClass.class, "calc", int.class, int.class).wrapNull();
		assertNotNull(testMethod);
		int a = random.nextInt(1000);
		int b = random.nextInt(1000);
		int c = random.nextInt(1000);
		int res1 = a * b;
		int res2 = a - b + c;
		assertEquals(res1, testMethod.getOrExecute(a, b));
		int refRes2 = ReflectionUtils.executeStaticMethod(TestClass.class, "calc", int.class, Signature.allOf(int.class, a, b, c));
		assertEquals(res2, refRes2);
	}

}
