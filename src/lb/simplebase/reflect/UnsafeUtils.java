package lb.simplebase.reflect;

import sun.misc.Unsafe;

/**
 * This class provides easy access to the inofficial {@link Unsafe} class/API that
 * can do 'unsafe' operations like allocating memory manually.
 * <p>
 * <b>Only use these methods if you know what they do and only if you absolutely need them.</b>
 */
@RequireUndocumented("sun.misc.Unsafe")
public final class UnsafeUtils {

	private static Unsafe UNSAFE;
	private static boolean errorFlag;
	
	static {
		UNSAFE = ReflectionUtils.getStaticField(Unsafe.class, "theUnsafe", Unsafe.class);
		errorFlag = UNSAFE == null;	
	}
	
	private UnsafeUtils() {}
	
	/**
	 * The {@link Unsafe} instance that is used to perform unsafe operations.<br>
	 * Use {@link #hasUnsafe()} to see whether the instance was initialized correctly
	 * <p>
	 * This {@link Unsafe} instance can be used to do unsafe operations, but in many cases this class already contains a
	 * static method to do the same, often with improvements like type safety.
	 * @return The unsafe instance
	 */
	public static Unsafe getUnsafe() {
		return UNSAFE;
	}
	
	/**
	 * If <code>false</code>, an error has occurred in the static initializer that is responsible for retrieving the
	 * {@link Unsafe} instance. In this case, {@link #getUnsafe()} will return <code>null</code> and almost all other
	 * methods will throw {@link NullPointerException}s.
	 * @return Whether the {@link Unsafe} instance was initialized
	 */
	public static boolean hasUnsafe() {
		return !errorFlag;
	}
	
	/**
	 * Re-throws a {@link Throwable} that normally would be a checked exception, without wrapping it in a {@link RuntimeException}.
	 * Methods using this may have to add a dummy return statement after this method call, because the compiler will not recoginze this
	 * methodlike a <code>throw</code> statement. However, this method will always throw the exception in the parameter.
	 * @param ex The {@link Throwable} to throw
	 */
	public static void throwExceptionUnchecked(Throwable ex) {
		UNSAFE.throwException(ex);
	}
	
	/**
	 * Creates a new instance of a type without calling a constructor. This means that all fields will be left uninitialized,
	 * objects will be <code>null</code> and primitives will be 0. Note that also no superclass constructor will be called.<br>
	 * If the instance cannot be created, for example because the type is an abstract class or an interface, <code>null</code>
	 * is returned instead.
	 * @param <T> The type of the created instance
	 * @param clazz The type of which a new instance should be allocated
	 * @return The new, uninitialized instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInstanceWithoutConstructor(Class<T> clazz) {
		try {
			return (T) UNSAFE.allocateInstance(clazz);
		} catch (InstantiationException e) {
			return null;
		}
	}
	
	/**
	 * Creates a new {@link OffHeapArray} that can store large amounts of data that is not
	 * checked by the garbage collector.<br>
	 * Only primitive types are possible as array element type.
	 * <p><b>
	 * The memory allocated for this array must be freed by calling {@link OffHeapArray#freeMemory()}
	 * or {@link #freeMemory(AllocatedMemory)}.</b>
	 * @param <T> The type of the array elements
	 * @param size The size of the array
	 * @param type The {@link FixedSizeObject} that describes the type of the array's elements.
	 * @return The created {@link OffHeapArray}
	 */
	public static <T> OffHeapArray<T> createOffHeapArray(long size, FixedSizeObject<T> type) {
		long allocSize = size * type.getSize();
		long pointer = UNSAFE.allocateMemory(allocSize); //Allocates size bytes
		return new OffHeapArray<T>(pointer, allocSize, size, type);
	}
	
	/**
	 * Creates a new {@link AllocatedMemory} instance that stores bytes which are not seen by
	 * the garbage collector. To store other primitive types than bytes, use {@link #createOffHeapArray(long, FixedSizeObject)}.
	 * <p><b>
	 * The allocated memory must be freed by calling {@link AllocatedMemory#freeMemory()}
	 * or {@link #freeMemory(AllocatedMemory)}.</b>
	 * @param byteSize The size of the memory region in bytes
	 * @return The allocated memory region
	 */
	public static AllocatedMemory allocateMemory(long byteSize) {
		long address = UNSAFE.allocateMemory(byteSize);
		return new AllocatedMemory(address, byteSize);
	}
	
	/**
	 * Frees the memory allocated in an {@link AllocatedMemory} or {@link OffHeapArray}.
	 * This method will call the {@link AllocatedMemory#freeMemory()} method on the parameter.
	 * It only exists here so both memory allocation and freeing of memory are in the same place.
	 * @param memory The {@link AllocatedMemory} that should be freed
	 */
	public static void freeMemory(AllocatedMemory memory) {
		memory.freeMemory();
	}
	
}
