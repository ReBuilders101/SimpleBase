package lb.simplebase.glcore.model;

public class ModelFormatException extends Exception {
	private static final long serialVersionUID = -2928026524286600924L;

	private final ModelBuilder builder;
	
	@Deprecated
	public ModelFormatException(String string, Throwable throwable) {
		super(string, throwable);
		this.builder = null;
	}

	@Deprecated
	public ModelFormatException(String string) {
		super(string);
		this.builder = null;
	}
	
	public ModelFormatException(String string, Throwable throwable, ModelBuilder builder) {
		super(string, throwable);
		this.builder = builder;
	}

	public ModelFormatException(String string, ModelBuilder builder) {
		super(string);
		this.builder = builder;
	}

	public ModelBuilder getFailedBuilder() {
		return builder;
	}
	
}
