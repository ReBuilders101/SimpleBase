package lb.simplebase.linalg;

public class GlobalBuffer {

	private static ThreadLocal<double[]> buffer;
	
	public static double[] get() {
		return buffer.get();
	}
	
	public static void init(int dimension) {
		if(buffer == null) {
			initAllThreads(dimension);
		} else {
			buffer.set(new double[dimension]);
		}
	}
	
	public static void initAllThreads(int dimension) {
		buffer = ThreadLocal.withInitial(() -> new double[dimension]);
	}
	
}
