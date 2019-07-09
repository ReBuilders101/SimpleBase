package lb.simplebase.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import sun.reflect.ConstructorAccessor;

public final class EnumUtils {
	
	private EnumUtils() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalArgumentException, InvocationTargetException {
		
	}
	
	/**
	 * Signature must be: String, int, &lt;other constructor params&gt;
	 * @param clazz
	 * @param params
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T getInstance(Class<T> clazz, Signature<?>[] params) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalArgumentException, InvocationTargetException {
		Constructor<T> constructor = clazz.getDeclaredConstructor(Signature.createTypeArray(params));
		ConstructorAccessor accessor = ReflectionUtils.executeDeclaredMethod(Constructor.class, "acquireConstructorAccessor",
				constructor, ConstructorAccessor.class, Signature.empty());
		return (T) accessor.newInstance(Signature.createValueArray(params));
	}
}
