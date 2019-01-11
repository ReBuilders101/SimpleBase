package lb.simplebase.reflect;

/**
 * This exception is thrown when a memory region is accessed through an {@link AllocatedMemory} instance
 * that is no longer allocated.
 */
public class MemoryNotAllocatedException extends RuntimeException{

	private static final long serialVersionUID = 7428856765309235613L;
	
	private AllocatedMemory memory;
	
	/**
	 * Creates a new {@link MemoryNotAllocatedException} with a message and an instance of {@link AllocatedMemory}.
	 * @param message The error message
	 * @param memory The {@link AllocatedMemory} instance where the error occurred
	 */
	public MemoryNotAllocatedException(String message, AllocatedMemory memory) {
		super(message);
		this.memory = memory;
	}
	
	/**
	 * Returns The {@link AllocatedMemory} where the error occurred.
	 * @return The {@link AllocatedMemory} where the error occurred
	 */
	public AllocatedMemory getMemory() {
		return memory;
	}
	
}
