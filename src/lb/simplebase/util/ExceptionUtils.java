package lb.simplebase.util;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import lb.simplebase.core.RequireUndocumented;
import lb.simplebase.reflect.UnsafeUtils;

public final class ExceptionUtils {

	private ExceptionUtils() {}
	
	public static <T> T wrapException(Callable<T> task, T value) {
		try {
			return task.call();
		} catch(Exception e) {
			return value;
		}
	}
	
	public static <T> T wrapException(Callable<T> task, Supplier<T> value) {
		try {
			return task.call();
		} catch(Exception e) {
			return value.get();
		}
	}
	
	public static boolean wrapException(Callable<?> task) {
		try {
			task.call();
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public static <T> T wrapUnchekedException(Callable<T> task) {
		try {
			return task.call();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	} 
	
	@RequireUndocumented("sun.misc.Unsafe")
	public static <T> T wrapUnsafeException(Callable<T> task) {
		try {
			return task.call();
		} catch(Exception e) {
			UnsafeUtils.throwExceptionUnchecked(e);
			return null;
		}
	}
}
