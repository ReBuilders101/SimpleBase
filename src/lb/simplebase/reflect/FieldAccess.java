package lb.simplebase.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.Function;

public abstract class FieldAccess<T> extends MemberAccess {
	
	protected FieldAccess(final boolean isStatic, final Object instance) {
		super(isStatic, instance);
	}
	
	public void setStatic(final T value) {
		if(!isStatic()) throw new UnsupportedOperationException("setStatic can only be used for static fields");
		setInstance(null, value);
	}
	
	public T getStatic() {
		if(!isStatic()) throw new UnsupportedOperationException("getStatic can only be used for static fields");
		return getInstance(null);
	}
	
	public void setBound(final T value) {
		if(!isBound()) throw new UnsupportedOperationException("setBound can only be used if an instance is bound to this FieldAccess");
		setInstance(null, value);
	}
	
	public T getBound() {
		if(!isBound()) throw new UnsupportedOperationException("getBound can only be used if an instance is bound to this FieldAccess");
		return getInstance(null);
	}

	public abstract void setInstance(final Object instance, final T value);
	public abstract T getInstance(final Object instance);
	
	public <R> FieldAccess<R> applyFunction(final Function<T, R> getFunction, final Function<R, T> setFunction) {
		return new DelegateFieldAccess<>(this, getFunction, setFunction);
	}
	
	protected static class GetAndSetFieldAccess<T> extends FieldAccess<T> {

		protected GetAndSetFieldAccess(MethodAccess<T> getter, MethodAccess<?> setter) { 
			super(false, null);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void setInstance(Object instance, T value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public T getInstance(Object instance) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	protected static class DelegateFieldAccess<T,R> extends FieldAccess<R> {

		private final FieldAccess<T> delegate;
		private final Function<T, R> getTransform;
		private final Function<R, T> setTransform;
		
		protected DelegateFieldAccess(final FieldAccess<T> delegate, final Function<T, R> getTransform, final Function<R, T> setTransform) {
			super(false, null);
			this.delegate = delegate;
			this.getTransform = getTransform;
			this.setTransform = setTransform;
		}

		@Override
		public void setStatic(final R value) {
			delegate.setStatic(setTransform.apply(value));
		}

		@Override
		public R getStatic() {
			return getTransform.apply(delegate.getStatic());
		}

		@Override
		public void setBound(final R value) {
			delegate.setBound(setTransform.apply(value));
		}

		@Override
		public R getBound() {
			return getTransform.apply(delegate.getBound());
		}

		@Override
		public void setInstance(final Object instance, final R value) {
			delegate.setInstance(instance, setTransform.apply(value));
		}

		@Override
		public R getInstance(final Object instance) {
			return getTransform.apply(delegate.getInstance(instance));
		}

		@Override
		public void bindToInstance(final Object instance) {
			delegate.bindToInstance(instance);
		}

		@Override
		public <V> FieldAccess<V> applyFunction(final Function<R, V> getFunctionParam, final Function<V, R> setFunctionParam) {
			return delegate.applyFunction(getFunctionParam.compose(getTransform), setTransform.compose(setFunctionParam));
		}

		@Override
		public boolean isStatic() {
			return delegate.isStatic();
		}

		@Override
		public boolean isBound() {
			return delegate.isBound();
		}

		@Override
		public Exception getLastException() {
			return delegate.getLastException();
		}

		@Override
		public Optional<Exception> getLastExceptionOptional() {
			return delegate.getLastExceptionOptional();
		}

		@Override
		public boolean hasLastException() {
			return delegate.hasLastException();
		}

		@Override
		protected void setException(final Exception e) {
			delegate.setException(e);
		}

		@Override
		public void rethrowException() throws Exception {
			delegate.rethrowException();
		}

		
	}
	
	protected static class ReflectionFieldAccess<T> extends FieldAccess<T> {

		private final Field field;
		
		protected ReflectionFieldAccess(final Field field, final Object instance) {
			super(Modifier.isStatic(field.getModifiers()), instance);
			this.field = field;
		}

		@Override
		public void setInstance(final Object instance, final T value) {
			setException(null);
			try {
				field.set(instance, value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				setException(e);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public T getInstance(final Object instance) {
			setException(null);
			try {
				return (T) field.get(instance);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				setException(e);
				return null;
			}
		}
	}
	
}
