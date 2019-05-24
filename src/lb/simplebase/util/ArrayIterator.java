package lb.simplebase.util;

import java.util.Iterator;
import java.util.stream.IntStream;

public class ArrayIterator<T> implements Iterator<T>{

	private T[] data;
	private int nextIndex;
	
	@SafeVarargs
	public ArrayIterator(T...data) {
		this.data = data;
		nextIndex = 0;
	}
	
	@Override
	public boolean hasNext() {
		return nextIndex < data.length;
	}

	@Override
	public T next() {
		return data[nextIndex++];
	}

	
	public static ArrayIterator<Integer> ofInts(int...data) {
		return new ArrayIterator<>(IntStream.of(data).boxed().toArray(Integer[]::new));
	}
}
