package lb.simplebase.dynamicdata;

import java.util.function.Function;

public interface BaseData {

	public DataFormat getFormat();
	
	public <T> void setContent(DataTypeNode<T> node, T content);
	
	public <T> T getContent(DataTypeNode<T> node);
	
	public DataTypeNode<?> getCurrentType();
	
	public boolean isDynamicType();
	
	public boolean hasContent();
	
	public default <T> T getContent(Function<DataFormat, DataTypeNode<T>> nodeGetter) {
		return getContent(nodeGetter.apply(getFormat()));
	}
	
	public default <T> void setContent(Function<DataFormat, DataTypeNode<T>> nodeGetter, T content) {
		setContent(nodeGetter.apply(getFormat()), content);
	}
}
