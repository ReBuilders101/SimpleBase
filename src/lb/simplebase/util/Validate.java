package lb.simplebase.util;

import java.util.Objects;
import java.util.function.Predicate;

import lb.simplebase.reflect.QuickReflectionUtils;

/**
 * Methods used to validate method parameters. If the parameter is valid, the method will return. If the parameter is invalid,
 * A {@link IllegalArgumentException} will be thrown. Optionally, methods can throw {@link NullPointerException}s if parameters are null
 */
public final class Validate {

	private Validate() {}
	
	public static <T> void noNullElements(final T[] array, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(array, "Array that should be checked for null elements must not be null");
		for(T t : array) {
			if(t == null) throw new IllegalArgumentException(message);
		}
	}
	
	public static <T> void noMatchingElements(final T[] array, final Predicate<T> tester, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(array, "Array that should be checked for matching elements must not be null");
		Objects.requireNonNull(array, "Predicate for matching elements must not be null");
		for(T t : array) {
			if(tester.test(t)) throw new IllegalArgumentException(message);
		}
	}
	
	public static <T> void requireTypeOrNull(Object object, Class<?> type, String message) throws IllegalArgumentException {
		Objects.requireNonNull(type, "Type to check must not be null");
		if(!QuickReflectionUtils.isOfType(object, type)) throw new IllegalArgumentException(message);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T requireType(Object object, Class<T> type, String message) throws IllegalArgumentException {
		Objects.requireNonNull(type, "Type to check must not be null");
		Objects.requireNonNull(object, "Object to check must not be null");
		if(!QuickReflectionUtils.isOfType(object, type)) throw new IllegalArgumentException(message);
		return (T) object;
	}
	
	public static void requireTrue(boolean value, String message) throws IllegalArgumentException {
		if(!value) throw new IllegalArgumentException(message);
	}
	
	public static void requireFalse(boolean value, String message) throws IllegalArgumentException {
		if(value) throw new IllegalArgumentException(message);
	}
}
