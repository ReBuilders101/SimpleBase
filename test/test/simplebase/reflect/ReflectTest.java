package test.simplebase.reflect;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.reflect.Parameters;
import lb.simplebase.reflect.QuickReflectionUtils;
import lb.simplebase.util.ReflectedMethodNE;

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
		int refPriv = QuickReflectionUtils.Fields.getField(TestClass.class, "priv1", test, int.class);
		assertEquals(priv1, refPriv);
		int refPub = QuickReflectionUtils.Fields.getField(TestClass.class, "pub1", test, int.class);
		assertEquals(test.pub1, refPub);
		String result = QuickReflectionUtils.Methods.executeMethod(TestClass.class, "getTestString", test, Parameters.empty(), String.class);
		assertEquals(ret1, result);
		ReflectedMethodNE testMethod = OldReflectionUtils.getStaticMethodExecutor(TestClass.class, "calc", int.class, int.class).wrapNull();
		assertNotNull(testMethod);
		int a = random.nextInt(1000);
		int b = random.nextInt(1000);
		int c = random.nextInt(1000);
		int res1 = a * b;
		int res2 = a - b + c;
		assertEquals(res1, testMethod.getOrExecute(a, b));
		int refRes2 = QuickReflectionUtils.Methods.executeStaticMethod(TestClass.class, "calc", Parameters.allOf(int.class, a, b, c), int.class);
		assertEquals(res2, refRes2);
	}

}
