package lb.simplebase.dynamicdata;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface DataList extends Iterable<Data>{

	public int getSize();
	
	public Data getElement(int index);
	
	public void setElement(int index, Data data);
	
	public void appendElement(Data data);
	
	public void sliceLeft(int amount);
	
	public void sliceRight(int amount);
	
	public default boolean isEmpty() {
		return getSize() == 0;
	}
	
	public default Stream<Data> streamElements() {
		return StreamSupport.stream(spliterator(), false);
	}
	
}
