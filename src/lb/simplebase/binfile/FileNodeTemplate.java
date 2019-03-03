package lb.simplebase.binfile;

import java.util.function.BiConsumer;
import java.util.function.Function;

import lb.simplebase.net.ReadableByteData;
import lb.simplebase.net.WritableByteData;

/**
 * The {@link FileNodeTemplate} contains rules for parsing and writing objects from and to a file
 * @param <T> The object type that will be parsed
 */
public abstract class FileNodeTemplate<T> {
	
	private Class<T> clazz;
	private String name;
	
	protected FileNodeTemplate(Class<T> clazz, String name) {
		this.clazz = clazz;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<T> getInstanceClass() {
		return clazz;
	}
	
	public abstract T parseElement(ReadableByteData data);
	public abstract void writeElement(WritableByteData data, T element);

	@SuppressWarnings("unchecked")
	public void writeElementUnchecked(ByteArrayWriter data, Object element) {
		writeElement(data, (T) element);
	}
	
	public static <T> FileNodeTemplate<T> createFromDelegates(Function<ReadableByteData, T> parser, BiConsumer<WritableByteData, T> writer, Class<T> clazz, String name) {
		return new DelegateFileNodeTemplate<>(parser, writer, clazz, name);
	}
	
	private static class DelegateFileNodeTemplate<T> extends FileNodeTemplate<T>{

		private Function<ReadableByteData, T> parser;
		private BiConsumer<WritableByteData, T> writer;
		
		public DelegateFileNodeTemplate(Function<ReadableByteData, T> parser, BiConsumer<WritableByteData, T> writer, Class<T> clazz, String name) {
			super(clazz, name);
			this.parser = parser;
			this.writer = writer;
		}
		
		@Override
		public T parseElement(ReadableByteData data) {
			return parser.apply(data);
		}

		@Override
		public void writeElement(WritableByteData data, T element) {
			writer.accept(data, element);
		}
		
	}
}
