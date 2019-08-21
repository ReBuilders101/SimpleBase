package lb.simplebase.util;

import java.util.function.Predicate;

import lb.simplebase.reflect.QuickReflectionUtils;

public final class Validate {

	private Validate() {}
	
	public static <T, E extends Throwable> void noNullElements(T[] array, E exception) throws E {
		for(T t : array) {
			if(t == null) throw exception;
		}
	}
	
	public static <T, E extends Throwable> void noMatchingElements(T[] array, Predicate<T> tester, E exception) throws E {
		for(T t : array) {
			if(tester.test(t)) throw exception;
		}
	}
	
	public static <T, E extends Throwable> void requireType(Object object, Class<?> type, E exception) throws E {
		if(!QuickReflectionUtils.isOfType(object, type)) throw exception;
	}
	
	public static <T> void requireType(Object object, Class<?> type, String exception) throws IllegalArgumentException {
		if(!QuickReflectionUtils.isOfType(object, type)) throw new IllegalArgumentException(exception);
	}
	
}
