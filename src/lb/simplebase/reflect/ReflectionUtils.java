package lb.simplebase.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lb.simplebase.function.ReflectedMethod;

public final class ReflectionUtils {

	private ReflectionUtils() {}
	
	@SuppressWarnings("unchecked")
	public static <T, C> T getField(Class<C> clazz, String fieldName, C object, Class<T> fieldType) {
		try {
			Field field = accessField(clazz, fieldName);
			return (T) field.get(object);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	public static <T> T getStaticField(Class<?> clazz, String fieldName, Class<T> fieldType) {
		return getField(clazz, fieldName, null, fieldType);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, C> T getDeclaredField(Class<C> clazz, String fieldName, C object, Class<T> fieldType) {
		try {
			Field field = accessDeclaredField(clazz, fieldName);
			return (T) field.get(object);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	public static <T> T getStaticDeclaredField(Class<?> clazz, String fieldName, Class<T> fieldType) {
		return getDeclaredField(clazz, fieldName, null, fieldType);
	}
	
	public static <C> Object getField(Class<C> clazz, String fieldName, C object) {
		try {
			Field field = accessField(clazz, fieldName);
			return field.get(object);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	public static Object getStaticField(Class<?> clazz, String fieldName) {
		return getField(clazz, fieldName, null);
	}
	
	public static <C> Object getDeclaredField(Class<C> clazz, String fieldName, C object) {
		try {
			Field field = accessDeclaredField(clazz, fieldName);
			return field.get(object);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	public static Object getStaticDeclaredField(Class<?> clazz, String fieldName) {
		return getDeclaredField(clazz, fieldName, null);
	}
	
	public static List<Field> getPrivateSuperFieldsList(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		
		Class<?> currentClass = clazz;
		do {
			Field[] currentFields = currentClass.getDeclaredFields();
			for(Field f : currentFields) fields.add(f);
		} while((currentClass = currentClass.getSuperclass()) != Object.class);
		return fields;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Field field, Object instance, Class<T> fieldType) {
		try {
			return (T) field.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	public static Object getFieldValue(Field field, Object instance) {
		try {
			return field.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	public static <T> T getStaticFieldValue(Field field, Class<T> fieldType) {
		return getFieldValue(field, null, fieldType);
	}
	
	public static Object getStaticFieldValue(Field field) {
		return getFieldValue(field, null);
	}
	
	public static <C> Object executeMethod(Class<C> clazz, String methodName, C instance, Signature<?>...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = clazz.getMethod(methodName, types);
			return method.invoke(instance, values);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

	public static <T, C> T executeMethod(Class<C> clazz, String methodName, C instance, Class<T> returnType, Signature<?>...sig) {
		return (T) executeMethod(clazz, methodName, instance, returnType, sig);
	}
	
	public static <C> Object executeDeclaredMethod(Class<C> clazz, String methodName, C instance, Signature<?>...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = clazz.getDeclaredMethod(methodName, types);
			return method.invoke(instance, values);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

	public static <T, C> T executeDeclaredMethod(Class<C> clazz, String methodName, C instance, Class<T> returnType, Signature<?>...sig) {
		return (T) executeDeclaredMethod(clazz, methodName, instance, returnType, sig);
	}	
	
	public static Object executeStaticMethod(Class<?> clazz, String methodName, Signature<?>...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = clazz.getMethod(methodName, types);
			return method.invoke(null, values);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

	public static <T> T executeStaticMethod(Class<?> clazz, String methodName, Class<T> returnType, Signature<?>...sig) {
		return (T) executeStaticMethod(clazz, methodName, returnType, sig);
	}
	
	public static <C> boolean executeVoidMethod(Class<C> clazz, String methodName, C instance, Signature<?>...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = clazz.getMethod(methodName, types);
			method.invoke(instance, values);
			return true;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return false;
		}
	}
	
	public static <C> Object executeDeclaredVoidMethod(Class<C> clazz, String methodName, C instance, Signature<?>...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = clazz.getDeclaredMethod(methodName, types);
			method.invoke(instance, values);
			return true;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return false;
		}
	}
	
	public static boolean executeStaticVoidMethod(Class<?> clazz, String methodName, Signature<?>...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = clazz.getMethod(methodName, types);
			method.invoke(null, values);
			return true;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return false;
		}
	}
	
	public static <T> ReflectedMethod getMethodExecutor(Class<T> clazz, String methodName, T instance, Class<?>...sig) {
		try {
			final Method method = clazz.getMethod(methodName, sig);
			return (o) -> method.invoke(instance, o);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}
	}
	
	public static ReflectedMethod getStaticMethodExecutor(Class<?> clazz, String methodName, Class<?>...sig) {
		return getMethodExecutor(clazz, methodName, null, sig);
	}
	
	public static <C> boolean setField(Class<C> clazz, String fieldName, C instance, Object value) {
		try {
			Field field = accessField(clazz, fieldName);
			field.set(instance, value);
			return true;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return false;
		}
	}
	
	public static Field accessField(Class<?> clazz, String fieldName) throws NoSuchFieldException, SecurityException {
		Field field = clazz.getField(fieldName);
		if(!field.isAccessible()) field.setAccessible(true);
		return field;
	}
	
	public static Field accessDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException, SecurityException {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field;
	}
}
