package lb.simplebase.util;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

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
	
	public default ReflectedMethodNE wrapLambda() {
		final MethodType interfaceType = MethodType.methodType(ReflectedMethodNE.class);
		final MethodType methodType = MethodType.methodType(Object.class, Object[].class);
		
		try {
			final CallSite factory = LambdaMetafactory.metafactory(MethodHandles.lookup(), "getOrExecute",
					interfaceType, methodType, MethodHandles.lookup().bind(this, "getOrExecute", methodType), methodType);
			return (ReflectedMethodNE) factory.getTarget().invokeExact();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
