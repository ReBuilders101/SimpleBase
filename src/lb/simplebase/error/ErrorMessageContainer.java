package lb.simplebase.error;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This object stores a group of {@link ErrorMessage}s or other {@link ErrorMessageContainer}s with its own header and an optional
 * description of this section of the error report
 */
public class ErrorMessageContainer implements ErrorReportElement{

	private String containerName;
	private String description;
	private List<ErrorReportElement> subElements;
	
	/**
	 * Creates a new {@link ErrorMessageContainer} with the specified name and an empty description.
	 * The container initially contains no elements.
	 * @param name The name of the section
	 */
	public ErrorMessageContainer(String name) {
		this(name, null);
	}
	
	/**
	 * Creates a new {@link ErrorMessageContainer} with the specified name and description.
	 * The container initially contains no elements.
	 * @param name The name of the section
	 * @param description The description of the section
	 */
	public ErrorMessageContainer(String name, String description) {
		this.containerName = name;
		this.description = description;
		this.subElements = new ArrayList<>();
	}
	
	/**
	 * Returns the name of this {@link ErrorMessageContainer} or error report section.
	 * The name will be printed in a short header in front of the contained error messages.
	 * @return The section's name
	 */
	public String getName() {
		return containerName;
	}
	
	/**
	 * Returns the description of this {@link ErrorMessageContainer} or error report section.
	 * The name will be printed in a short header in front of the contained error messages only if detailed display is enabled.
	 * @return The section's description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Adds a new {@link ErrorReportElement} to this container.
	 * @param element The {@link ErrorReportElement} to add
	 */
	public void addElement(ErrorReportElement element) {
		subElements.add(element);
	}
	
	/**
	 * Prints the description of this container/section and all contained messages.
	 * @param stream The {@link PrintStream} that the element should be printed to.
	 * @param addDetails If enabled, the output should have more details and more explainations for the errors.
	 */
	@Override
	public void printTo(PrintStream stream, boolean addDetails) {
		//print header
		stream.println("== Start error report section ==");
		stream.println("Section name: " + containerName);
		if(description != null && !description.isEmpty())
			stream.println("Section description: " + description);
		stream.println("Errors in this section:");
		stream.println();
		subElements.forEach((e) -> e.printTo(stream, addDetails));
		stream.println("== End error report section ==");
		stream.println();
	}

	/**
	 * Creates a <i>flat</i> {@link Stream} of all messages nested within this container. The returned stream must be <i>flat</i>,
	 * which means that it may not contain any nested structures.<br>
	 * The stream must still be open, that means no terminal operation may have been called for this stream yet. This method
	 * must also return a new stream instance for every new call.
	 * @return A stream of all elements
	 * @see Stream#flatMap(java.util.function.Function)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Stream<ErrorReportElement> streamElementsFlat() {
		return subElements.stream().flatMap((e) -> e.streamElementsFlat());
	}
}
