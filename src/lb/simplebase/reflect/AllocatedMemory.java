package lb.simplebase.reflect;

public class AllocatedMemory {
	
	private long address;
	private long byteSize;
	private boolean isAllocated;
	
	protected AllocatedMemory(long address, long byteSize) {
		this.address = address;
		this.byteSize = byteSize;
		this.isAllocated = true;
	}
	
	public long getByteSize() {
		return byteSize;
	}
	
	public long getAddress() {
		return address;
	}
	
	public byte getByteAtOffset(long offset) {
		if(isOffsetValid(offset)) {
			return UnsafeUtils.getUnsafe().getByte(address + offset);
		} else {
			throw new IndexOutOfBoundsException("Byte offset " + offset + " (Memory size: " + byteSize + ")");
		}
	}
	
	public void setByteAt(long offset, byte value) {
		if(isOffsetValid(offset)) {
			UnsafeUtils.getUnsafe().putByte(address + offset, value);
		} else {
			throw new IndexOutOfBoundsException("Byte offset " + offset + " (Memory size: " + byteSize + ")");
		}
	}
	
	public boolean isAddressInRange(long testAddress) {
		if(testAddress < address) return false;
		if(testAddress >= address + byteSize) return false;
		return true;
	}
	
	public boolean isOffsetValid(long offset) {
		if(offset < 0) return false;
		if(offset >= byteSize) return false;
		return true;
	}
	
	public void freeMemory() {
		if(isAllocated)
			UnsafeUtils.getUnsafe().freeMemory(address);
		isAllocated = false;
	}
	
	public boolean isAllocated() {
		return isAllocated;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		freeMemory();
	}
	
}
