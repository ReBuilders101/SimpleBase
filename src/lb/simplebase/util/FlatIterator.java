package lb.simplebase.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


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
	
	public static <T> Iterator<T> createFlatOfIterator(Iterator<Iterator<T>> iters) {
		return createFlatOfIterator(() -> iters);
	}
	
	public static <T> Iterator<T> createFlatOfIterable(Iterator<Iterable<T>> iters) {
		return createFlatOfIterable(() -> iters);
	}
	
	public static <T> Iterator<T> createFlatOfIterator(Iterable<Iterator<T>> iters) {
		return StreamSupport.stream(iters.spliterator(), false).flatMap((i) -> streamIterator(i)).iterator(); //Good luck understanding this
	}
	
	public static <T> Iterator<T> createFlatOfIterable(Iterable<Iterable<T>> iters) {
		return StreamSupport.stream(iters.spliterator(), false).flatMap((i) -> streamIterable(i)).iterator();
	}
	
	public static <T> Iterable<T> ofIterator(Iterator<T> iterator) {
		return () -> iterator;
	}
	
	public static <T> Stream<T> streamIterator(Iterator<T> iterator) {
		return StreamSupport.stream(ofIterator(iterator).spliterator(), false);
	}
	
	public static <T> Stream<T> streamIterable(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}
}
