package lb.simplebase.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

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
	public static <T extends Enum<T>> T getInstanceAlt(Class<T> clazz, Signature[] params) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalArgumentException, InvocationTargetException {
		Constructor<T> constructor = clazz.getDeclaredConstructor(Signature.createTypeArray(params));
		ConstructorAccessor accessor = ReflectionUtils.executeDeclaredMethod(Constructor.class, "acquireConstructorAccessor",
				constructor, ConstructorAccessor.class, Signature.empty());
		return (T) accessor.newInstance(Signature.createValueArray(params));
	}
	
	public static <T extends Enum<T>> T getInstance(Class<T> clazz, String name, int ordinal, Signature[] params) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalArgumentException, InvocationTargetException {
		Objects.requireNonNull(name, "Enum item name must not be null");
		Objects.requireNonNull(clazz, "Enum class must not be null");
		
		final Signature[] fullSig;
		if(params == null || params.length == 0) {
			fullSig = Signature.of(String.class, name, int.class, ordinal);
		} else {
			fullSig = new Signature[params.length + 2];
			fullSig[0] = new Signature(name);
			fullSig[1] = new Signature(ordinal, int.class);
			System.arraycopy(params, 0, fullSig, 2, params.length);
		}
		
		return getInstanceAlt(clazz, fullSig);
	}
	
	public static <T extends Enum<T>> T getInstanceOrNull(Class<T> clazz, String name, int ordinal, Signature[] params) {
		try {
			return getInstance(clazz, name, ordinal, params);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalArgumentException| InvocationTargetException e) {
			return null;
		}
	}
}
