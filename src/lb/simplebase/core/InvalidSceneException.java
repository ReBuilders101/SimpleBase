package lb.simplebase.core;

/**
 * @version 1.0
 * @author LB
 * This exception is thrown when a scene has null as a name, has a duplicate name or a scene of this name does not exist
 */
public class InvalidSceneException extends Exception{
	private static final long serialVersionUID = 2486172148957429580L;
	
	/**
	 * Creates a new {@link InvalidSceneException}.
	 * @param message The error message
	 */
	public InvalidSceneException(String message){
		super(message);
	}
	
}
