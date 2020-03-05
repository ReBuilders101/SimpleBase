package lb.simplebase.dynamicdata;

public abstract class AbstractDataTypeNode<T, F extends DataFormat> implements DataTypeNode<T> {

	private final Class<T> typeClass;
	private final F dataFormat;
	
	protected AbstractDataTypeNode(F dataFormat, Class<T> typeClass) {
		this.dataFormat = dataFormat;
		this.typeClass = typeClass;
	}
	
	protected abstract T decodeImpl(BaseData data, F fomat);
	protected abstract Data encodeImpl(T data, F format);
	
	@Override
	public T decode(BaseData data) {
		return decodeImpl(data, dataFormat);
	}

	@Override
	public Data encode(T data) {
		return encodeImpl(data, dataFormat);
	}

	@Override
	public F getDataFormat() {
		return dataFormat;
	}

	@Override
	public Class<T> getDataType() {
		return typeClass;
	}

}
