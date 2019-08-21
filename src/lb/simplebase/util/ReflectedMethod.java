package lb.simplebase.util;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import lb.simplebase.core.RequireUndocumented;
import lb.simplebase.reflect.UnsafeUtils;

@Deprecated
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
			try {
				return (ReflectedMethodNE) StaticInitializerHack.delegateSite.getTarget().invokeExact(this);
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}
	}
	
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
	
	class StaticInitializerHack {
		private static final CallSite delegateSite;
		static {
			CallSite site = null;
			try {
				final MethodType interfaceType = MethodType.methodType(ReflectedMethodNE.class);
				final MethodType methodType = MethodType.methodType(Object.class, Object[].class);
				final MethodHandle methodHandle = MethodHandles.lookup().findVirtual(ReflectedMethodNE.class, "getOrExecute", methodType);
				site = LambdaMetafactory.metafactory(MethodHandles.lookup(), "getOrExecute",
						interfaceType, methodType, methodHandle, methodType);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			delegateSite = site;
		}
	}
	
	
}
