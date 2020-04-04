package lb.simplebase.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A list that wraps around from the last to the first element, so that {@code get(x)} is equal to
 * {@code get(x +/- getSize()}. <br> The circular list is immutable.
 * @param <E> The content type for this list
 */
public class CircularList<E> extends AbstractList<E> implements RandomAccess, Serializable {
	private static final long serialVersionUID = 8796781701598011494L;
	
	//The list that holds the elements. Always a unmodifiable view of an ArrayList (that can't be modified) -> effectively immutable
	private final List<E> data;
	
	/**
	 * Private constructor. Create instances using the static methods.
	 * @param elements The elements of this list. it is the callers responsiblity to ensure that this List will never me modified.
	 */
	private CircularList(List<E> elements) {
		//Elements is the base list for the view, the handle is discarded and the writable list is not accessible
		this.data = Collections.unmodifiableList(elements);
	}
	
	/**
	 * Creates a view of this list where all elements are shifted backwards/to the left by the {@code offset} amount.
	 * @param offset The offset by which each element will be moved
	 * @return An immutable view of this list with the element offset
	 */
	public CircularList<E> withOffset(int offset) {
		return new View<>(data, offset); //Read-Through view is ok because data is immutable
	}
	
	/**
	 * Whether this list is empty (has a size of 0).
	 * @return guess what
	 */
	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	/**
	 * Returns the element form the list. Unlike with normal lists, the index can be greater than the list size or smaller than
	 * zero. The index will be wrapped around as if the collections elements were repeating infinitely.
	 * @return The element from the list
	 */
	@Override
	public E get(int offset) {
		return data.get(fixIndex(offset));
	}

	/**
	 * The size of the list.
	 * @return The size of the list, as stated above
	 */
	@Override
	public int size() {
		return data.size();
	}
	
	/**
	 * Creates an iterator that will infinitely iterate over the list, starting back at the beginning when the end of the list
	 * would have been reached. This iterator is meant to be used as a source for repeating elements.
	 * As this list is immutable, using the {@link Iterator#remove()} method will throw an exception.
	 * @return An infinite iterator that repeats the lists elements.
	 */
	public Iterator<E> circularIterator() {
		return new CircularIterator();
	}
	
	/**
	 * A normal iterator that iterates all list elements once.
	 * @return ^^
	 */
	@Override
	public Iterator<E> iterator() {
		return data.iterator();
	}
	
	public Stream<E> infiniteStream() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(circularIterator(), Spliterator.IMMUTABLE), false);
	}
	
	@Override
	public Stream<E> stream() {
		return StreamSupport.stream(Spliterators.spliterator(data, Spliterator.IMMUTABLE), false); //Don't use data.stream() so it is immutable
	}
	
	protected int fixIndex(int index) {
		if(isEmpty()) return 0; //Nothing is valid, so don't even calc anything
		if(index >= 0) return index % data.size();
		//mod and remainder are different for negative numbers
		return data.size() + (index % data.size());
	}
	
	private static class View<E> extends CircularList<E> {
		private static final long serialVersionUID = -3430172669524153205L;
		
		private final int offset;
		
		private View(List<E> data, int offset) {
			super(data);
			this.offset = offset;
		}
		
		@Override
		protected int fixIndex(int index) {
			return super.fixIndex(index) - offset;
		}

		@Override
		public CircularList<E> withOffset(int offset) {
			return super.withOffset(offset + this.offset);
		}
		
	}
	
	private class CircularIterator implements Iterator<E> {

		private int currentIndex = 0;
		
		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public E next() {
			currentIndex = fixIndex(currentIndex); //prevent int overflow when used often
			return data.get(currentIndex++);
		}
		
	}
	
	@SafeVarargs
	public static <E> CircularList<E> create(E... data) {
		//Wrapped in arraylist because Arrays.asList reads/writes through to the (possibly mutable) array
		return new CircularList<>(data == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(data)));
	}
	
	public static <E> CircularList<E> create(Collection<E> data) {
		return new CircularList<>(data == null ? new ArrayList<>() : new ArrayList<>(data));
	}
}

