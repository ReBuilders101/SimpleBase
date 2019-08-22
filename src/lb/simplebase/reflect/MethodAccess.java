package lb.simplebase.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class MethodAccess<T> extends MemberAccess {

	protected MethodAccess(final boolean isStatic, final Object instance) {
		super(isStatic, instance);
	}

	public T callStatic(final Object...params) {
		if(!isStatic()) throw new UnsupportedOperationException("callStatic can only be used for static methods");
		return callInstance(null, params);
	}
	
	public T callBound(final Object...params) {
		if(!isBound()) throw new UnsupportedOperationException("callBound can only be used if an instance is bound to this MethodAccess");
		return callInstance(instance, params);
	}
	
	public abstract T callInstance(final Object instance, final Object...params);
	
	public static class ReflectionMethodAccess<T> extends MethodAccess<T>{

		private final Method method;
		
		protected ReflectionMethodAccess(final Method method, final Object instance) {
			super(Modifier.isStatic(method.getModifiers()), instance);
			this.method = method;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T callInstance(Object instance, Object... params) {
			setException(null);
			try {
				return (T) method.invoke(instance, params);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				setException(e);
				return null;
			}
		}
		
	}
	
}
