package lb.simplebase.reflect;

public class Signature<T> {

	private T value;
	private Class<?> clazz;
	
	public Signature(T value) {
		this.value = value;
		this.clazz = value.getClass();
	}
	
	public Signature(T value, Class<? super T> forcedType) {
		this.value = value;
		this.clazz = forcedType;
	}
	
	public T getValue() {
		return value;
	}
	
	public Class<?> getType() {
		return clazz;
	}
	
	public static Object[] createValueArray(Signature<?>...signatures) {
		final int length = signatures.length;
		Object[] ret = new Object[length];
		for(int i = 0; i < length; i++) {
			ret[i] = signatures[i].getValue();
		}
		return ret;
	}
	
	public static Class<?>[] createTypeArray(Signature<?>...signatures) {
		final int length = signatures.length;
		Class<?>[] ret = new Class<?>[length];
		for(int i = 0; i < length; i++) {
			ret[i] = signatures[i].getType();
		}
		return ret;
	}
	
	public static <T, U, V, W> Signature<?>[] of(Class<? super T> type1, T value1, Class<? super U> type2, U value2, Class<? super V> type3, V value3, Class<? super W> type4, W value4) {
		return new Signature<?>[] {new Signature<>(value1, type1), new Signature<>(value2, type2), new Signature<>(value3, type3), new Signature<>(value4, type4)};
	}
	
	public static <T, U, V> Signature<?>[] of(Class<? super T> type1, T value1, Class<? super U> type2, U value2, Class<? super V> type3, V value3) {
		return new Signature<?>[] {new Signature<>(value1, type1), new Signature<>(value2, type2), new Signature<>(value3, type3)};
	}
	
	public static <T, U> Signature<?>[] of(Class<? super T> type1, T value1, Class<? super U> type2, U value2) {
		return new Signature<?>[] {new Signature<>(value1, type1), new Signature<>(value2, type2)};
	}
	
	public static <T, U> Signature<?>[] of(Class<? super T> type1, T value1) {
		return new Signature<?>[] {new Signature<>(value1, type1)};
	}
	
	public static Signature<?>[] of(Object...values) {
		final int length = values.length;
		Signature<?>[] ret = new Signature<?>[length];
		for(int i = 0; i < length; i++) {
			ret[i] = new Signature<>(values[i]);
		}
		return ret;
	}
}
