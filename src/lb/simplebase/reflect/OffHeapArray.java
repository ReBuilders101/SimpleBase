package lb.simplebase.reflect;

/**
 * Implementation mainly taken from:<br>
 * <code>https://www.baeldung.com/java-unsafe</code>
 */
@RequireUndocumented("sun.misc.Unsafe")
public class OffHeapArray<T> extends AllocatedMemory{

	private FixedSizeObject<T> fixed;
	private long elementsize;
	
	/**
	 * 
	 * @param pointer
	 * @param bytesize The BYTE size
	 * @param elementsize The number of elements
	 * @param fixed
	 */
	protected OffHeapArray(long pointer, long bytesize, long elementsize, FixedSizeObject<T> fixed) {
		super(pointer, bytesize);
		assert bytesize == elementsize * fixed.getSize();
		this.elementsize = elementsize;
		this.fixed = fixed;
	}

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
	
	public long getSize() {
		return elementsize;
	}
	
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
