package lb.simplebase.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import lb.simplebase.util.Validate;

public final class CachedReflectionUtils {

	private CachedReflectionUtils() {}
	
	public static final class Fields {
		
		public static FieldAccess<Object> getFieldAccess(final Class<?> declaringClass, final String fieldName) {
			return getFieldAccess(declaringClass, fieldName, Object.class);
		}
		
		public static FieldAccess<Object> getBoundFieldAccess(final Class<?> declaringClass, final String fieldName, final Object instance) {
			return getBoundFieldAccess(declaringClass, fieldName, instance, Object.class);
		}
		
		//Typed
		
		public static <T> FieldAccess<T> getFieldAccess(final Class<?> declaringClass, final String fieldName, final Class<T> fieldType) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(fieldName, "Field name must not be null");
			Objects.requireNonNull(fieldType, "Field type must not be null");
			
			final Field field = BaseReflectionUtils.getField(declaringClass, fieldName);
			if(field == null) return null;
			return new FieldAccess.ReflectionFieldAccess<>(field, null);
		}
		
		public static <T> FieldAccess<T> getBoundFieldAccess(final Class<?> declaringClass, final String fieldName, final Object instance, final Class<T> fieldType) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(fieldName, "Field name must not be null");
			Objects.requireNonNull(fieldType, "Field type must not be null");
			Validate.requireType(instance, declaringClass, "Instance must be a subtype of the type that contains the field"); //May be null
			
			final Field field = BaseReflectionUtils.getField(declaringClass, fieldName);
			if(field == null) return null;
			return new FieldAccess.ReflectionFieldAccess<>(field, instance);
		}
		
	}

	public static final class Methods {
		
		public static MethodAccess<Object> getMethodAccess(final Class<?> declaringClass, final String methodName, final Parameters params) {
			return getMethodAccess(declaringClass, methodName, Object.class, params.getTypeArray());
		}
		
		public static MethodAccess<Object> getMethodAccess(final Class<?> declaringClass, final String methodName, final Class<?>...params) {
			return getMethodAccess(declaringClass, methodName, Object.class, params);
		}
		
		public static MethodAccess<Object> getBoundMethodAccess(final Class<?> declaringClass, final String methodName, final Object instance, final Parameters params) {
			return getBoundMethodAccess(declaringClass, methodName, instance, Object.class, params.getTypeArray());
		}
		
		public static MethodAccess<Object> getBoundMethodAccess(final Class<?> declaringClass, final String methodName, final Object instance, final Class<?>...params) {
			return getBoundMethodAccess(declaringClass, methodName, instance, Object.class, params);
		}
		
		public static <T> MethodAccess<T> getMethodAccess(final Class<?> declaringClass, final String methodName, final Parameters params, final Class<T> returnType) {
			return getMethodAccess(declaringClass, methodName, returnType, params.getTypeArray());
		}
		
		public static <T> MethodAccess<T> getMethodAccess(final Class<?> declaringClass, final String methodName, final Class<T> returnType, final Class<?>...params) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(methodName, "Method name must not be null");
			Objects.requireNonNull(params, "Parameters must not be null");
			
			final Method method = BaseReflectionUtils.getMethod(declaringClass, methodName, params);
			if(method == null) return null;
			
			return new MethodAccess.ReflectionMethodAccess<>(method, null);
		}
		
		public static <T> MethodAccess<T> getBoundMethodAccess(final Class<?> declaringClass, final String methodName, final Object instance, final Parameters params, final Class<T> returnType) {
			return getBoundMethodAccess(declaringClass, methodName, instance, returnType, params.getTypeArray());
		}
		
		public static <T> MethodAccess<T> getBoundMethodAccess(final Class<?> declaringClass, final String methodName, final Object instance, final Class<T> returnType, final Class<?>...params) {
			Objects.requireNonNull(declaringClass, "Declaring class must not be null");
			Objects.requireNonNull(methodName, "Method name must not be null");
			Objects.requireNonNull(params, "Parameters must not be null");
			Validate.requireType(instance, declaringClass, "Instance must be a subtype of the type that contains the method");
			
			final Method method = BaseReflectionUtils.getMethod(declaringClass, methodName, params);
			if(method == null) return null;
			
			return new MethodAccess.ReflectionMethodAccess<>(method, instance);
		}
		
		public static MethodAccess<Void> getVoidMethodAccess(final Class<?> declaringClass, final String methodName, final Parameters params) {
			return getMethodAccess(declaringClass, methodName, void.class, params.getTypeArray());
		}
		
		public static MethodAccess<Void> getBoundVoidMethodAccess(final Class<?> declaringClass, final String methodName, final Object instance, final Parameters params) {
			return getBoundMethodAccess(declaringClass, methodName, instance, void.class, params.getTypeArray());
		}
		
		public static MethodAccess<Void> getVoidMethodAccess(final Class<?> declaringClass, final String methodName, final Class<?>...params) {
			return getMethodAccess(declaringClass, methodName, void.class, params);
		}
		
		public static MethodAccess<Void> getBoundVoidMethodAccess(final Class<?> declaringClass, final String methodName, final Object instance, final Class<?>...params) {
			return getBoundMethodAccess(declaringClass, methodName, instance, void.class, params);
		}
		
	}
}
