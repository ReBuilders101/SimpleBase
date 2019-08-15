package lb.simplebase.util;

import lb.simplebase.core.RequireUndocumented;
import lb.simplebase.reflect.UnsafeUtils;

@FunctionalInterface
public interface ReflectedMethod {

	public Object getOrExecute(Object...params) throws Throwable;
	
	public default ReflectedMethodNE wrap(final Object exceptionReturnValue) {
		return (o) -> {
			try {
				return getOrExecute(o);
			} catch (Throwable e) {
				return exceptionReturnValue;
			}
		};
	}
	
	public default ReflectedMethodNE wrapNull() {
		return wrap(null);
	}
	
	public default ReflectedMethodNE wrapRuntime() {
		return (o) -> {
			try {
				return getOrExecute(o);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
	
	@RequireUndocumented("sun.misc.Unsafe")
	public default ReflectedMethodNE wrapUnsafe() {
		return (o) -> {
			try {
				return getOrExecute(o);
			} catch (Throwable e) {
				UnsafeUtils.throwExceptionUnchecked(e);
				return null;
			}
		};
	}
	
}
