package lb.simplebase.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FlatIterator<T> implements Iterator<T>{

	private PeekIterator<Iterator<T>> iters;
	private Iterator<T> current; //null means no more
	
	public FlatIterator(Iterator<Iterator<T>> iterators) {
		iters = new PeekIterator<>(iterators);
	}
	
	public FlatIterator(Iterable<Iterator<T>> iterators) {
		iters = new PeekIterator<>(iterators);
	}
	
	@SafeVarargs
	public FlatIterator(Iterator<T>...iterators) {
		iters = new PeekIterator<>(new ArrayIterator<>(iterators));
	}
	
	@Override
	public boolean hasNext() {
		if(current == null) return false;
		return current.hasNext() || iters.peek().hasNext();
	}

	@Override
	public T next() {
		if(!hasNext()) throw new NoSuchElementException("No more elements");
		if(!current.hasNext()) current = iters.next();
		return current.next();
	}
	
	
	
}
