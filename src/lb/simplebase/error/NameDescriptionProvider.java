package lb.simplebase.error;

/**
 * Objects implementing this Interface provide two methods to get information about the object.<br>
 * The first piece of information is the name of the object: A short and, if possible, unique {@link String}.<br>
 * The second piece of information is the description: It provides more details than just the name and may also
 * contain explainations.<br>
 * Both Strings can be multi-lined and are for console output, not for parsing. 
 */
public interface NameDescriptionProvider {
	
	/**
	 * The name of the object is a short and, if possible, unique {@link String}, often just a single word that describes it.
	 * @return The name of the object
	 */
	public String getName();
	/**
	 * The description of the object contains more details and explainations about the object than just the name.
	 * @return The description of the object
	 */
	public String getDescription();
	
}
