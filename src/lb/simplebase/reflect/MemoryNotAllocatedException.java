package lb.simplebase.reflect;

public class MemoryNotAllocatedException extends RuntimeException{

	private static final long serialVersionUID = 7428856765309235613L;
	
	private AllocatedMemory memory;
	
	public MemoryNotAllocatedException(String message, AllocatedMemory memory) {
		super(message);
		this.memory = memory;
	}
	
	public AllocatedMemory getMemory() {
		return memory;
	}
	
}
