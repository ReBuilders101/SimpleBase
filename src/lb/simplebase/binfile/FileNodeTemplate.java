package lb.simplebase.binfile;

import java.util.function.BiConsumer;
import java.util.function.Function;

import lb.simplebase.net.ReadableByteData;
import lb.simplebase.net.WriteableByteData;

/**
 * The {@link FileNodeTemplate} contains rules for parsing and writing objects from and to a file
 * @param <T> The object type that will be parsed
 */
public abstract class FileNodeTemplate<T> {
	
	private Class<T> clazz;
	
	protected FileNodeTemplate(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public Class<T> getInstanceClass() {
		return clazz;
	}
	
	public abstract T parseElement(ReadableByteData data);
	public abstract void writeElement(WriteableByteData data, T element);

	public static <T> FileNodeTemplate<T> createFromDelegates(Function<ReadableByteData, T> parser, BiConsumer<WriteableByteData, T> writer, Class<T> clazz) {
		return new DelegateFileNodeTemplate<>(parser, writer, clazz);
	}
	
	private static class DelegateFileNodeTemplate<T> extends FileNodeTemplate<T>{

		private Function<ReadableByteData, T> parser;
		private BiConsumer<WriteableByteData, T> writer;
		
		public DelegateFileNodeTemplate(Function<ReadableByteData, T> parser, BiConsumer<WriteableByteData, T> writer, Class<T> clazz) {
			super(clazz);
			this.parser = parser;
			this.writer = writer;
		}
		
		@Override
		public T parseElement(ReadableByteData data) {
			return parser.apply(data);
		}

		@Override
		public void writeElement(WriteableByteData data, T element) {
			writer.accept(data, element);
		}
		
	}
	
}
