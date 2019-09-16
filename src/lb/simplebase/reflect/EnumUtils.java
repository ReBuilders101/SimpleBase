package lb.simplebase.reflect;

import java.util.Objects;

import lb.simplebase.core.RequireUndocumented;
import lb.simplebase.util.Validate;

public final class EnumUtils {
	
	private EnumUtils() {}
	
	@RequireUndocumented("sun.reflect.ConstructorAccessor")
	public static <T extends Enum<T>> T getInstance(final Class<T> clazz, final String name, final int ordinal, final Parameters params){
		Objects.requireNonNull(name, "Enum item name must not be null");
		Objects.requireNonNull(clazz, "Enum class must not be null");
		Objects.requireNonNull(params, "Enum constructor parameters must not be null");
		Validate.requireTrue(clazz.isEnum(), "Class in parameter must be an enum class");
		Validate.requireMin(ordinal, 0, "Enum item ordinal must be larger than 0");
		
		final Parameters fullSig;
		if(params == null || params.getLength() == 0) {
			fullSig = Parameters.of(String.class, name, int.class, ordinal);
		} else {
			final Class<?>[] newTypes = new Class<?>[params.getLength() + 2];
			final Object[] newValues = new Object[params.getLength() + 2];
			newTypes[0] = String.class;
			newTypes[1] = int.class;
			newValues[0] = name;
			newValues[1] = ordinal;
			System.arraycopy(params.getTypeArray() , 0, newTypes , 2, params.getLength());
			System.arraycopy(params.getValueArray(), 0, newValues, 2, params.getLength());
			fullSig = Parameters.ofArrays(newTypes, newValues);
		}
		
		return QuickReflectionUtils.Constructors.constructObjectUnchecked(clazz, fullSig);
	}
	
}

