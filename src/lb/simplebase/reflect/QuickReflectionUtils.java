package lb.simplebase.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

import lb.simplebase.core.RequireUndocumented;
import lb.simplebase.util.Validate;
import sun.reflect.ConstructorAccessor;

/**
 * Utility Methods to quickly get / set fields and invoke methods without throwing exceptions.<br>
 * Use inner classes for static methods. Does not cache field access: for repeated access, use {@link CachedReflectionUtils}.
 */
public final class QuickReflectionUtils {

	private QuickReflectionUtils() {}
	
	/**
	 * Checks whether {@code superclass} is a superclass of the {@code referenceClass}.
	 * @param superclass The class that should be the superclass of the reference class
	 * @param referenceClass The class used as a reference for the superclass
	 * @return whether the class is a superclass of the reference class
	 * @throws NullPointerException when {@code superclass} and/or {@code referenceClass} is null
	 * @see #isSubclassOf(Class, Class)
	 * @see Class#isAssignableFrom(Class)
	 */
	public static boolean isSuperclassOf(final Class<?> superclass, final Class<?> referenceClass) {
		Objects.requireNonNull(superclass, "The superclass must not be null");
		Objects.requireNonNull(referenceClass, "The reference class must not be null");
		return superclass.isAssignableFrom(referenceClass);
	}
	
	/**
	 * Checks whether {@code subclass} is a subclass of the {@code referenceClass}.
	 * @param subclass The class that should be the superclass of the reference class
	 * @param referenceClass The class used as a reference for the superclass
	 * @return whether the class is a subclass of the reference class
	 * @throws NullPointerException when {@code subclass} and/or {@code referenceClass} is null
	 * @see #isSuperclassOf(Class, Class)
	 * @see Class#isAssignableFrom(Class)
	 */
	public static boolean isSubclassOf(final Class<?> subclass, final Class<?> referenceClass) {
		Objects.requireNonNull(subclass, "The subclass must not be null");
		Objects.requireNonNull(referenceClass, "The reference class must not be null");
		return referenceClass.isAssignableFrom(subclass);
	}
	
	/**
	 * Checks whether an object's type is a subclass of the {@code testType}.
	 * If {@code true}, this means that the Object can be casted to the tested type safely.<br>
	 * If the object is {@code null}, this method will return {@code true}, as {@code null} can be used for any type.
	 * (Unlike {@link Class#isInstance(Object)})
	 * @param object The object that should be tested
	 * @param testType The type that the object should have
	 * @return Whether the object's type is a subclass of the {@code testType}
	 * @see Class#isInstance(Object)
	 */
	public static boolean isOfType(final Object object, final Class<?> testType) {
		if(testType == null) return false; //null is not a type
		if(object == null) return true; //Null matches any type
		return testType.isInstance(object);
//		return testType.isAssignableFrom(object.getClass());
	}
	
	/**
	 * Utility Methods that get and set field values using reflection.
	 */
	public static final class Fields {
		
		private Fields() {}
		
		//OBECJT GET
		
		public static Object getField(final String fieldName, final Object instance) {
			return getField(instance.getClass(), fieldName, instance);
		}
		
		public static Object getField(final Class<?> declaringClass, final String fieldName, final Object instance) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(fieldName, "Field name must not be null");
			Validate.requireTypeOrNull(instance, declaringClass, "Instance must be a subtype of the type that contains the field");
			//Instance may be null
			
			final Field field = BaseReflectionUtils.getField(declaringClass, fieldName);
			if(field == null) return null;
			try {
				return field.get(instance);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				return null;
			}
		}
		
		public static Object getStaticField(final Class<?> declaringClass, final String fieldName) {
			return getField(declaringClass, fieldName, null);
		}
		
		public static Optional<Object> getStaticFieldOptional(final Class<?> declaringClass, final String fieldName) {
			return getFieldOptional(declaringClass, fieldName, null);
		}
		
		public static Optional<Object> getFieldOptional(final String fieldName, final Object instance) {
			return getFieldOptional(instance.getClass(), fieldName, instance);
		}
		
		public static Optional<Object> getFieldOptional(final Class<?> declaringClass, final String fieldName, final Object instance) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(fieldName, "Field name must not be null");
			Validate.requireTypeOrNull(instance, declaringClass, "Instance must be a subtype of the type that contains the field");
			//Instance may be null
			final Field field = BaseReflectionUtils.getField(declaringClass, fieldName);
			if(field == null) return null;
			try {
				return Optional.ofNullable(field.get(instance));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				return null;
			}
		}
		
		//TYPED GET - can throw classcastexceptions
		
		public static <T> T getField(final String fieldName, final Object instance, final Class<T> fieldType) {
			return getField(instance.getClass(), fieldName, instance, fieldType);
		}
		
		@SuppressWarnings("unchecked")
		public static <T> T getField(final Class<?> declaringClass, final String fieldName, final Object instance, final Class<T> fieldType) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(fieldName, "Field name must not be null");
			Objects.requireNonNull(fieldType, "Field type must not be null");
			Validate.requireTypeOrNull(instance, declaringClass, "Instance must be a subtype of the type that contains the field");
			//Instance may be null
			
			final Field field = BaseReflectionUtils.getField(declaringClass, fieldName);
			if(field == null) return null;
			try {
				return (T) field.get(instance);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				return null;
			}
		}
		
		public static <T> T getStaticField(final Class<?> declaringClass, final String fieldName, final Class<T> fieldType) {
			return getField(declaringClass, fieldName, null, fieldType);
		}
		
		public static <T> Optional<T> getStaticFieldOptional(final Class<?> declaringClass, final String fieldName, final Class<T> fieldType) {
			return getFieldOptional(declaringClass, fieldName, null, fieldType);
		}
		
		public static <T> Optional<T> getFieldOptional(final String fieldName, final Object instance, final Class<T> fieldType) {
			return getFieldOptional(instance.getClass(), fieldName, instance, fieldType);
		}
		
		@SuppressWarnings("unchecked")
		public static <T> Optional<T> getFieldOptional(final Class<?> declaringClass, final String fieldName, final Object instance, final Class<T> fieldType) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(fieldName, "Field name must not be null");
			Objects.requireNonNull(fieldType, "Field type must not be null");
			Validate.requireTypeOrNull(instance, declaringClass, "Instance must be a subtype of the type that contains the field");
			//Instance may be null
			final Field field = BaseReflectionUtils.getField(declaringClass, fieldName);
			if(field == null) return null;
			try {
				return Optional.ofNullable((T) field.get(instance));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				return null;
			}
		}
		
		//SET
		
		public static boolean setField(final Class<?> declaringClass, final String fieldName, final Object instance, final Object value) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(fieldName, "Field name must not be null");
			Validate.requireTypeOrNull(instance, declaringClass, "Instance must be a subtype of the type that contains the field");
			//Instance and value may be null
			final Field field = BaseReflectionUtils.getField(declaringClass, fieldName);
			if(field == null) return false;
			try {
				field.set(instance, value);
				return true;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				return false;
			}
		}
		
		public static boolean setField(final String fieldName, final Object instance, final Object value) {
			return setField(instance.getClass(), fieldName, instance, value);
		}
		
		public static boolean setStaticField(final Class<?> declaringClass, final String fieldName, final Object value) {
			return setField(declaringClass, fieldName, null, value);
		}
		
	}

	/**
	 * Utility Methods that execute methods using reflection
	 */
	public static final class Methods {
		
		//NOT TYPED
		//normal, optional, static, normal-inferred, optional-inferred, static-optional
		
		//Normal
		public static Object executeMethod(final Class<?> declaringClass, final String methodName, final Object instance, final Parameters params) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(methodName, "Method name must not be null");
			Objects.requireNonNull(params, "Parameters must not be null");
			Validate.requireTypeOrNull(instance, declaringClass, "Instance must be a subtype of the type that contains the method");
			if(!params.hasValues()) throw new IllegalArgumentException("Parameter object must contain values for method execution");
			
			final Method method = BaseReflectionUtils.getMethod(declaringClass, methodName, params);
			if(method == null) return null;
			try {
				return method.invoke(instance, params.getValueArray());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				return null;
			}
		}
		
		//optional
		public static Optional<Object> executeMethodOptional(final Class<?> declaringClass, final String methodName, final Object instance, final Parameters params) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(methodName, "Method name must not be null");
			Objects.requireNonNull(params, "Parameters must not be null");
			Validate.requireTypeOrNull(instance, declaringClass, "Instance must be a subtype of the type that contains the method");
			if(!params.hasValues()) throw new IllegalArgumentException("Parameter object must contain values for method execution");
			
			final Method method = BaseReflectionUtils.getMethod(declaringClass, methodName, params);
			if(method == null) return null; //on error null
			try {
				return Optional.ofNullable(method.invoke(instance, params.getValueArray()));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				return null; //on error null
			}
		}
		
		//static
		public static Object executeStaticMethod(final Class<?> declaringClass, final String methodName, final Parameters params) {
			return executeMethod(declaringClass, methodName, null, params);
		}
		
		//normal-inferred
		public static Object executeMethod(final String methodName, final Object instance, final Parameters params) {
			return executeMethod(instance.getClass(), methodName, instance, params);
		}
		
		//optional-inferred
		public static Optional<Object> executeMethodOptional(final String methodName, final Object instance, final Parameters params) {
			return executeMethodOptional(instance.getClass(), methodName, instance, params);
		}
		
		public static Optional<Object> executeStaticMethodOptional(final Class<?> declaringClass, final String methodName, final Parameters params) {
			return executeMethodOptional(declaringClass, methodName, null, params);
		}
		
		//TYPED
		
		//Normal
		@SuppressWarnings("unchecked")
		public static <T> T executeMethod(final Class<?> declaringClass, final String methodName, final Object instance, final Parameters params, final Class<T> returnType) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(methodName, "Method name must not be null");
			Objects.requireNonNull(params, "Parameters must not be null");
			Objects.requireNonNull(returnType, "Return type must not be null");
			Validate.requireTypeOrNull(instance, declaringClass, "Instance must be a subtype of the type that contains the method");
			if(!params.hasValues()) throw new IllegalArgumentException("Parameter object must contain values for method execution");

			final Method method = BaseReflectionUtils.getMethod(declaringClass, methodName, params);
			if(method == null) return null;
			try {
				return (T) method.invoke(instance, params.getValueArray());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				return null;
			}
		}

		//optional
		@SuppressWarnings("unchecked")
		public static <T> Optional<T> executeMethodOptional(final Class<?> declaringClass, final String methodName, final Object instance, final Parameters params, final Class<T> returnType) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(methodName, "Method name must not be null");
			Objects.requireNonNull(params, "Parameters must not be null");
			Objects.requireNonNull(returnType, "Return type must not be null");
			Validate.requireTypeOrNull(instance, declaringClass, "Instance must be a subtype of the type that contains the method");
			if(!params.hasValues()) throw new IllegalArgumentException("Parameter object must contain values for method execution");

			final Method method = BaseReflectionUtils.getMethod(declaringClass, methodName, params);
			if(method == null) return null; //on error null
			try {
				return Optional.ofNullable((T) method.invoke(instance, params.getValueArray()));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				return null; //on error null
			}
		}

		//static
		public static <T> T executeStaticMethod(final Class<?> declaringClass, final String methodName, final Parameters params, final Class<T> returnType) {
			return executeMethod(declaringClass, methodName, null, params, returnType);
		}

		//normal-inferred
		public static <T> T executeMethod(final String methodName, final Object instance, final Parameters params, final Class<T> returnType) {
			return executeMethod(instance.getClass(), methodName, instance, params, returnType);
		}

		//optional-inferred
		public static <T> Optional<T> executeMethodOptional(final String methodName, final Object instance, final Parameters params, final Class<T> returnType) {
			return executeMethodOptional(instance.getClass(), methodName, instance, params, returnType);
		}

		public static <T> Optional<T> executeStaticMethodOptional(final Class<?> declaringClass, final String methodName, final Parameters params, final Class<T> returnType) {
			return executeMethodOptional(declaringClass, methodName, null, params, returnType);
		}
		
		//VOID
		//Don't need optional variants: normal, static, normal-inferred
		
		//Normal
		public static boolean executeVoidMethod(final Class<?> declaringClass, final String methodName, final Object instance, final Parameters params) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(methodName, "Method name must not be null");
			Objects.requireNonNull(params, "Parameters must not be null");
			Validate.requireTypeOrNull(instance, declaringClass, "Instance must be a subtype of the type that contains the method");
			if(!params.hasValues()) throw new IllegalArgumentException("Parameter object must contain values for method execution");

			final Method method = BaseReflectionUtils.getMethod(declaringClass, methodName, params);
			if(method == null) return false;
			try {
				method.invoke(instance, params.getValueArray());
				return true;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				return false;
			}
		}

		//static
		public static boolean executeStaticVoidMethod(final Class<?> declaringClass, final String methodName, final Parameters params) {
			return executeVoidMethod(declaringClass, methodName, null, params);
		}

		//normal-inferred
		public static boolean executeVoidMethod(final String methodName, final Object instance, final Parameters params) {
			return executeVoidMethod(instance.getClass(), methodName, instance, params);
		}

	}
	
	/**
	 * Utility Methods that create new instances using reflection
	 */
	public static class Constructors {
		
		@SuppressWarnings("unchecked")
		public static <T> T constructObject(final Class<T> declaringClass, Parameters params) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(params, "Parameters must not be null");
			if(!params.hasValues()) throw new IllegalArgumentException("Parameter object must contain values for constructor execution");
			
			final Constructor<?> constructor = BaseReflectionUtils.getConstructor(declaringClass, params.getTypeArray());
			if(constructor == null) return null;
			try {
				return (T) constructor.newInstance(params.getValueArray());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException	| InvocationTargetException e) {
				return null;
			}
		}
		
		@SuppressWarnings("unchecked")
		public static <T> T constructObjectUnchecked(final Class<T> declaringClass, Parameters params) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(params, "Parameters must not be null");
			if(!params.hasValues()) throw new IllegalArgumentException("Parameter object must contain values for constructor execution");
			
			Constructor<T> constructor = BaseReflectionUtils.getConstructor(declaringClass, params);
			if(constructor == null) return null;
			ConstructorAccessor accessor = QuickReflectionUtils.Methods.executeMethod(Constructor.class, "acquireConstructorAccessor",
					constructor, Parameters.empty(), ConstructorAccessor.class);
			if(accessor == null) return null;
			try {
				return (T) accessor.newInstance(params.getValueArray());
			} catch (InstantiationException | IllegalArgumentException | InvocationTargetException e) {
				return null;
			}
		}
		
		@RequireUndocumented("sun.misc.unsafe")
		public static <T> T constructUninitializedObject(final Class<T> declaringClass) {
			return UnsafeUtils.getInstanceWithoutConstructor(declaringClass);
		}
		
	}
	
}
