package lb.simplebase.reflect;

import sun.misc.Unsafe;

/**
 * Describes a memory region that was allocated to this program using the {@link Unsafe#allocateMemory(long)} method.
 * An instance of this class can be obtained using the {@link UnsafeUtils#allocateMemory(long)} method. This
 * class provides additional methods to write to bytes and free the memory.
 * <p>
 * <b>To avoid memory leaks, {@link #freeMemory()} must be called on every instance before the program terminates.</b>
 */
@RequireUndocumented("sun.misc.Unsafe")
public class AllocatedMemory {
	
	private long address;
	private long byteSize;
	private boolean isAllocated;
	
	/**
	 * Creates a new instance. Not accessible. Use {@link UnsafeUtils#allocateMemory(long)}.
	 * @param address The base address
	 * @param byteSize The size in bytes / max offset
	 */
	protected AllocatedMemory(long address, long byteSize) {
		this.address = address;
		this.byteSize = byteSize;
		this.isAllocated = true;
	}
	
	/**
	 * Returns the size of the allocated memory region in bytes.
	 * @return The size of the memory region
	 */
	public long getByteSize() {
		return byteSize;
	}
	
	/**
	 * Returns the base address of this memory region.
	 * @return The address for this memory region
	 */
	public long getAddress() {
		return address;
	}
	
	/**
	 * Returns the byte at the address that is the base address of this memory region plus the offset.
	 * @param offset The offset from the base address
	 * @throws IndexOutOfBoundsException If the offset is not valid
	 * @throws MemoryNotAllocatedException If the memory is not allocated anymore, because {@link #freeMemory()} has been called
	 * @return The byte at this offset
	 * @see #isOffsetValid(long)
	 */
	public byte getByteAtOffset(long offset) {
		if(!isAllocated) throw new MemoryNotAllocatedException("Memory is not allocated anymore", this);
		if(isOffsetValid(offset)) {
			return UnsafeUtils.getUnsafe().getByte(address + offset);
		} else {
			throw new IndexOutOfBoundsException("Byte offset " + offset + " (Memory size: " + byteSize + ")");
		}
	}
	
	/**
	 * Sets the byte at the address that is the base address of this memory region plus the offset to the new value.
	 * @param offset The offset from the base address
	 * @param value the new value for this byte
	 * @throws IndexOutOfBoundsException If the offset is not valid
	 * @throws MemoryNotAllocatedException If the memory is not allocated anymore, because {@link #freeMemory()} has been called
	 * @see #isOffsetValid(long)
	 */
	public void setByteAt(long offset, byte value) {
		if(!isAllocated) throw new MemoryNotAllocatedException("Memory is not allocated anymore", this);
		if(isOffsetValid(offset)) {
			UnsafeUtils.getUnsafe().putByte(address + offset, value);
		} else {
			throw new IndexOutOfBoundsException("Byte offset " + offset + " (Memory size: " + byteSize + ")");
		}
	}
	
	/**
	 * Tests whether an address is in this allocated memory region.
	 * @param testAddress The address to test
	 * @return Whether the address is in this allocated memory region
	 */
	public boolean isAddressInRange(long testAddress) {
		if(testAddress < address) return false;
		if(testAddress >= address + byteSize) return false;
		return true;
	}
	
	/**
	 * Tests whether an offset is in this allocated memory region.
	 * @param offset The offset to test
	 * @return Whether the offset is in this allocated memory region.
	 */
	public boolean isOffsetValid(long offset) {
		if(offset < 0) return false;
		if(offset >= byteSize) return false;
		return true;
	}
	
	/**
	 * Frees the allocated memory. This {@link AllocatedMemory} will not be useable anymore after calling this method.
	 * Attempts to read / write will result in an {@link MemoryNotAllocatedException}.
	 * <p>
	 * <b>This method must be called on every instance before the program terminates, to avoid memory leaks.</b><br>
	 * It will be called by the <code>finalize()</code> method and the garbage collector, but it is not guaranteed to run,
	 * so it should be run manually.
	 */
	public void freeMemory() {
		if(isAllocated)
			UnsafeUtils.getUnsafe().freeMemory(address);
		isAllocated = false;
	}
	
	/**
	 * Returns whether the memory is still allocated. If it is not, read / write operations will throw an {@link MemoryNotAllocatedException}.
	 * @return Whether the memory is still allocated
	 */
	public boolean isAllocated() {
		return isAllocated;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		freeMemory();
	}
	
}
