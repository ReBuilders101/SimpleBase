package lb.simplebase.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lb.simplebase.util.ReflectedMethod;

/**
 * Contains static utility methods for reflection. 
 */
public final class ReflectionUtils {
	
	/**
	 * Empty method parameter list
	 */
	public static final Class<?>[] EMPTY = new Class<?>[]{};

	private ReflectionUtils() {}
	
	/**
	 * Returns the content of a field. The field can be declared in the class in the <i>clazz</i> parameter,
	 * or in a superclass of this class. However, it must be a visible / public field.
	 * To get private fields, use {@link #getDeclaredField(Class, String, Object, Class)}.<br>
	 * If any exception occurs while getting the value of the field, <code>null</code> is returned.
	 * @param <T> The type of the field, and the return type
	 * @param <C> The type of the class that contains the field and the instance
	 * @param clazz The class that contains the field
	 * @param fieldName The name of the field
	 * @param object The instance of the class for which the value should be read
	 * @param fieldType The type of the field
	 * @return The content of the field for this instance
	 * @see Class#getField(String)
	 */
	@SuppressWarnings("unchecked")
	public static <T, C> T getField(Class<C> clazz, String fieldName, C object, Class<T> fieldType) {
		try {
			Field field = accessField(clazz, fieldName);
			return (T) field.get(object);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	/**
	 * Returns the content of a static field.
	 * The static field must be declared in this class, and not in a superclass.
	 * The field may have any access modifier.<br>
	 * If any exception occurs while getting the value of the field, <code>null</code> is returned.
	 * @param <T> The type of the field, and the return type
	 * @param clazz The class that contains the static field
	 * @param fieldName The name of the field
	 * @param fieldType The type of the field
	 * @return The content of the field
	 */
	public static <T> T getStaticField(Class<?> clazz, String fieldName, Class<T> fieldType) {
		return getDeclaredField(clazz, fieldName, null, fieldType);
	}
	
	/**
	 * Returns the content of a field. The field must be declared in the class in the <i>clazz</i> parameter,
	 * not in a superclass of this class. The Field may have any access modifier.<br>
	 * If any exception occurs while getting the value of the field, <code>null</code> is returned.
	 * @param <T> The type of the field, and the return type
	 * @param <C> The type of the class that contains the field and the instance
	 * @param clazz The class that contains the field
	 * @param fieldName The name of the field
	 * @param object The instance of the class for which the value should be read
	 * @param fieldType The type of the field
	 * @return The content of the field for this instance
	 * @see Class#getDeclaredField(String)
	 */
	@SuppressWarnings("unchecked")
	public static <T, C> T getDeclaredField(Class<C> clazz, String fieldName, C object, Class<T> fieldType) {
		try {
			Field field = accessDeclaredField(clazz, fieldName);
			return (T) field.get(object);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	/**
	 * Returns the content of a field. The field can be declared in the class in the <i>clazz</i> parameter,
	 * or in a superclass of this class. However, it must be a visible / public field.
	 * To get private fields, use {@link #getDeclaredField(Class, String, Object, Class)}.<br>
	 * If any exception occurs while getting the value of the field, <code>null</code> is returned.<br>
	 * Use {@link #getField(Class, String, Object, Class)} if you want the result to be casted to the field type.
	 * @param <C> The type of the class that contains the field and the instance
	 * @param clazz The class that contains the field
	 * @param fieldName The name of the field
	 * @param object The instance of the class for which the value should be read
	 * @return The content of the field for this instance
	 * @see Class#getField(String)
	 */
	public static <C> Object getField(Class<C> clazz, String fieldName, C object) {
		try {
			Field field = accessField(clazz, fieldName);
			return field.get(object);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	/**
	 * Returns the content of a static field.
	 * The static field must be declared in this class, and not in a superclass.
	 * The field may have any access modifier.<br>
	 * If any exception occurs while getting the value of the field, <code>null</code> is returned.<br>
	 * Use {@link #getStaticField(Class, String, Class)} if you want the result to be casted to the field type.
	 * @param clazz The class that contains the static field
	 * @param fieldName The name of the field
	 * @return The content of the field
	 */
	public static Object getStaticField(Class<?> clazz, String fieldName) {
		return getDeclaredField(clazz, fieldName, null);
	}
	
	/**
	 * Returns the content of a field. The field must be declared in the class in the <i>clazz</i> parameter,
	 * not in a superclass of this class. The Field may have any access modifier.<br>
	 * If any exception occurs while getting the value of the field, <code>null</code> is returned.<br>
	 * Use {@link #getDeclaredField(Class, String, Object, Class)} if you want the result to be casted to the field type.
	 * @param <C> The type of the class and the instance
	 * @param clazz The class that contains the field
	 * @param fieldName The name of the field
	 * @param object The instance of the class for which the value should be read
	 * @return The content of the field
	 * @see Class#getDeclaredField(String)
	 */
	public static <C> Object getDeclaredField(Class<C> clazz, String fieldName, C object) {
		try {
			Field field = accessDeclaredField(clazz, fieldName);
			return field.get(object);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	/**
	 * Creates a list of Fields similar to {@link Class#getFields()}, that contains all fields of that class
	 * and the superclasses. While the method on {@link Class} only returns public fields, this method
	 * also includes fields with other visibilities.
	 * @param clazz The class for which the field list should be created
	 * @return The list of all fields with any access modifier in the class and the superclasses  
	 */
	public static List<Field> getPrivateSuperFieldsList(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		
		Class<?> currentClass = clazz;
		do {
			Field[] currentFields = currentClass.getDeclaredFields();
			for(Field f : currentFields) fields.add(f);
		} while((currentClass = currentClass.getSuperclass()) != Object.class);
		return fields;
	}
	
	/**
	 * Reads the value from a {@link Field} object for an instance.
	 * If an exception occurs while reading the field value, <code>null</code> is returned.
	 * However, a result of <code>null</code> can also mean that the field contained the value <code>null</code>.
	 * @param <T> The type of the field, and the return type
	 * @param field The field that should be read from
	 * @param instance The instance that holds the value
	 * @param fieldType The type of the field
	 * @return The value of the field for this instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> T readFieldValue(Field field, Object instance, Class<T> fieldType) {
		try {
			return (T) field.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	/**
	 * Reads the value from a {@link Field} object for an instance.
	 * If an exception occurs while reading the field value, <code>null</code> is returned.
	 * However, a result of <code>null</code> can also mean that the field contained the value <code>null</code>.<br>
	 * If you want the result to be casted to the field type, use {@link #readFieldValue(Field, Object, Class)}.
	 * @param field The field that should be read from
	 * @param instance The instance that holds the value
	 * @return The value of the field for this instance
	 */
	public static Object readFieldValue(Field field, Object instance) {
		try {
			return field.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	/**
	 * Reads the value from a {@link Field} object, assuming that the field is a static field.
	 * If an exception occurs while reading the field value, <code>null</code> is returned.
	 * However, a result of <code>null</code> can also mean that the field contained the value <code>null</code>.
	 * @param <T> The type of the field, and the return type of the method
	 * @param field The static field that should be read from
	 * @param fieldType The type of the field
	 * @return The value of the static field
	 */
	public static <T> T readStaticFieldValue(Field field, Class<T> fieldType) {
		return readFieldValue(field, null, fieldType);
	}
	
	/**
	 * Reads the value from a {@link Field} object, assuming that the field is a static field.
	 * If an exception occurs while reading the field value, <code>null</code> is returned.
	 * However, a result of <code>null</code> can also mean that the field contained the value <code>null</code>.<br>
	 * If you want the result to be casted to the field type, use {@link #readStaticFieldValue(Field, Class)}.
	 * @param field The static field that should be read from
	 * @return The value of the static field
	 */
	public static Object readStaticFieldValue(Field field) {
		return readFieldValue(field, null);
	}
	
	///////////////////////////////////////METHODS////////////////////////////////////////////////////////////
	
	/**
	 * Executes a method for a given instance.
	 * The method can be defined in this class or in a superclass, but it must be visible.
	 * To execute methods that are not accessible, use {@link #executeDeclaredMethod(Class, String, Object, Signature...)}.
	 * The return value of the executed method is returned. If the method has the return type void, <code>null</code> is returned.<br>
	 * If an exception occurs while executing the method, <code>null</code> is returned. If the return type of the method is void,
	 * {@link #executeVoidMethod(Class, String, Object, Signature...)} can be used instead to get better information about whether the
	 * execution was successful.
	 * If the result should be casted to the return type of the executed method, use {@link #executeMethod(Class, String, Object, Class, Signature...)}.
	 * @param <C> The type of the class and the instance
	 * @param clazz The class that contains the method
	 * @param methodName The name of the method
	 * @param instance The instance of the class for which the method should be executed
	 * @param sig The {@link Signature} of the method (the types of parameters and the values)
	 * @return The return value of the method call
	 */
	public static <C> Object executeMethod(Class<C> clazz, String methodName, C instance, Signature...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = accessMethod(clazz, methodName, types);
			return method.invoke(instance, values);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

	/**
	 * Executes a method for a given instance.
	 * The method can be defined in this class or in a superclass, but it must be visible.
	 * To execute methods that are not accessible, use {@link #executeDeclaredMethod(Class, String, Object, Class, Signature...)}.
	 * The return value of the executed method is returned. If the method has the return type void, <code>null</code> is returned.<br>
	 * If an exception occurs while executing the method, <code>null</code> is returned. If the return type of the method is void,
	 * {@link #executeVoidMethod(Class, String, Object, Signature...)} can be used instead to get better information about whether the
	 * execution was successful.
	 * @param <T> The return type of the executed method, and of this method
	 * @param <C> The type of the class and the instance
	 * @param clazz The class that contains the method
	 * @param methodName The name of the method
	 * @param instance The instance of the class for which the method should be executed
	 * @param returnType The return type of the executed methods
	 * @param sig The {@link Signature} of the method (the types of parameters and the values)
	 * @return The return value of the method call
	 */
	@SuppressWarnings("unchecked")
	public static <T, C> T executeMethod(Class<C> clazz, String methodName, C instance, Class<T> returnType, Signature...sig) {
		return (T) executeMethod(clazz, methodName, instance, sig);
	}
	
	/**
	 * Executes a method for a given instance.
	 * The method must be defined in this class and not in a superclass, but it may have any access modifier.
	 * The return value of the executed method is returned. If the method has the return type void, <code>null</code> is returned.<br>
	 * If an exception occurs while executing the method, <code>null</code> is returned. If the return type of the method is void,
	 * {@link #executeDeclaredVoidMethod(Class, String, Object, Signature...)} can be used instead to get better information about whether the
	 * execution was successful.
	 * If the result should be casted to the return type of the executed method, use {@link #executeDeclaredMethod(Class, String, Object, Class, Signature...)}.
	 * @param <C> The type of the class and the instance
	 * @param clazz The class that contains the method
	 * @param methodName The name of the method
	 * @param instance The instance of the class for which the method should be executed
	 * @param sig The {@link Signature} of the method (the types of parameters and the values)
	 * @return The return value of the method call
	 */
	public static <C> Object executeDeclaredMethod(Class<C> clazz, String methodName, C instance, Signature...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = accessDeclaredMethod(clazz, methodName, types);
			return method.invoke(instance, values);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

	/**
	 * Executes a method for a given instance.
	 * The method must be defined in this class and not in a superclass, but it may have any access modifier.
	 * The return value of the executed method is returned. If the method has the return type void, <code>null</code> is returned.<br>
	 * If an exception occurs while executing the method, <code>null</code> is returned. If the return type of the method is void,
	 * {@link #executeDeclaredVoidMethod(Class, String, Object, Signature...)} can be used instead to get better information about whether the
	 * execution was successful.
	 * @param <T> The return type of the executed method, and of this method
	 * @param <C> The type of the class and the instance
	 * @param clazz The class that contains the method
	 * @param methodName The name of the method
	 * @param instance The instance of the class for which the method should be executed
	 * @param returnType The return type of the executed methods
	 * @param sig The {@link Signature} of the method (the types of parameters and the values)
	 * @return The return value of the method call
	 */
	@SuppressWarnings("unchecked")
	public static <T, C> T executeDeclaredMethod(Class<C> clazz, String methodName, C instance, Class<T> returnType, Signature...sig) {
		return (T) executeDeclaredMethod(clazz, methodName, instance, sig);
	}	
	
	/**
	 * Executes a static method.
	 * The return value of the executed method is returned. If the method has the return type void, <code>null</code> is returned.<br>
	 * If an exception occurs while executing the method, <code>null</code> is returned. If the return type of the method is void,
	 * {@link #executeStaticVoidMethod(Class, String, Signature...)} can be used instead to get better information about whether the
	 * execution was successful.<br>
	 * If the result should be casted to the return type of the executed method, use {@link #executeStaticMethod(Class, String, Class, Signature...)}. 
	 * @param clazz The class that contains the static method
	 * @param methodName The name of the method
	 * @param sig The {@link Signature} of the method (the types of parameters and the values)
	 * @return The return value of the method call
	 */
	public static Object executeStaticMethod(Class<?> clazz, String methodName, Signature...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = accessDeclaredMethod(clazz, methodName, types);
			return method.invoke(null, values);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

	/**
	 * Executes a static method.
	 * The return value of the executed method is returned. If the method has the return type void, <code>null</code> is returned.<br>
	 * If an exception occurs while executing the method, <code>null</code> is returned. If the return type of the method is void,
	 * {@link #executeStaticVoidMethod(Class, String, Signature...)} can be used instead to get better information about whether the
	 * execution was successful.
	 * @param <T> The return type of the executed method, and of this method
	 * @param clazz The class that contains the static method
	 * @param methodName The name of the method
	 * @param returnType The type that is returned by the executed method
	 * @param sig The {@link Signature} of the method (the types of parameters and the values)
	 * @return The return value of the method call
	 */
	@SuppressWarnings("unchecked")
	public static <T> T executeStaticMethod(Class<?> clazz, String methodName, Class<T> returnType, Signature...sig) {
		return (T) executeStaticMethod(clazz, methodName, sig);
	}
	
	/**
	 * Executes a method for an instance. 
	 * If the return type of the executed method is not <code>void</code>, the returned value is ignored.
	 * This method returns <code>true</code> if the method was executed successfully, and <code>false</code> if
	 * execution failed with an exception.<br>
	 * The method can be declared in this class or in a superclass, but it must be visible.
	 * To execute methods that are not accessible, use {@link #executeDeclaredVoidMethod(Class, String, Object, Signature...)}.
	 * @param <C> The type of the class and the instance
	 * @param clazz The class that contains the method
	 * @param methodName The name of the method
	 * @param instance The Instance of the class for which the method should be executed
	 * @param sig The {@link Signature} of the method (the types of parameters and the values)
	 * @return Whether the method execution was successful
	 */
	public static <C> boolean executeVoidMethod(Class<C> clazz, String methodName, C instance, Signature...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = accessMethod(clazz, methodName, types);
			method.invoke(instance, values);
			return true;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return false;
		}
	}
	
	/**
	 * Executes a method for an instance. 
	 * If the return type of the executed method is not <code>void</code>, the returned value is ignored.
	 * This method returns <code>true</code> if the method was executed successfully, and <code>false</code> if
	 * execution failed with an exception.<br>
	 * The method must be declared in this class and not in a superclass, but it may have any access modifier.
	 * @param <C> The type of the class and the instance
	 * @param clazz The class that contains the method
	 * @param methodName The name of the method
	 * @param instance The Instance of the class for which the method should be executed
	 * @param sig The {@link Signature} of the method (the types of parameters and the values)
	 * @return Whether the method execution was successful
	 */
	public static <C> boolean executeDeclaredVoidMethod(Class<C> clazz, String methodName, C instance, Signature...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = accessDeclaredMethod(clazz, methodName, types);
			method.invoke(instance, values);
			return true;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return false;
		}
	}
	
	/**
	 * Executes a static method.
	 * If the return type of the executed method is not <code>void</code>, the returned value is ignored.
	 * This method returns <code>true</code> if the method was executed successfully, and <code>false</code> if
	 * execution failed with an exception.<br>
	 * @param clazz The class that contains the static method
	 * @param methodName The name of the method
	 * @param sig The {@link Signature} of the method (the types of parameters and the values)
	 * @return Whether the method execution was successful
	 */
	public static boolean executeStaticVoidMethod(Class<?> clazz, String methodName, Signature...sig) {
		try {
			Class<?>[] types = Signature.createTypeArray(sig);
			Object[] values = Signature.createValueArray(sig);
			Method method = accessDeclaredMethod(clazz, methodName, types);
			method.invoke(null, values);
			return true;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return false;
		}
	}
	
	/**
	 * Returns a {@link ReflectedMethod}, which is a {@link FunctionalInterface}, that will execute this
	 * method with its {@link ReflectedMethod#getOrExecute(Object...)} method.
	 * The method can be declared in this class or in a superclass, but it must be accessible.
	 * To create executors from methods that are not accessible, use {@link #getDeclaredMethodExecutor(Class, String, Object, Class...)}.<br>
	 * If an exception occurs while creating the functional interface from the method, <code>null</code> is returned.  
	 * @param <T> The type of the class and the instance
	 * @param clazz The class that contains the method
	 * @param methodName The name of the method
	 * @param instance The instance for which the method should be called
	 * @param sig The signature of the method (Types of parameters)
	 * @return A {@link ReflectedMethod} that contains the method
	 */
	public static <T> ReflectedMethod getMethodExecutor(Class<T> clazz, String methodName, T instance, Class<?>...sig) {
		try {
			final Method method = accessMethod(clazz, methodName, sig);
			return (o) -> method.invoke(instance, o);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Returns a {@link ReflectedMethod}, which is a {@link FunctionalInterface}, that will execute this
	 * method with its {@link ReflectedMethod#getOrExecute(Object...)} method.
	 * The method must be declared in this class and not in a superclass, but it may have any access modifier.
	 * If an exception occurs while creating the functional interface from the method, <code>null</code> is returned.  
	 * @param <T> The type of the class and the instance
	 * @param clazz The class that contains the method
	 * @param methodName The name of the method
	 * @param instance The instance for which the method should be called
	 * @param sig The signature of the method (Types of parameters)
	 * @return A {@link ReflectedMethod} that contains the method
	 */
	public static <T> ReflectedMethod getDeclaredMethodExecutor(Class<T> clazz, String methodName, T instance, Class<?>...sig) {
		try {
			final Method method = accessDeclaredMethod(clazz, methodName, sig);
			return (o) -> method.invoke(instance, o);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Returns a {@link ReflectedMethod}, which is a {@link FunctionalInterface}, that will execute this static
	 * method with its {@link ReflectedMethod#getOrExecute(Object...)} method.
	 * The method must be declared in this class and not in a superclass, but it may have any access modifier.
	 * If an exception occurs while creating the functional interface from the method, <code>null</code> is returned.  
	 * @param clazz The class that contains the static method
	 * @param methodName The name of the method
	 * @param sig The signature of the method (Types of parameters)
	 * @return A {@link ReflectedMethod} that contains the method
	 */
	public static ReflectedMethod getStaticMethodExecutor(Class<?> clazz, String methodName, Class<?>...sig) {
		return getDeclaredMethodExecutor(clazz, methodName, null, sig);
	}
	
	/////////////////////////////////METHOD HANDLES//////////////////////////////////////////////////
	
	/**
	 * Returns a {@link ReflectedMethod}, which is a {@link FunctionalInterface}, that will execute this
	 * method with its {@link ReflectedMethod#getOrExecute(Object...)} method.
	 * The method can be declared in the class of the <code>classAndInstance</code> parameter, or in a superclass.
	 * If an exception occurs while creating the functional interface from the method, <code>null</code> is returned.<p>  
	 * Uses {@link MethodHandle}s instead of reflection. 
	 * @param methodName The name of the method
	 * @param classAndInstance The instance for which the method should be called
	 * @param returnType The type that is returned by this method. If the method doesn't return anything, use <code>void.class</code> or {@link #getVoidMethodHandleExecutor(Class, String, Object, Class...)}
	 * @param sig The signature of the method (Types of parameters). can be <code>null</code> if the method has no parameters
	 * @return A {@link ReflectedMethod} that contains the method
	 * @see #getMethodExecutor(Class, String, Object, Class...)
	 */
	public static ReflectedMethod getMethodHandleExecutor(String methodName, Object classAndInstance, Class<?> returnType, Class<?>...sig) {
		if(returnType == null) return null;
		
		final MethodType type;
		if(sig == null || sig.length == 0) {
			type = MethodType.methodType(returnType);
		} else {
			type = MethodType.methodType(returnType, sig);
		}
		
		final Lookup lookup = MethodHandles.lookup();
		try {
			final MethodHandle handle = lookup.bind(classAndInstance, methodName, type);
			return handle::invoke;
		} catch (NoSuchMethodException | IllegalAccessException e) {
			return null;
		} 
	}
	
	/**
	 * Returns a {@link ReflectedMethod}, which is a {@link FunctionalInterface}, that will execute this
	 * method with its {@link ReflectedMethod#getOrExecute(Object...)} method.
	 * The method can be declared in the class of the <code>classAndInstance</code> parameter, or in a superclass.
	 * If an exception occurs while creating the functional interface from the method, <code>null</code> is returned.<p>  
	 * Uses {@link MethodHandle}s instead of reflection. 
	 * @param methodName The name of the method
	 * @param classAndInstance The instance for which the method should be called
	 * @param sig The signature of the method (Types of parameters). can be <code>null</code> if the method has no parameters
	 * @return A {@link ReflectedMethod} that contains the method
	 * @see #getMethodHandleExecutor(String, Object, Class, Class...)
	 */
	public static ReflectedMethod getVoidMethodHandleExecutor(String methodName, Object classAndInstance, Class<?>...sig) {
		return getMethodHandleExecutor(methodName, classAndInstance, void.class, sig);
	}
	
	
	/**
	 * Returns a {@link ReflectedMethod}, which is a {@link FunctionalInterface}, that will execute this
	 * method with its {@link ReflectedMethod#getOrExecute(Object...)} method.
	 * If an exception occurs while creating the functional interface from the method, <code>null</code> is returned.<p>  
	 * Uses {@link MethodHandle}s instead of reflection.
	 * @param clazz The class that contains the static method
	 * @param methodName The name of the method
	 * @param returnType The type that is returned by this method. If the method doesn't return anything, use <code>void.class</code>
	 * @param sig The signature of the method (Types of parameters). can be <code>null</code> if the method has no parameters
	 * @return A {@link ReflectedMethod} that contains the method
	 * @see #getMethodExecutor(Class, String, Object, Class...)
	 */
	public static ReflectedMethod getStaticMethodHandleExecutor(Class<?> clazz, String methodName, Class<?> returnType, Class<?>...sig) {
		if(returnType == null) return null;
		
		final MethodType type;
		if(sig == null || sig.length == 0) {
			type = MethodType.methodType(returnType);
		} else {
			type = MethodType.methodType(returnType, sig);
		}
		
		final Lookup lookup = MethodHandles.lookup();
		try {
			final MethodHandle handle = lookup.findStatic(clazz, methodName, type);
			return handle::invoke;
		} catch (NoSuchMethodException | IllegalAccessException e) {
			return null;
		} 
	}
	
	/////////////////////////////////SET FIELDS///////////////////////////////////////////////////
	
	/**
	 * Sets a field for an instance to a value.
	 * The field can be declared in the class in the <i>clazz</i> parameter,
	 * or in a superclass of this class. However, it must be a visible / public field.
	 * To get private fields, use {@link #setDeclaredField(Class, String, Object, Object)}.
	 * @param <C> The type of the class and the instance
	 * @param clazz The class that contains the field
	 * @param fieldName The name of the field
	 * @param instance The instance for which the field value should be set
	 * @param value The new value for the field
	 * @return Whether the value was set successfully
	 */
	public static <C> boolean setField(Class<C> clazz, String fieldName, C instance, Object value) {
		try {
			Field field = accessField(clazz, fieldName);
			field.set(instance, value);
			return true;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return false;
		}
	}
	
	/**
	 * Sets a field for an instance to a value.
	 * The field must be declared in the class in the <i>clazz</i> parameter, and it may have any
	 * visiblity / access modifier.
	 * @param <C> The type of the class and the instance
	 * @param clazz The class that contains the field
	 * @param fieldName The name of the field
	 * @param instance The instance for which the field value should be set
	 * @param value The new value for the field
	 * @return Whether the value was set successfully
	 */
	public static <C> boolean setDeclaredField(Class<C> clazz, String fieldName, C instance, Object value) {
		try {
			Field field = accessDeclaredField(clazz, fieldName);
			field.set(instance, value);
			return true;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return false;
		}
	}
	
	/**
	 * Sets a static field to a value. The field may have any access modifier.
	 * @param clazz The class that contains the field
	 * @param fieldName The name of the field
	 * @param value The new value for the field
	 * @return Whether the value was set successfully
	 */
	public static boolean setStaticField(Class<?> clazz, String fieldName, Object value) {
		try {
			Field field = accessField(clazz, fieldName);
			field.set(null, value);
			return true;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return false;
		}
	}
	
	
	
	/////////////////////////////////UTIL//////////////////////////////////////////////////////////////////
	
	/**
	 * Gets a {@link Field} object from the class and makes it accessible by calling {@link Field#setAccessible(boolean)}. 
	 * The field can be declared in this class or a superclass, but must be visible.<br>
	 * To include private fields, use {@link #accessDeclaredField(Class, String)}.
	 * @param clazz The class that contains the field
	 * @param fieldName The name of the field
	 * @return The accessible field object
	 * @throws NoSuchFieldException When the field was not found
	 * @throws SecurityException When the {@link SecurityManager} denies access to this class
	 */
	public static Field accessField(Class<?> clazz, String fieldName) throws NoSuchFieldException, SecurityException {
		Field field = clazz.getField(fieldName);
		if(!field.isAccessible()) field.setAccessible(true);
		return field;
	}
	
	/**
	 * Gets a {@link Field} object from the class and makes it accessible by calling {@link Field#setAccessible(boolean)}.
	 * The field must be declared in this class, and not in a superclass, but may have any access modifier.
	 * @param clazz The class that contains the field
	 * @param fieldName The name of the field
	 * @return The accessible field object
	 * @throws NoSuchFieldException When the field was not found
	 * @throws SecurityException When the {@link SecurityManager} denies access to this class
	 */
	public static Field accessDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException, SecurityException {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field;
	}
	
	/**
	 * Gets a {@link Method} object from the class and makes it accessible by calling {@link Method#setAccessible(boolean)}. 
	 * The field can be declared in this class or a superclass, but must be visible.<br>
	 * To include private methods, use {@link #accessDeclaredMethod(Class, String, Class[])}.
	 * @param clazz The class that contains the method
	 * @param methodName The name of the method
	 * @param sig The signature of the method (The types of the parameters)
	 * @return The accessible method object
	 * @throws NoSuchMethodException When the method was not found
	 * @throws SecurityException When the {@link SecurityManager} denies access to this class
	 */
	public static Method accessMethod(Class<?> clazz, String methodName, Class<?>[] sig) throws NoSuchMethodException, SecurityException {
		Method method = clazz.getMethod(methodName, sig);
		method.setAccessible(true);
		return method;
	}
	
	/**
	 * Gets a {@link Method} object from the class and makes it accessible by calling {@link Method#setAccessible(boolean)}. 
	 * The method must be declared in this class, and not in a superclass, but may have any access modifier.
	 * @param clazz The class that contains the method
	 * @param methodName The name of the method
	 * @param sig The signature of the method (The types of the parameters)
	 * @return The accessible method object
	 * @throws NoSuchMethodException When the method was not found
	 * @throws SecurityException When the {@link SecurityManager} denies access to this class
	 */
	public static Method accessDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] sig) throws NoSuchMethodException, SecurityException {
		Method method = clazz.getDeclaredMethod(methodName, sig);
		method.setAccessible(true);
		return method;
	}
}
