package lb.simplebase.util;

import java.util.Iterator;

public class PeekIterator<T> implements Iterator<T>{
	
	private Iterator<T> delegate;
	private T peeked;
	
	public PeekIterator(Iterator<T> delegate) {
		this.delegate = delegate;
	}
	
	public PeekIterator(Iterable<T> source) {
		this.delegate = source.iterator();
	}
	
	@Override
	public boolean hasNext() {
		return peeked != null || delegate.hasNext();
	}
	
	@Override
	public T next() {
		if(peeked != null) {
			T temp = peeked;
			peeked = null;
			return temp;
		} else {
			return delegate.next();
		}
	}
	
	public T peek() {
		peeked = delegate.next();
		return peeked;
	}
	
}
