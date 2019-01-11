package lb.simplebase.reflect;

/**
 * A signature describes a parameter type and an associated value of that type.<br>
 * An actual method signature is described by an array if {@link Signature} objects.
 * <p>
 * If an array of signatures is required, most of the time one of the static helper
 * methods can be used instead of creating a new array.
 * @param <T> The type of this signature element
 */
public class Signature<T> {

	private T value;
	private Class<?> clazz;
	
	/**
	 * Creates a new {@link Signature}. The type of the signature will be <code>value.getClass()</code>.<br>
	 * Use {@link #Signature(Object, Class)} if you want / need to specify the type of the signature
	 * different from the type of the implementation of <code>value</code>.
	 * @param value The value associated with this parameter
	 */
	public Signature(T value) {
		this.value = value;
		this.clazz = value.getClass();
	}
	
	/**
	 * Creates a new {@link Signature}. The type of the signature is a explicitly specified supertype
	 * of the type of the value, in case The method signature / parameter type does not match the type of the value
	 * <p>
	 * Example:<br>
	 * <code>value</code> is of type {@link Integer}, the method accepts {@link Number} in its signature. The type
	 * of the signature must be explicitly specified, because otherwise it would be <code>Integer.class</code>
	 * and not <code>Number.class</code>, which could be a different method.<p>
	 * If it is not clear which implementation the <code>value</code> comes from, this constructor should be preferred.
	 * @param value The value associated with this parameter
	 * @param forcedType A supertype of the type of the value, that is in the method's signature
	 */
	public Signature(T value, Class<? super T> forcedType) {
		this.value = value;
		this.clazz = forcedType;
	}
	
	/**
	 * Returns the value of this signature element.
	 * <p>
	 * An array of values can be obtained from an array of {@link Signature}s with the
	 * {@link #createValueArray(Signature...)} utility method.
	 * @return The value of this signature element
	 */
	public T getValue() {
		return value;
	}
	
	/**
	 * Returns the type of this signature element.
	 * <p>
	 * An array of types can be obtained from an array of {@link Signature}s with the
	 * {@link #createTypeArray(Signature...)} utility method.
	 * @return The type of this signature element
	 */
	public Class<?> getType() {
		return clazz;
	}
	
	/**
	 * Extracts all values from an array of signatures, and returns them as an array.<br>
	 * {@link Signature}s of different types can be mixed.
	 * @param signatures The signatures that contain the values
	 * @return The values in the same order
	 */
	public static Object[] createValueArray(Signature<?>...signatures) {
		final int length = signatures.length;
		Object[] ret = new Object[length];
		for(int i = 0; i < length; i++) {
			ret[i] = signatures[i].getValue();
		}
		return ret;
	}
	
	/**
	 * Extracts all types from an array of signatures, and returns them as an array.<br>
	 * {@link Signature}s of different types can be mixed.
	 * @param signatures The signatures that contain the types
	 * @return The types in the same order
	 */
	public static Class<?>[] createTypeArray(Signature<?>...signatures) {
		final int length = signatures.length;
		Class<?>[] ret = new Class<?>[length];
		for(int i = 0; i < length; i++) {
			ret[i] = signatures[i].getType();
		}
		return ret;
	}
	
	/**
	 * Creates a new {@link Signature} array with four parameters of possibly different types.
	 * @param <T> The type of the first parameter
	 * @param <U> The type of the second parameter
	 * @param <V> The type of the third parameter
	 * @param <W> The type of the fourth parameter
	 * @param type1 The type of the first parameter
	 * @param value1 The value of the first parameter
	 * @param type2 The type of the second parameter
	 * @param value2 The value of the second parameter
	 * @param type3 The type of the third parameter
	 * @param value3 The value of the third parameter
	 * @param type4 The type of the fourth parameter
	 * @param value4 The value of the fourth parameter
	 * @return The method signature
	 * @see #of(Class, Object)
	 * @see #of(Class, Object, Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object)
	 */
	public static <T, U, V, W> Signature<?>[] of(Class<? super T> type1, T value1, Class<? super U> type2, U value2, Class<? super V> type3, V value3, Class<? super W> type4, W value4) {
		return new Signature<?>[] {new Signature<>(value1, type1), new Signature<>(value2, type2), new Signature<>(value3, type3), new Signature<>(value4, type4)};
	}
	
	/**
	 * Creates a new {@link Signature} array with three parameters of possibly different types.
	 * @param <T> The type of the first parameter
	 * @param <U> The type of the second parameter
	 * @param <V> The type of the third parameter
	 * @param type1 The type of the first parameter
	 * @param value1 The value of the first parameter
	 * @param type2 The type of the second parameter
	 * @param value2 The value of the second parameter
	 * @param type3 The type of the third parameter
	 * @param value3 The value of the third parameter
	 * @return The method signature
	 * @see #of(Class, Object)
	 * @see #of(Class, Object, Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object, Class, Object)
	 */
	public static <T, U, V> Signature<?>[] of(Class<? super T> type1, T value1, Class<? super U> type2, U value2, Class<? super V> type3, V value3) {
		return new Signature<?>[] {new Signature<>(value1, type1), new Signature<>(value2, type2), new Signature<>(value3, type3)};
	}
	
	/**
	 * Creates a new {@link Signature} array with two parameters of possibly different types.
	 * @param <T> The type of the first parameter
	 * @param <U> The type of the second parameter
	 * @param type1 The type of the first parameter
	 * @param value1 The value of the first parameter
	 * @param type2 The type of the second parameter
	 * @param value2 The value of the second parameter
	 * @return The method signature
	 * @see #of(Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object, Class, Object)
	 */
	public static <T, U> Signature<?>[] of(Class<? super T> type1, T value1, Class<? super U> type2, U value2) {
		return new Signature<?>[] {new Signature<>(value1, type1), new Signature<>(value2, type2)};
	}
	
	/**
	 * Creates a new {@link Signature} array that contains any amount of parameters, which are all of one type.
	 * @param <T> The type of all values
	 * @param type The type of all values
	 * @param values Any amount of values
	 * @return The method signature
	 */
	@SafeVarargs
	public static <T> Signature<?>[] allOf(Class<? super T> type, T...values) {
		final int length = values.length;
		Signature<?>[] ret = new Signature<?>[length];
		for(int i = 0; i < length; i++) {
			ret[i] = new Signature<T>(values[i], type);
		}
		return ret;
	}
	
	/**
	 * Creates a new {@link Signature} array with one parameter of a specified type.
	 * @param <T> The type of the first parameter
	 * @param type1 The type of the first parameter
	 * @param value1 The value of the first parameter
	 * @return The method signature
	 * @see #of(Class, Object, Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object, Class, Object)
	 */
	public static <T> Signature<?>[] of(Class<? super T> type1, T value1) {
		return new Signature<?>[] {new Signature<>(value1, type1)};
	}
	
	/**
	 * Creates a new {@link Signature} array that contains any amount of parameters.
	 * The type of each parameter will be the type of the value for that parameter.
	 * @param values The values of different types
	 * @return The method signature
	 */
	public static Signature<?>[] of(Object...values) {
		final int length = values.length;
		Signature<?>[] ret = new Signature<?>[length];
		for(int i = 0; i < length; i++) {
			ret[i] = new Signature<>(values[i]);
		}
		return ret;
	}
	
	private static final Signature<?>[] EMPTY = new Signature<?>[0];
	
	/**
	 * Returns an empty {@link Signature} array, for methods that take no parameters.
	 * The returned array is always the same one to save memory. 
	 * @return The method signature
	 */
	public static Signature<?>[] empty() {
		return EMPTY;
	}
}
