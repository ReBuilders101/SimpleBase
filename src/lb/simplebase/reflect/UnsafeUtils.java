package lb.simplebase.reflect;

import lb.simplebase.core.RequireUndocumented;
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
		UNSAFE = QuickReflectionUtils.Fields.getStaticField(Unsafe.class, "theUnsafe", Unsafe.class);
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
	 * method like a <code>throw</code> statement. However, this method will always throw the exception in the parameter.
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
}
