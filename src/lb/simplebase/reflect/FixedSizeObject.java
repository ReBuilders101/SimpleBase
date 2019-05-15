package lb.simplebase.reflect;

import java.util.function.BiFunction;

import lb.simplebase.util.TriConsumer;
import sun.misc.Unsafe;

/**
 * A {@link FixedSizeObject} saves information about a data type with a fixed byte size.
 * Instances are available for all primitive types as static fields.
 * @param <T> The type that is represented by this {@link FixedSizeObject} instance
 */
public final class FixedSizeObject<T> {
	
	/**
	 * The {@link FixedSizeObject} that represents the {@link Byte} type.
	 */
	public static FixedSizeObject<Byte>      BYTE    = new FixedSizeObject<>(Byte.BYTES,      Byte.class,      byte.class,   Unsafe::getByte,   Unsafe::putByte);
	/**
	 * The {@link FixedSizeObject} that represents the {@link Character} type.
	 */
	public static FixedSizeObject<Character> CHAR    = new FixedSizeObject<>(Character.BYTES, Character.class, char.class,   Unsafe::getChar,   Unsafe::putChar);
	/**
	 * The {@link FixedSizeObject} that represents the {@link Short} type.
	 */
	public static FixedSizeObject<Short>     SHORT   = new FixedSizeObject<>(Short.BYTES,     Short.class,     short.class,  Unsafe::getShort,  Unsafe::putShort);
	/**
	 * The {@link FixedSizeObject} that represents the {@link Integer} type.
	 */
	public static FixedSizeObject<Integer>   INTEGER = new FixedSizeObject<>(Integer.BYTES,   Integer.class,   int.class,    Unsafe::getInt,    Unsafe::putInt);
	/**
	 * The {@link FixedSizeObject} that represents the {@link Long} type.
	 */
	public static FixedSizeObject<Long>      LONG    = new FixedSizeObject<>(Long.BYTES,      Long.class,      long.class,   Unsafe::getLong,   Unsafe::putLong);
	/**
	 * The {@link FixedSizeObject} that represents the {@link Float} type.
	 */
	public static FixedSizeObject<Float>     FLOAT   = new FixedSizeObject<>(Float.BYTES,     Float.class,     float.class,  Unsafe::getFloat,  Unsafe::putFloat);
	/**
	 * The {@link FixedSizeObject} that represents the {@link Double} type.
	 */
	public static FixedSizeObject<Double>    DOUBLE  = new FixedSizeObject<>(Double.BYTES,    Double.class,    double.class, Unsafe::getDouble, Unsafe::putDouble);
	
	private Class<T> clazz;
	private Class<?> pClazz;
	private long size;
	private BiFunction<Unsafe, Long, T> read;
	private TriConsumer<Unsafe, Long, T> write;
	
	private FixedSizeObject(long size, Class<T> clazz, Class<?> pClazz, BiFunction<Unsafe, Long, T> read, TriConsumer<Unsafe, Long, T> write) {
		this.size = size;
		this.clazz = clazz;
		this.pClazz = pClazz;
		this.read = read;
		this.write = write;
	}
	
	/**
	 * Returns the number of bytes that are needed to save an object of this type.
	 * @return The number of bytes that are needed to save an object of this type
	 */
	public long getSize() {
		return size;
	}
	
	/**
	 * Returns the {@link Class} of the type that is represented by this instance.
	 * @return The {@link Class} of the type that is represented by this instance
	 */
	public Class<?> getTypeClass() {
		return clazz;
	}
	
	/**
	 * Returns the {@link Class} of the primitive type that is represented by this instance.
	 * @return The {@link Class} of the primitive type that is represented by this instance
	 */
	public Class<?> getPrimitiveClass() {
		return pClazz;
	}
	
	/**
	 * A {@link BiFunction} that can be used to read a value of this type from a memory address.
	 * @return A read function for this type
	 */
	public BiFunction<Unsafe, Long, T> getReadFunction() {
		return read;
	}
	
	/**
	 * A {@link TriConsumer} that can be used to write a value of this type from a memory address.
	 * @return A write function for this type
	 */
	public TriConsumer<Unsafe, Long, T> getWriteFunction() {
		return write;
	}
	
	/**
	 * All instances of {@link FixedSizeObject} that are available, as an array.
	 */
	public static final FixedSizeObject<?>[] VALUES = new FixedSizeObject<?>[] { BYTE, CHAR, SHORT, INTEGER, LONG, FLOAT, DOUBLE };
}
