package lb.simplebase.util;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import lb.simplebase.reflect.QuickReflectionUtils;

/**
 * Methods used to validate method parameters. If the parameter is valid, the method will return. If the parameter is invalid,
 * A {@link IllegalArgumentException} will be thrown. Optionally, methods can throw {@link NullPointerException}s if parameters are null
 */
public final class Validate {

	private Validate() {}
	
	public static <T> T[] noNullElements(final T[] array, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(array, "Array that should be checked for null elements must not be null");
		for(T t : array) {
			if(t == null) throw new IllegalArgumentException(message);
		}
		return array;
	}
	
	public static <T> T[] noMatchingElements(final T[] array, final Predicate<T> tester, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(array, "Array that should be checked for matching elements must not be null");
		Objects.requireNonNull(array, "Predicate for matching elements must not be null");
		for(T t : array) {
			if(tester.test(t)) throw new IllegalArgumentException(message);
		}
		return array;
	}
	
	public static <T> T[] onlyMatchingElements(final T[] array, final Predicate<T> tester, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(array, "Array that should be checked for matching elements must not be null");
		Objects.requireNonNull(array, "Predicate for matching elements must not be null");
		for(T t : array) {
			if(!tester.test(t)) throw new IllegalArgumentException(message);
		}
		return array;
	}
	
	public static <T> Iterable<T> noNullElements(final Iterable<T> iterable, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(iterable, "Iterable that should be checked for null elements must not be null");
		for(T t : iterable) {
			if(t == null) throw new IllegalArgumentException(message);
		}
		return iterable;
	}
	
	public static <T> Iterable<T> noMatchingElements(final Iterable<T> iterable, final Predicate<T> tester, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(iterable, "Iterable that should be checked for matching elements must not be null");
		Objects.requireNonNull(iterable, "Predicate for matching elements must not be null");
		for(T t : iterable) {
			if(tester.test(t)) throw new IllegalArgumentException(message);
		}
		return iterable;
	}
	
	public static <T> Iterable<T> onlyMatchingElements(final Iterable<T> iterable, final Predicate<T> tester, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(iterable, "Iterable that should be checked for matching elements must not be null");
		Objects.requireNonNull(iterable, "Predicate for matching elements must not be null");
		for(T t : iterable) {
			if(!tester.test(t)) throw new IllegalArgumentException(message);
		}
		return iterable;
	}
	
	public static <T> void requireTypeOrNull(final Object object, final Class<?> type, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(type, "Type to check must not be null");
		if(!QuickReflectionUtils.isOfType(object, type)) throw new IllegalArgumentException(message);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T requireType(final Object object, final Class<T> type, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(type, "Type to check must not be null");
		Objects.requireNonNull(object, "Object to check must not be null");
		if(!type.isInstance(object));
		return (T) object;
	}
	
	public static <T> T[] requireSize(final T[] array, final int exactSize, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(array, "Array to check must not be null");
		if(array.length != exactSize) throw new IllegalArgumentException(message);
		return array;
	}
	
	public static <T> T[] requireMinSize(final T[] array, final int minSize, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(array, "Array to check must not be null");
		if(array.length < minSize) throw new IllegalArgumentException(message);
		return array;
	}
	
	public static <T> T[] requireMaxSize(final T[] array, final int maxSize, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(array, "Array to check must not be null");
		if(array.length > maxSize) throw new IllegalArgumentException(message);
		return array;
	}
	
	public static <T> Collection<T> requireSize(final Collection<T> collection, final int exactSize, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(collection, "Collection to check must not be null");
		if(collection.size() != exactSize) throw new IllegalArgumentException(message);
		return collection;
	}
	
	public static <T> Collection<T> requireMinSize(final Collection<T> collection, final int minSize, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(collection, "Collection to check must not be null");
		if(collection.size() < minSize) throw new IllegalArgumentException(message);
		return collection;
	}
	
	public static <T> Collection<T> requireMaxSize(final Collection<T> collection, final int maxSize, final String message) throws IllegalArgumentException {
		Objects.requireNonNull(collection, "Collection to check must not be null");
		if(collection.size() > maxSize) throw new IllegalArgumentException(message);
		return collection;
	}
	
	public static int requireMin(final int value, final int minValue, final String message) {
		if(value < minValue) throw new IllegalArgumentException(message);
		return value;
	}
	
	public static int requireMax(final int value, final int maxValue, final String message) {
		if(value > maxValue) throw new IllegalArgumentException(message);
		return value;
	}
	
	public static int requireRange(final int value, final int minValue, final int maxValue, final String message) {
		if(value <  minValue || value > maxValue) throw new IllegalArgumentException(message);
		return value;
	}
	
	public static void requireTrue(final boolean value, final String message) throws IllegalArgumentException {
		if(!value) throw new IllegalArgumentException(message);
	}
	
	public static void requireFalse(final boolean value, final String message) throws IllegalArgumentException {
		if(value) throw new IllegalArgumentException(message);
	}
}
