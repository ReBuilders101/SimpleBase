package lb.simplebase.reflect;

import java.util.function.BiFunction;

import lb.simplebase.function.TriConsumer;
import sun.misc.Unsafe;

public final class FixedSizeObject<T> {
	
	public static FixedSizeObject<Byte>      BYTE    = new FixedSizeObject<>(Byte.BYTES,      Byte.class,      byte.class, Unsafe::getByte,     Unsafe::putByte);
	public static FixedSizeObject<Character> CHAR    = new FixedSizeObject<>(Character.BYTES, Character.class, char.class, Unsafe::getChar,     Unsafe::putChar);
	public static FixedSizeObject<Short>     SHORT   = new FixedSizeObject<>(Short.BYTES,     Short.class,     short.class, Unsafe::getShort,   Unsafe::putShort);
	public static FixedSizeObject<Integer>   INTEGER = new FixedSizeObject<>(Integer.BYTES,   Integer.class,   int.class, Unsafe::getInt,       Unsafe::putInt);
	public static FixedSizeObject<Long>      LONG    = new FixedSizeObject<>(Long.BYTES,      Long.class,      long.class, Unsafe::getLong,     Unsafe::putLong);
	public static FixedSizeObject<Float>     FLOAT   = new FixedSizeObject<>(Float.BYTES,     Float.class,     float.class, Unsafe::getFloat,   Unsafe::putFloat);
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
	
	public long getSize() {
		return size;
	}
	
	public Class<?> getTypeClass() {
		return clazz;
	}
	
	public Class<?> getPrimitiveClass() {
		return pClazz;
	}
	
	public BiFunction<Unsafe, Long, T> getReadFunction() {
		return read;
	}
	
	public TriConsumer<Unsafe, Long, T> getWriteFunction() {
		return write;
	}
	
	public static final FixedSizeObject<?>[] VALUES = new FixedSizeObject<?>[] { BYTE, CHAR, SHORT, INTEGER, LONG, FLOAT, DOUBLE };
}
