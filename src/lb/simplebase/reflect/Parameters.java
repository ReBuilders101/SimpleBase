package lb.simplebase.reflect;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import lb.simplebase.util.Validate;

/**
 * Represents a list of method or constructor parameters that is validated at creation time.<br>
 * Can optionally contain a value paired to each parameter for method execution.
 */
public class Parameters {

	private static final Parameters EMPTY = new Parameters(null, null);
	
	private final Class<?>[] types;
	private final Object[] values;
	
	private Parameters(final Class<?>[] types, final Object[] values) {
		this.types = types;
		this.values = values;
	}

	protected boolean hasValues() {
		return types == null ? true : values != null; //If types are null, no params -> always enough values. Otherwise check values
	}
	
	protected Class<?>[] getTypeArray() {
		return types;
	}
	
	protected Object[] getValueArray() {
		return values;
	}
	
	protected int getLength() {
		return types.length;
	}
	
	/**
	 * Creates a new {@link Parameters} that represents a method or constructor with no parameters
	 * @return The empty method signature
	 */
	public static Parameters empty() {
		return EMPTY;
	}
	
	/**
	 * Creates a special {@link Parameters} object that contains only types, but no values.
	 * Can't be used to execute a method.
	 * @param types The parameter types
	 * @return The method signature without values
	 */
	public static Parameters ofTypes(final Class<?>... types) {
		if(types == null || types.length == 0) {
			return EMPTY;
		} else {
			Validate.noMatchingElements(types, VALID_PARAM, new IllegalArgumentException("Type array must not contain null elements or void.class"));
			return new Parameters(types, null);
		}
	}

	/**
	 * Creates a {@link Parameters} object with any amount of parameters that all have the value {@code null}.
	 * @param types The parameter types
	 * @return The method signature
	 */
	public static Parameters ofNull(final Class<?>... types) {
		if(types == null || types.length == 0) {
			return EMPTY;
		} else {
			final int length = types.length;
			final Class<?>[] values = new Class<?>[length];
			Arrays.fill(values, null);
			return new Parameters(types, values);
		}
	}
	
	/**
	 * Creates a {@link Parameters} from two arrays containing parameter types and values
	 * @param types The types of the parameters
	 * @param values The values of the parameters
	 * @return The method signature
	 */
	public static Parameters ofArrays(final Class<?>[] types, final Object[] values) {
		Objects.requireNonNull(types, "Type array must not be null");
		Objects.requireNonNull(values, "Value array must not be null");
		Validate.noMatchingElements(types, VALID_PARAM, new IllegalArgumentException("Type array must not contain null elements or void.class"));
		
		if(types.length == values.length) {
			for(int i = 0; i < types.length; i++) {
				checkTypeMatch(types[i], values[i], i + 1);
			}
			return new Parameters(types, values);
		} else {
			throw new IllegalArgumentException("Type array and value array must have the same length");
		}
	}
	
	/**
	 * Creates a new {@link Parameters} with four parameters of possibly different types.
	 * @param type1 The type of the first parameter
	 * @param value1 The value of the first parameter
	 * @param type2 The type of the second parameter
	 * @param value2 The value of the second parameter
	 * @param type3 The type of the third parameter
	 * @param value3 The value of the third parameter
	 * @param type4 The type of the fourth parameter
	 * @param value4 The value of the fourth parameter
	 * @return The method signature
	 * @see #of(Class, Object)
	 * @see #of(Class, Object, Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object)
	 */
	public static Parameters of(final Class<?> type1, final Object value1, final Class<?> type2, final Object value2,
			final Class<?> type3, final Object value3, final Class<?> type4, final Object value4) {
		checkTypeMatch(type1, value1, 1);
		checkTypeMatch(type2, value2, 2);
		checkTypeMatch(type3, value3, 3);
		checkTypeMatch(type4, value4, 4);
		
		return new Parameters(new Class<?>[] {
			type1, type2, type3, type4
		}, new Object[] {
			value1, value2, value3, value4
		});
	}
	
	/**
	 * Creates a new {@link Parameters} with three parameters of possibly different types.
	 * @param type1 The type of the first parameter
	 * @param value1 The value of the first parameter
	 * @param type2 The type of the second parameter
	 * @param value2 The value of the second parameter
	 * @param type3 The type of the third parameter
	 * @param value3 The value of the third parameter
	 * @return The method signature
	 * @see #of(Class, Object)
	 * @see #of(Class, Object, Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object, Class, Object)
	 */
	public static Parameters of(final Class<?> type1, final Object value1, final Class<?> type2,
			final Object value2, final Class<?> type3, final Object value3) {
		checkTypeMatch(type1, value1, 1);
		checkTypeMatch(type2, value2, 2);
		checkTypeMatch(type3, value3, 3);
		
		return new Parameters(new Class<?>[] {
			type1, type2, type3
		}, new Object[] {
			value1, value2, value3
		});
	}
	
	/**
	 * Creates a new {@link Parameters} with two parameters of possibly different types.
	 * @param <T> The type of the first parameter
	 * @param <U> The type of the second parameter
	 * @param type1 The type of the first parameter
	 * @param value1 The value of the first parameter
	 * @param type2 The type of the second parameter
	 * @param value2 The value of the second parameter
	 * @return The method signature
	 * @see #of(Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object, Class, Object)
	 */
	public static Parameters of(final Class<?> type1, final Object value1, final Class<?> type2, final Object value2) {
		checkTypeMatch(type1, value1, 1);
		checkTypeMatch(type2, value2, 2);
		
		return new Parameters(new Class<?>[] {
			type1, type2
		}, new Object[] {
			value1, value2
		});
	}
	
	/**
	 * Creates a new {@link Parameters} that contains any amount of parameters, which are all of one type.
	 * @param type The type of all values
	 * @param values Any amount of values
	 * @return The method signature
	 */
	@SafeVarargs
	public static Parameters allOf(final Class<?> type, final Object...values) {
		Objects.requireNonNull(type, "The type must not be null");
		Objects.requireNonNull(type, "The element array must not be null");
		
		final int length = values.length;
		final Class<?>[] types = new Class<?>[length];
		for(int i = 0; i < length; i++) {
			checkTypeMatch(type, values[i], i + 1); //1-based for param count
			types[i] = type;
		}
		return new Parameters(types, values);
	}
	
	/**
	 * Creates a new {@link Parameters} with one parameter of a specified type.
	 * @param type1 The type of the first parameter
	 * @param value1 The value of the first parameter
	 * @return The method signature
	 * @see #of(Class, Object, Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object)
	 * @see #of(Class, Object, Class, Object, Class, Object, Class, Object)
	 */
	public static Parameters of(final Class<?> type1, final Object value1) {
		checkTypeMatch(type1, value1, 1);
		return new Parameters(new Class<?>[] {
			type1
		}, new Object[] {
			value1
		});
	}
	
	/**
	 * Creates a new {@link Parameters} that contains any amount of parameters.
	 * The type of each parameter will be the type of the value for that parameter.
	 * <b>The type will not be the type of the declared variable type, but the type of the implementation, which can be a subclass of the decalred type</b>
	 * @param values The values of different types
	 * @return The method signature
	 */
	public static Parameters of(final Object...values) {
		final int length = values.length;
		final Class<?>[] types = new Class<?>[length];
		for(int i = 0; i < length; i++) {
			if(values[i] == null) {
				throw new IllegalArgumentException("Value array must not contain null elements: cannot determine parameter type from null value");
			}
			types[i] = values[i].getClass();
		}
		return new Parameters(types, values);
	}
	
	private static void checkTypeMatch(Class<?> type, Object value, int paramIndex) {
		if(void.class == type) throw new IllegalArgumentException("Parameters can not be of type void");
		if(!QuickReflectionUtils.isOfType(value, type)) throw new IllegalArgumentException("Parameter value " + paramIndex + " does not match the required type");
	}
	
	private static final Predicate<Class<?>> VALID_PARAM = (c) -> c == null || c == void.class;
}
