package lb.simplebase.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
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
	
	@Override
	public abstract FieldAccess<T> bindToInstance(final Object instance);

	public T getBound() {
		if(!isBound()) throw new UnsupportedOperationException("getBound can only be used if an instance is bound to this FieldAccess");
		return getInstance(null);
	}

	public abstract void setInstance(final Object instance, final T value);
	public abstract T getInstance(final Object instance);
	
	@Override
	public abstract FieldAccess<T> clone();
 	
	public <R> FieldAccess<R> applyFunction(final Function<T, R> getFunction, final Function<R, T> setFunction) {
		return new DelegateFieldAccess<>(this.clone(), getFunction, setFunction);
	}
	
	public MethodAccess<T> createGetter() {
		return new MethodAccess.GetterMethodAccess<>(this.clone());
	}
	
	public MethodAccess<Void> createSetter() {
		return new MethodAccess.SetterMethodAccess<>(this.clone());
	}
	
	protected static class GetAndSetFieldAccess<T> extends FieldAccess<T> {

		protected final MethodAccess<T> getterFunction;
		protected final MethodAccess<Void> setterFunction;
		
		protected GetAndSetFieldAccess(final MethodAccess<T> getter, final MethodAccess<Void> setter) { 
			super(getter.isStatic, getter.instance);
			this.getterFunction = getter;
			this.setterFunction = setter;
		}

		@Override
		public void setInstance(final Object instance, final T value) {
			setterFunction.callInstance(instance, value);
			setException(setterFunction.getLastException());
		}

		@Override
		public T getInstance(final Object instance) {
			final T t = getterFunction.callInstance(instance);
			setException(getterFunction.getLastException());
			return t;
		}

		@Override
		public FieldAccess<T> bindToInstance(final Object instanceParam) {
			return new GetAndSetFieldAccess<>(getterFunction.bindToInstance(instance), setterFunction.bindToInstance(instanceParam));
		}

		@Override
		public MethodAccess<T> createGetter() {
			return getterFunction.clone();
		}

		@Override
		public MethodAccess<Void> createSetter() {
			return setterFunction.clone();
		}

		@Override
		public FieldAccess<T> clone() {
			return new GetAndSetFieldAccess<>(getterFunction.clone(), setterFunction.clone());
		}
		
	}
	
	protected static class DelegateFieldAccess<T,R> extends FieldAccess<R> {

		protected final FieldAccess<T> delegate;
		protected final Function<T, R> getTransform;
		protected final Function<R, T> setTransform;
		
		protected DelegateFieldAccess(final FieldAccess<T> delegate, final Function<T, R> getTransform, final Function<R, T> setTransform) {
			super(delegate.isStatic, delegate.instance);
			this.delegate = delegate;
			this.getTransform = getTransform;
			this.setTransform = setTransform;
		}

		@Override
		public <V> FieldAccess<V> applyFunction(final Function<R, V> getFunctionParam, final Function<V, R> setFunctionParam) {
			return delegate.applyFunction(getFunctionParam.compose(getTransform), setTransform.compose(setFunctionParam));
		}

		@Override
		public FieldAccess<R> bindToInstance(final Object instance) {
			return new DelegateFieldAccess<>(delegate.bindToInstance(instance), getTransform, setTransform);
		}

		@Override
		public void setInstance(final Object instance, final R value) {
			delegate.setInstance(instance, setTransform.apply(value));
			setException(delegate.getLastException());
		}

		@Override
		public R getInstance(final Object instance) {
			final R r = getTransform.apply(delegate.getInstance(instance));
			setException(delegate.getLastException());
			return r;
		}

		@Override
		public FieldAccess<R> clone() {
			return new DelegateFieldAccess<>(delegate.clone(), getTransform, setTransform);
		}
		
	}
	
	protected static class ReflectionFieldAccess<T> extends FieldAccess<T> {

		protected final Field field;
		
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
				if(BaseReflectionUtils.enabled) BaseReflectionUtils.REF_LOG.error("Could not set value of field (through access): " + field, e);
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
				if(BaseReflectionUtils.enabled) BaseReflectionUtils.REF_LOG.error("Could not get value of field (through access): " + field, e);
				setException(e);
				return null;
			}
		}

		@Override
		public FieldAccess<T> bindToInstance(final Object instance) {
			return new ReflectionFieldAccess<>(field, instance);
		}

		@Override
		public FieldAccess<T> clone() {
			return new ReflectionFieldAccess<>(field, instance);
		}
	}
	
	public static <T> FieldAccess<T> fromMethods(final MethodAccess<T> getter, final MethodAccess<Void> setter) {
		Objects.requireNonNull(getter, "Getter method must not be null");
		Objects.requireNonNull(setter, "Setter method must not be null");
		
		if(getter.isStatic() != setter.isStatic()) throw new IllegalArgumentException("Getter and setter must have the same value for the isStatic() status");
		if(getter.isBound() != setter.isBound()) throw new IllegalArgumentException("Getter and setter must have the same value for the isBound() status");
		if(getter.instance != setter.instance) throw new IllegalArgumentException("If getter and setter are bound to an instance, the instances must be identical");
		
		return new GetAndSetFieldAccess<>(getter.clone(), setter.clone());
	}
	
}
