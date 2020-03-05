package lb.simplebase.dynamicdata;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lb.simplebase.dynamicdata.DataMap.DataMapEntry;

public interface DataMap extends Iterable<DataMapEntry>{

	public DataFormat getFormat();
	
	public Data getElement(String key);
	
	public void setElement(String key, Data data); // ? instead of f is necessary to use Data<?> in code

	public void hasElement(String key);
	
	public Iterable<String> keys();
	
	public Iterable<Data> values();
	
	public void clear();
	
	public default Stream<String> streamKeys() {
		return StreamSupport.stream(keys().spliterator(), false);
	}
	
	public default Stream<Data> streamValues() {
		return StreamSupport.stream(values().spliterator(), false);
	}
	
	public default Stream<DataMapEntry> streamEntries() {
		return StreamSupport.stream(spliterator(), false);
	}
	
	
	public static final class DataMapEntry {
		
		private final String key;
		private final Data data;
		
		protected DataMapEntry(String key, Data data) {
			this.key = key;
			this.data = data;
		}
		
		public String getKey() {
			return key;
		}
		
		public Data getData() {
			return data;
		}
		
	}
}
