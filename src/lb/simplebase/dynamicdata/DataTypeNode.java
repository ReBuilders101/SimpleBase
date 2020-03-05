package lb.simplebase.dynamicdata;

public interface DataTypeNode<T> {

	public T decode(BaseData data);
	
	public Data encode(T data);
	
	public DataFormat getDataFormat();
	
	public Class<T> getDataType();
	
	public default boolean isUndefinedType() {
		return isUndefinedType(this);
	}
	
	/**
	 * To be compatible, both nodes must not be {@code null} and
	 * have the same Data format instance and the same type class.
	 * @param node1
	 * @param node2
	 * @return
	 */
	public static boolean isCompatible(DataTypeNode<?> node1, DataTypeNode<?> node2) {
		if(node1 == null || node2 == null) return false;
		if(node1 == node2) return true;
		if(node1.getDataFormat() == node2.getDataFormat() && node1.getDataType() == node2.getDataType()) return true;
		return false;
	}
	
	public static boolean isUndefinedType(DataTypeNode<?> node) {
		return node == DefaultTypes.UNDEFINED;
	}
	
}
