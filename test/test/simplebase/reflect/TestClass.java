package test.simplebase.reflect;

@SuppressWarnings("unused")
public class TestClass {
	
	private int priv1;
	private String ret1;
	public int pub1;
	
	private static String test = "test";
	
	public TestClass(int priv1, int pub1, String ret1) {
		this.priv1 = priv1;
		this.pub1 = pub1;
		this.ret1 = ret1;
	}
	
	private String getTestString() {
		return ret1;
	}
	
	private static int calc(int a, int b) {
		return a * b;
	}
	
	private static int calc(int a, int b, int c) {
		return a - b + c;
	}
	
	
	

}
