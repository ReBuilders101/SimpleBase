package lb.simplebase.binfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lb.simplebase.net.Packet;

/**
 * Represents a section of a binary data file
 * @param <T> The type of data that is stored in this section
 */
public abstract class FileNode<T extends Packet> {
	
	protected FileNode(String name, Class<T> type) {
		nodeName = name;
		typeClass = type;
		items = new ArrayList<>();
		dataParsing = new Future<Collection<T>>() { //Dummy class

			@Override
			public boolean cancel(boolean paramBoolean) {
				return false;
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public Collection<T> get() throws InterruptedException, ExecutionException {
				return items;
			}

			@Override
			public Collection<T> get(long paramLong, TimeUnit paramTimeUnit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return items;
			}
		};
	}
	
	private final String nodeName; //Name of the node
	private ExecutorService asyncParser; //The async parsing service. Only one async operation at the same time
	private Future<Collection<T>> dataParsing; //Result of parsing function
	private final Class<T> typeClass; //Type of T
	private Collection<T> items; //Parsed items, never access directly
	
	public final Future<Collection<T>> getNodeElements() {
		return dataParsing;
	}
	
	protected final void parseAll(byte[] data) {
		if(isParsing()) { //Wait for a running parsing operation to complete
			dataParsing.cancel(true); //Attempt to cancel
		}
		if(asyncParser == null) { //If no asyncParser is present
			asyncParser = Executors.newSingleThreadExecutor(); //Create a new instance
		}
		//If no async operation is running:
		items.clear(); //Clear old parsed values
		dataParsing = asyncParser.submit(() -> { //Submit the new task
			parseFill(data, items);
			return items; //Items list will be future result
		});
	}
	
	protected abstract void parseFill(byte[] data, Collection<T> toFill);
	
	public final String getName() {
		return nodeName;
	}
	
	public final boolean isParsing() {
		return !dataParsing.isDone() && !dataParsing.isCancelled();
	}
	
	public final Class<T> getTypeClass() {
		return typeClass;
	}
	
	public final void addElement(T element) {
		items.add(element);
	}
	
	public static interface Factory {
		public <V extends Packet> FileNode<V> create(String name, Class<V> type);
	}
	
	public abstract byte[] createData();
}
