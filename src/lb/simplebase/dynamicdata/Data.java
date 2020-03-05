package lb.simplebase.dynamicdata;

public interface Data extends BaseData {

	public default byte getByteValue() {
		return getContent(DefaultTypes.byteNode(getFormat())).byteValue();
	}
	
	public default short getShortValue() {
		return getContent(DefaultTypes.shortNode(getFormat())).shortValue();
	}
	
	public default char getCharValue() {
		return getContent(DefaultTypes.charNode(getFormat())).charValue();
	}
	
	public default int getIntValue() {
		return getContent(DefaultTypes.intNode(getFormat())).intValue();
	}
	
	public default long getLongValue() {
		return getContent(DefaultTypes.longNode(getFormat())).longValue();
	}
	
	public default float getFloatValue() {
		return getContent(DefaultTypes.floatNode(getFormat())).floatValue();
	}
	
	public default double getDoubleValue() {
		return getContent(DefaultTypes.doubleNode(getFormat())).doubleValue();
	}
	
	public default String getStringValue() {
		return getContent(DefaultTypes.stringNode(getFormat()));
	}
	
	public default DataMap getMap() {
		return getContent(DefaultTypes.mapNode(getFormat()));
	}
	
	public default DataList getList() {
		return getContent(DefaultTypes.listNode(getFormat()));
	}
	
	public default void setByteValue(byte value) {
		setContent(DefaultTypes.byteNode(getFormat()), value);
	}
	
	public default void setShortValue(short value) {
		setContent(DefaultTypes.shortNode(getFormat()), value);
	}
	
	public default void setCharValue(char value) {
		setContent(DefaultTypes.charNode(getFormat()), value);
	}
	
	public default void setIntValue(int value) {
		setContent(DefaultTypes.intNode(getFormat()), value);
	}
	
	public default void setLongValue(long value) {
		setContent(DefaultTypes.longNode(getFormat()), value);
	}
	
	public default void setFloatValue(float value) {
		setContent(DefaultTypes.floatNode(getFormat()), value);
	}
	
	public default void setDoubleValue(double value) {
		setContent(DefaultTypes.doubleNode(getFormat()), value);
	}
	
	public default void setStringValue(String value) {
		setContent(DefaultTypes.stringNode(getFormat()), value);
	}
	
	public default DataMap setNewMap() {
		setContent(DefaultTypes.mapNode(getFormat()), getFormat().createMap());
		return getContent(DefaultTypes.mapNode(getFormat()));
	}
	
	public default DataList setNewList() {
		setContent(DefaultTypes.listNode(getFormat()), getFormat().createArray());
		return getContent(DefaultTypes.listNode(getFormat()));
	}
	
	public default boolean isType(DataTypeNode<?> testType) {
		return DataTypeNode.isCompatible(getCurrentType(), testType);
	}
	
}
