package lb.simplebase.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public abstract class OutputChannel {

	public abstract void appendMessage(LogMessage message);

	private boolean printRaw;
	
	protected OutputChannel(boolean printRaw) {
		this.printRaw = printRaw;
	}
	
	protected boolean printRaw() {
		return printRaw;
	}
	
	private static final LogLevel DEFAULT_ERROR_LEVEL = LogLevel.ERROR;
	private static final boolean DEFAULT_RAW_OUTPUT = false;
	private static final OutputChannel DEFAULT_CHANNEL = new SplitStreamOutputChannel(System.out, System.err, DEFAULT_ERROR_LEVEL, DEFAULT_RAW_OUTPUT);
	
	public static OutputChannel getDefault() {
		return DEFAULT_CHANNEL;
	}
	
	public static OutputChannel createStreamOutputChannel(OutputStream stream) {
		return new StreamOutputChannel(stream, DEFAULT_RAW_OUTPUT);
	}
	
	public static OutputChannel createStreamOutputChannel(OutputStream stream, boolean rawOutput) {
		return new StreamOutputChannel(stream, rawOutput);
	}
	
	public static OutputChannel createSplitStreamOutputChannel(OutputStream outStream, OutputStream errStream) {
		return new SplitStreamOutputChannel(outStream, errStream, DEFAULT_ERROR_LEVEL, DEFAULT_RAW_OUTPUT);
	}
	
	public static OutputChannel createSplitStreamOutputChannel(OutputStream outStream, OutputStream errStream, LogLevel errorLevel) {
		return new SplitStreamOutputChannel(outStream, errStream, errorLevel, DEFAULT_RAW_OUTPUT);
	}
	
	public static OutputChannel createSplitStreamOutputChannel(OutputStream outStream, OutputStream errStream, boolean raw) {
		return new SplitStreamOutputChannel(outStream, errStream, DEFAULT_ERROR_LEVEL, raw);
	}
	
	public static OutputChannel createSplitStreamOutputChannel(OutputStream outStream, OutputStream errStream, LogLevel errorLevel, boolean raw) {
		return new SplitStreamOutputChannel(outStream, errStream, errorLevel, raw);
	}
	
	@Deprecated
	public static OutputChannel createFileOutputChannel(File file) {
		return new FileOutputChannel(file, DEFAULT_RAW_OUTPUT);
	}
	
	@Deprecated
	public static OutputChannel createFileOutputChannel(File file, boolean rawOutput) {
		return new FileOutputChannel(file, rawOutput);
	}
	
	//////////////IMPLEMENTATIONS////////////////////////////////////
	
	private static class StreamOutputChannel extends OutputChannel{

		private PrintStream stream;
		
		public StreamOutputChannel(OutputStream stream, boolean raw) {
			super(raw);
			this.stream = new PrintStream(stream);
		}
		
		@Override
		public void appendMessage(LogMessage message) {
			stream.println(printRaw() ? message.getRawMessage() : message.getFormattedMessage());
		}
		
	}
	
	private static class SplitStreamOutputChannel extends OutputChannel{

		private PrintStream outStream;
		private PrintStream errStream;
		private LogLevel errorLevel;

		public SplitStreamOutputChannel(OutputStream outStream, OutputStream errStream, LogLevel errorLevel, boolean raw) {
			super(raw);
			this.outStream = new PrintStream(outStream);
			this.errStream = new PrintStream(errStream);
			this.errorLevel = errorLevel;
		}

		@Override
		public void appendMessage(LogMessage message) {
			if(message.getLogLevel().isHigherOrEqual(errorLevel)) {
				errStream.println(printRaw() ? message.getRawMessage() : message.getFormattedMessage());
			} else {
				outStream.println(printRaw() ? message.getRawMessage() : message.getFormattedMessage());
			}
		}
	}
	
	private static class FileOutputChannel extends OutputChannel{
		
		private File file;
		
		protected FileOutputChannel(File file, boolean printRaw) {
			super(printRaw);	
			this.file = file;
		}

		@Override
		public void appendMessage(LogMessage message) {
			try(FileOutputStream fos = new FileOutputStream(file, true)) {
				PrintStream ps = new PrintStream(fos);
				ps.println(printRaw() ? message.getRawMessage() : message.getFormattedMessage());
			} catch (IOException e) {
				LogHelper.getDefaultLogger().error("IOException while appending log message in FileOutputChannel", e);
			}
		}
		
		
		
	}
	
}

