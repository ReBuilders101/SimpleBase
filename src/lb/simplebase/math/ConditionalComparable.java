package lb.simplebase.math;

public interface ConditionalComparable<T, E extends Enum<E>> extends Comparable<T>{

	public E getDefaultCompareMethod();
	public int compareTo(T object, E method);
	
	@Override
	public default int compareTo(T object) {
		return compareTo(object, getDefaultCompareMethod());
	}
	
}
