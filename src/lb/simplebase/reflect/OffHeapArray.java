package lb.simplebase.reflect;

import lb.simplebase.glcore.RequireUndocumented;

/**
 * Represents an array that can contain any primitive type. The array can have a maximum size of {@link Long#MAX_VALUE}.<br>
 * The array exists not on the heap memory, but in a specially allocated memory region. This region will not be checked
 * by the garbage collector, and using this may be faster for extremely large arrays.
 * <p>
 * <b>To avoid memory leaks, {@link #freeMemory()} must be called on every instance before the program terminates.</b>
 * <p>
 * Implementation mainly taken from <a href="https://www.baeldung.com/java-unsafe">https://www.baeldung.com/java-unsafe</a>
 * @param <T> The element type
 */
@RequireUndocumented("sun.misc.Unsafe")
public class OffHeapArray<T> extends AllocatedMemory{

	private FixedSizeObject<T> fixed;
	private long elementsize;
	
	/**
	 * Called from {@link UnsafeUtils#createOffHeapArray(long, FixedSizeObject)}.
	 * @param pointer The memory pointer
	 * @param bytesize The BYTE size
	 * @param elementsize The number of elements
	 * @param fixed The {@link FixedSizeObject} that describes the type
	 */
	protected OffHeapArray(long pointer, long bytesize, long elementsize, FixedSizeObject<T> fixed) {
		super(pointer, bytesize);
		assert bytesize == elementsize * fixed.getSize();
		this.elementsize = elementsize;
		this.fixed = fixed;
	}

	/**
	 * Returns a value stored in the array at this index
	 * @param index The index of the element
	 * @throws MemoryNotAllocatedException When the memory is not allocated anymore, because {@link #freeMemory()} has been called
	 * @throws ArrayIndexOutOfBoundsException When the index is not valid for this array. The index can be checked using {@link #isIndexValid(long)}
	 * @return The stored value
	 */
	public T get(long index) {
		if(isIndexValid(index)) {
			if(isAllocated()) {
				return fixed.getReadFunction().apply(UnsafeUtils.getUnsafe(), getPosition(index));
			} else {
				throw new MemoryNotAllocatedException("Memory is not allocated anymore", this);
			}
		} else {
			throw new ArrayIndexOutOfBoundsException("Array index out of range: " + index + " (Array size: " + elementsize + ")");
		}
	}
	
	/**
	 * Sets an index of the array to a new index
	 * @param index The index for the new value
	 * @param value The new value
	 * @throws MemoryNotAllocatedException When the memory is not allocated anymore, because {@link #freeMemory()} has been called
	 * @throws ArrayIndexOutOfBoundsException When the index is not valid for this array. The index can be checked using {@link #isIndexValid(long)}
	 */
	public void set(long index, T value) {
		if(isIndexValid(index)) {
			if(isAllocated()) {
				fixed.getWriteFunction().accept(UnsafeUtils.getUnsafe(), getPosition(index), value);
			} else {
				throw new MemoryNotAllocatedException("Memory is not allocated anymore", this);
			}
		} else {
			throw new ArrayIndexOutOfBoundsException("Array index out of range: " + index + " (Array size: " + elementsize + ")");
		}
	}
	
	/**
	 * The size of the array, which is equal to the amount of elements.
	 * @return The size of the array
	 */
	public long getSize() {
		return elementsize;
	}
	
	/**
	 * Tests whether this index is valid to use in {@link #get(long)} and {@link #set(long, Object)}.
	 * @param index The index to test
	 * @return Whether the index is valid
	 */
	public boolean isIndexValid(long index) {
		if(index < 0) return false;
		if(index >= elementsize) return false;
		return true;
	}
	
	private long getPosition(long index) {
		return (index * fixed.getSize()) + getAddress();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		freeMemory();
	}
	
}
