package lb.simplebase.reflect;

import java.lang.reflect.Field;
import java.security.ProtectionDomain;

import sun.misc.Unsafe;

@RequireUndocumented("sun.misc.Unsafe")
public final class UnsafeUtils {

	private static Unsafe UNSAFE;
	private static boolean errorFlag;
	private static ProtectionDomain pd;
	
	static {
		pd = ReflectionUtils.getField(ClassLoader.class, "defaultDomain", null, ProtectionDomain.class);
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			UNSAFE = (Unsafe) theUnsafe.get(null);
			errorFlag = false;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			UNSAFE = null;
			errorFlag = true;
		}
	}
	
	private UnsafeUtils() {}
	
	public static Unsafe getUnsafe() {
		return UNSAFE;
	}
	
	public static boolean hasUnsafe() {
		return !errorFlag;
	}
	
	public static void throwExceptionUnchecked(Throwable ex) {
		UNSAFE.throwException(ex);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getInstanceWithoutConstructor(Class<T> clazz) throws InstantiationException {
		return (T) UNSAFE.allocateInstance(clazz);
	}
	
	public static <T> OffHeapArray<T> createOffHeapArray(long size, FixedSizeObject<T> type) {
		long allocSize = size * type.getSize();
		long pointer = UNSAFE.allocateMemory(allocSize); //Allocates size bytes
		return new OffHeapArray<T>(pointer, allocSize, size, type);
	}
	
	public static AllocatedMemory allocateMemory(long byteSize) {
		long address = UNSAFE.allocateMemory(byteSize);
		return new AllocatedMemory(address, byteSize);
	}
	
	public static void freeMemory(AllocatedMemory memory) {
		memory.freeMemory();
	}
	
	public static void parkCurrentThread(boolean isAbsoulute, long time) {
		UNSAFE.park(isAbsoulute, time);
	}
	
	public static void unparkThread(Thread thread) {
		UNSAFE.unpark(thread);
	}
	
	public static Class<?> defineClassFromBytes(String name, byte[] data) {
		return UNSAFE.defineClass(name, data, 0, data.length, ClassLoader.getSystemClassLoader(), pd);
	}
	
}
