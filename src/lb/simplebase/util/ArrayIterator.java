package lb.simplebase.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
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
	
	public static <T> Iterable<T> iterable(final T[] array) {
		return () ->  new ArrayIterator<>(array);
	}
	
	public static <T> Collection<T> collection(final T[] array) {
		return new Collection<T>() {

			@Override
			public boolean add(T e) {
				throw new UnsupportedOperationException("Can't add elements to an array-wrapping collection");
			}

			@Override
			public boolean addAll(Collection<? extends T> c) {
				throw new UnsupportedOperationException("Can't add elements to an array-wrapping collection");
			}

			@Override
			public void clear() {
				throw new UnsupportedOperationException("Can't add elements to an array-wrapping collection");
			}

			@Override
			public boolean contains(Object o) {
				Objects.requireNonNull(o, "Object to find must not be null");
				for(T i : array) {
					if(o.equals(i)) return true;
				}
				return false;
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				for(Object other : c) {
					if(other == null) continue;
					if(!contains(other)) return false;
				}
				return true;
			}

			@Override
			public boolean isEmpty() {
				return array.length == 0;
			}

			@Override
			public Iterator<T> iterator() {
				return new ArrayIterator<>(array);
			}

			@Override
			public boolean remove(Object o) {
				throw new UnsupportedOperationException("Can't remove elements from an array-wrapping collection");
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException("Can't remove elements from an array-wrapping collection");
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException("Can't remove elements from an array-wrapping collection");
			}

			@Override
			public int size() {
				return array.length;
			}

			@Override
			public Object[] toArray() {
				return array;
			}

			@Override
			public <T2> T2[] toArray(T2[] a) {
				if(a.length >= array.length) {
					System.arraycopy(array, 0, a, 0, array.length);
					return a;
				} else {
					@SuppressWarnings("unchecked")
					T2[] a2 = (T2[]) Array.newInstance(a.getClass().getComponentType(), array.length);
					System.arraycopy(array, 0, a2, 0, array.length);
					return a2;
				}
			}
		};
	}
}
