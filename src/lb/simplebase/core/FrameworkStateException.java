package lb.simplebase.core;

/**
 * @version 1.0
 * @author LB
 * This exception is thrown when the {@link Framework} object does not have the correct state
 * This, for example, happens when you try to call the {@link Framework#start()} method while it is already running
 */
public class FrameworkStateException extends Exception{
	private static final long serialVersionUID = -2451695892516027701L;

	private boolean running;
	
	/**
	 * Create a new {@link FrameworkStateException}.	
	 * @param message The error message. It should include the expected and actual value of the state
	 * @param running Whether the framework was running when the error occurred
	 */
	public FrameworkStateException(String message, boolean running){
		super(message);
		this.running = running;
	}
	
	/**
	 * Whether the {@link Framework} was running when the error occurred.
	 * The state of the 'running' flag is usually, but not necessarily the cause of the exception. 
	 * @return Whether the {@link Framework} was running when the error occurred
	 */
	public boolean wasFrameworkRunning(){
		return running;
	}
	
}
