package lb.simplebase.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;

public abstract class MethodAccess<T> extends MemberAccess {

	protected MethodAccess(final boolean isStatic, final Object instance) {
		super(isStatic, instance);
	}

	public T callStatic(final Object...params) {
		if(!isStatic()) throw new UnsupportedOperationException("callStatic can only be used for static methods");
		return callInstance(null, params);
	}
	
	public T callStatic() {
		return callStatic((Object[]) null);
	}
	
	public T callBound() {
		return callBound((Object[]) null);
	}
	
	public T callInstance(final Object instance) {
		return callInstance(instance, (Object[]) null);
	}
	
	public T callBound(final Object...params) {
		if(!isBound()) throw new UnsupportedOperationException("callBound can only be used if an instance is bound to this MethodAccess");
		return callInstance(instance, params);
	}
	
	public <R> MethodAccess<R> applyFunction(final Function<T, R> resultTransform) {
		return new DelegateMethodAccess<>(this.clone(), resultTransform);
	}
	
	@Override
	public abstract MethodAccess<T> bindToInstance(final Object instance);
	
	public abstract T callInstance(final Object instance, final Object...params);
	
	@Override
	public abstract MethodAccess<T> clone();
	
	protected static class ReflectionMethodAccess<T> extends MethodAccess<T>{

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

		@Override
		public MethodAccess<T> bindToInstance(Object instanceParam) {
			return new ReflectionMethodAccess<>(method, instanceParam);
		}

		@Override
		public MethodAccess<T> clone() {
			return new ReflectionMethodAccess<>(method, instance);
		}
		
	}
	
	protected static class GetterMethodAccess<T> extends MethodAccess<T> {

		private final FieldAccess<T> fieldAccess;
		
		protected GetterMethodAccess(final FieldAccess<T> fieldAccess) {
			super(fieldAccess.isStatic, fieldAccess.instance);
			this.fieldAccess = fieldAccess;
		}

		@Override
		public MethodAccess<T> bindToInstance(final Object instanceParam) {
			return new GetterMethodAccess<>(fieldAccess.bindToInstance(instanceParam));
		}

		@Override
		public T callInstance(final Object instance, final Object... params) {
			final T t = fieldAccess.getInstance(instance);
			setException(fieldAccess.getLastException());
			return t;
		}

		@Override
		public MethodAccess<T> clone() {
			return new GetterMethodAccess<>(fieldAccess.clone());
		}
		
	}
	
	protected static class SetterMethodAccess<V> extends MethodAccess<Void> {

		private final FieldAccess<V> fieldAccess;
		
		protected SetterMethodAccess(final FieldAccess<V> fieldAccess) {
			super(fieldAccess.isStatic, fieldAccess.instance);
			this.fieldAccess = fieldAccess;
		}

		@Override
		public MethodAccess<Void> bindToInstance(final Object instanceParam) {
			return new SetterMethodAccess<>(fieldAccess.bindToInstance(instanceParam));
		}

		@SuppressWarnings("unchecked")
		@Override
		public Void callInstance(Object instance, Object... params) {
			if(params == null || params.length == 0) throw new IllegalArgumentException("Parameters for setter must have at least one element");
			fieldAccess.setInstance(instance, (V) params[0]);
			setException(fieldAccess.getLastException());
			return null;
		}

		@Override
		public MethodAccess<Void> clone() {
			return new SetterMethodAccess<>(fieldAccess.clone());
		}
		
	}

	protected static class DelegateMethodAccess<T, R> extends MethodAccess<R> {

		private final MethodAccess<T> delegate;
		private final Function<T, R> resultTransform;
		
		protected DelegateMethodAccess(final MethodAccess<T> delegate, final Function<T, R> resultTransform) {
			super(delegate.isStatic, delegate.instance);
			this.delegate = delegate;
			this.resultTransform = resultTransform;
		}

		@Override
		public <V> MethodAccess<V> applyFunction(Function<R, V> resultTransformParam) {
			return delegate.applyFunction(resultTransformParam.compose(resultTransform));
		}

		@Override
		public MethodAccess<R> bindToInstance(final Object instance) {
			return new DelegateMethodAccess<>(delegate.bindToInstance(instance), resultTransform);
		}

		@Override
		public R callInstance(final Object instance, final Object... params) {
			final T t = delegate.callInstance(instance, params);
			setException(delegate.getLastException());
			return resultTransform.apply(t);
		}

		@Override
		public MethodAccess<R> clone() {
			return new DelegateMethodAccess<>(delegate.clone(), resultTransform);
		}
		
	}
	
}
