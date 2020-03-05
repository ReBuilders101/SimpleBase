package lb.simplebase.dynamicdata;

public abstract class StaticData implements Data {

	private final DataFormat format;
	private final DataTypeNode<?> type;
	
	protected StaticData(DataFormat format, DataTypeNode<?> node) {
		this.format = format;
		this.type = node;
	}
	
	protected abstract <T> T getContentChecked(DataTypeNode<T> node);
	protected abstract <T> void setContentChecked(DataTypeNode<T> node, T content);
	
	@Override
	public DataFormat getFormat() {
		return format;
	}

	@Override
	public <T> void setContent(DataTypeNode<T> node, T content) {
		if(DataTypeNode.isCompatible(getCurrentType(), node)) {
			setContentChecked(node, content);
		} else {
			throw new IllegalArgumentException("Provided and contained Data types don't match");
		}
	}

	@Override
	public <T> T getContent(DataTypeNode<T> node) {
		if(hasContent()) {
			if(DataTypeNode.isCompatible(getCurrentType(), node)) {
				return getContentChecked(node);
			} else {
				throw new IllegalArgumentException("Requested and contained Data types don't match");
			}
		} else {
			throw new IllegalStateException("Cannot get content of an empty data object");
		}
	}

	@Override
	public DataTypeNode<?> getCurrentType() {
		return type;
	}

	@Override
	public final boolean isDynamicType() {
		return false;
	}

	@Override
	public abstract boolean hasContent();

}
