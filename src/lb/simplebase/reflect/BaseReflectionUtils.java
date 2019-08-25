package lb.simplebase.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import lb.simplebase.log.LogHelper;
import lb.simplebase.log.LogLevel;
import lb.simplebase.log.Logger;

/**
 * Basic reflection operations for getting {@link Field}, {@link Method} and {@link Constructor} references.
 * Used by other Reflection utility classes, but can also be used directly.
 */
public final class BaseReflectionUtils {
	
	private static final BaseReflectionUtilsImpl NORMAL = new NormalReflectionUtils();
	private static final BaseReflectionUtilsImpl PRIVILEGED = new PrivilegedReflectionUtils(NORMAL);
	private static BaseReflectionUtilsImpl INSTANCE = NORMAL;
	
	public static final Class<?>[] EMPTY = null;
	public static final String CONSTRUCTOR_NAME = "<init>";
	
	private BaseReflectionUtils() {}
	
	/**
	 * Tries to find a {@link Field} of any visiblity in the class parameter, or if that is not found, a public field in any superclass.
	 * The returned field will be accessible.
	 * @param declaringClass The class that contains the field
	 * @param fieldName The name of the field
	 * @return The {@link Field} object that represents the field, or {@code null} if the field was not found
	 */
	public static Field getField(final Class<?> declaringClass, final String fieldName) {
		return INSTANCE.getField(declaringClass, fieldName);
	}
	
	/**
	 * Tries to find a {@link Method} of any visiblity in the class parameter, or if that is not found, a public method in any superclass.
	 * The returned method will be accessible.
	 * @param declaringClass The class that contains the method
	 * @param methodName The name of the method
	 * @param signature The parameter types of the method. May be an empty array or {@code null} if the method has no parameters
	 * @return The {@link Method} that represents the method, or {@code null} if the method was not found
	 */
	public static Method getMethod(final Class<?> declaringClass, final String methodName, final Class<?>... signature) {
		return INSTANCE.getMethod(declaringClass, methodName, signature);
	}
	
	/**
	 * Tries to find a {@link Method} of any visiblity in the class parameter, or if that is not found, a public method in any superclass.
	 * The returned method will be accessible.
	 * @param declaringClass The class that contains the method
	 * @param methodName The name of the method
	 * @param signature The {@link Parameters} object representing the parameter types of the method. Values are not required
	 * @return The {@link Method} that represents the method, or {@code null} if the method was not found
	 */
	public static Method getMethod(final Class<?> declaringClass, final String methodName, final Parameters signature) {
		return INSTANCE.getMethod(declaringClass, methodName, signature.getTypeArray());
	}
	
	/**
	 * Tries to find a {@link Constructor} of any visiblity in the class parameter.
	 * The returned constructor will be accessible.
	 * @param <T> The type of the class and the returned constructor
	 * @param declaringClass The class that contains the constructor
	 * @param signature The parameter types of the constructor
	 * @return The {@link Constructor} that represents the constructor, or {@code null} if the constructor was not found
	 */
	public static <T> Constructor<T> getConstructor(final Class<T> declaringClass, final Class<?>... signature) {
		return INSTANCE.getConstructor(declaringClass, signature);
	}
	
	/**
	 * Tries to find a {@link Constructor} of any visiblity in the class parameter.
	 * The returned constructor will be accessible.
	 * @param <T> The type of the class and the returned constructor
	 * @param declaringClass The class that contains the constructor
	 * @param signature The parameter types of the constructor
	 * @return The {@link Constructor} that represents the constructor, or {@code null} if the constructor was not found
	 */
	public static <T> Constructor<T> getConstructor(final Class<T> declaringClass, final Parameters signature) {
		return INSTANCE.getConstructor(declaringClass, signature.getTypeArray());
	}
	
	/**
	 * After this method was called, all reflection operations in this package will run as privileged actions with all premissions of the
	 * library protection domain.<br>Requires the {@code RuntimePermission("accessDeclaredMembers")} and {@code ReflectPermission("suppressAccessChecks")}.
	 * @throws AccessControlException If the required permission was denied
	 */
	public static void usePrivileged() throws AccessControlException {
		AccessController.checkPermission(new RuntimePermission("accessDeclaredMembers"));
		AccessController.checkPermission(new ReflectPermission("suppressAccessChecks"));
		INSTANCE = PRIVILEGED;
	}
	
	protected static Logger REF_LOG = null;
	protected static boolean enabled = false;
	
	public static void enableErrorLogging() {
		if(REF_LOG == null) REF_LOG = LogHelper.create("Reflection", LogLevel.ERROR);
		enabled = true;
	}
	
	public static void disableErrorLogging() {
		enabled = false;
	}
	
	private static abstract class BaseReflectionUtilsImpl {
		
		protected abstract Field getField(final Class<?> declaringClass, final String fieldName);
		protected abstract Method getMethod(final Class<?> declaringClass, final String methodName, final Class<?>... signature);
		protected abstract <T> Constructor<T> getConstructor(final Class<T> declaringClass, final Class<?>... signature);
		
	}
	
	private static final class NormalReflectionUtils extends BaseReflectionUtilsImpl {
		
		private NormalReflectionUtils() {}

		@Override
		protected Field getField(final Class<?> declaringClass, final String fieldName) {
			try {
				final Field found1 = declaringClass.getDeclaredField(fieldName); //first, search in the class
				found1.setAccessible(true);
				return found1;
			} catch (NoSuchFieldException | SecurityException e) { //Try again for public fields only
				//Retry for NoSuchFieldException, because the field may be in a superclass
				//Retry for SecurityException, because accessDeclaredMembers permission might have been denied but the field is actually not in the class, but in a superclass
				try {
					final Field found2 = declaringClass.getField(fieldName); //Find the field
					found2.setAccessible(true); //And make it accessible //Is this necessary for public field??
					return found2;
				} catch (NoSuchFieldException | SecurityException e1) {
					if(enabled) REF_LOG.error("Field " + fieldName + " not found in " + declaringClass.getSimpleName() + " or superclass", e1);
					return null; //Can't do anything about it
				}
			}
		}

		@Override
		protected Method getMethod(final Class<?> declaringClass, final String methodName, final Class<?>... signature) {
			try {
				final Method found1 = declaringClass.getDeclaredMethod(methodName, signature); //first, search in the class
				found1.setAccessible(true);
				return found1;
			} catch (NoSuchMethodException | SecurityException e) { //Try again for public fields only
				//Retry for NoSuchMethodException, because the method may be in a superclass
				//Retry for SecurityException, because accessDeclaredMembers permission might have been denied but the method is actually not in the class, but in a superclass
				try {
					final Method found2 = declaringClass.getMethod(methodName, signature); //Find the field
					found2.setAccessible(true); //And make it accessible //Is this necessary for public field??
					return found2;
				} catch (NoSuchMethodException | SecurityException e1) {
					if(enabled) REF_LOG.error("Method " + methodName + " not found in " + declaringClass.getSimpleName() + " or superclass", e1);
					return null; //Can't do anything about it
				}
			}
		}

		@Override
		protected <T> Constructor<T> getConstructor(final Class<T> declaringClass, final Class<?>... signature) {
			try {
				Constructor<T> found = declaringClass.getConstructor(signature); //Find a constructor
				found.setAccessible(true);
				return found;
			} catch (NoSuchMethodException | SecurityException e) { //Or not
				if(enabled) REF_LOG.error("Constructor not found in " + declaringClass.getSimpleName(), e);
				return null;
			}
		}
		
	}
	
	private static final class PrivilegedReflectionUtils extends BaseReflectionUtilsImpl {
		
		private final BaseReflectionUtilsImpl delegate;
		
		private PrivilegedReflectionUtils(final BaseReflectionUtilsImpl delegate) {
			this.delegate = delegate;
		}

		@Override
		protected Field getField(final Class<?> declaringClass, final String fieldName) {
			return AccessController.doPrivileged((PrivilegedAction<Field>) () -> delegate.getField(declaringClass, fieldName));
		}

		@Override
		protected Method getMethod(final Class<?> declaringClass, final String methodName, final Class<?>... signature) {
			return AccessController.doPrivileged((PrivilegedAction<Method>) () -> delegate.getMethod(declaringClass, methodName, signature));
		}

		@Override
		protected <T> Constructor<T> getConstructor(final Class<T> declaringClass, final Class<?>... signature) {
			return AccessController.doPrivileged((PrivilegedAction<Constructor<T>>) () -> delegate.getConstructor(declaringClass, signature));
		}
		
	}
	
}
