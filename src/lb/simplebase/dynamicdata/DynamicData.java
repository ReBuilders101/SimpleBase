package lb.simplebase.dynamicdata;

import java.util.Objects;

import lb.simplebase.util.SilentCloseable;

public final class DynamicData implements Data {

	private Data impl;
	private final DataFormat format;

	protected DynamicData(DataFormat format) {
		this.format = format;
		this.impl = null;
	}
	
	@Override
	public DataFormat getFormat() {
		return format;
	}
	
	@Override
	public DataTypeNode<?> getCurrentType() {
		if(impl == null) {
			return DefaultTypes.UNDEFINED;
		} else {
			return impl.getCurrentType();
		}
	}

	@Override
	public boolean isDynamicType() {
		return true;
	}

	@Override
	public boolean hasContent() {
		return impl != null;
	}

	@Override
	public <T> void setContent(DataTypeNode<T> node, T content) {
		Objects.requireNonNull(node, "The content type node must not be null"); //content can be null
		if(node.isUndefinedType()) throw new IllegalArgumentException("Cannot use the DYNAMIC TypeNode as content type");
		if(!hasContent()) {
			impl = format.createStatic(node);
			impl.setContent(node, content);
		} else {
			if(DataTypeNode.isCompatible(getCurrentType(), node)) {
				impl.setContent(node, content);
			} else {
				if(impl instanceof SilentCloseable) {
					((SilentCloseable) impl).close();
				}
				impl = format.createStatic(node);
				impl.setContent(node, content);
			}
		}
	}

	@Override
	public <T> T getContent(DataTypeNode<T> node) {
		Objects.requireNonNull(node, "The content type node must not be null"); //content can be null
		if(node.isUndefinedType()) throw new IllegalArgumentException("Cannot use the DYNAMIC TypeNode as content type");
		if(hasContent()) {
			if(DataTypeNode.isCompatible(getCurrentType(), node)) {
				return impl.getContent(node);
			} else {
				throw new IllegalArgumentException("Requested and contained Data types don't match");
			}
		} else {
			throw new IllegalStateException("Cannot get content of an empty data object");
		}
	}
}
