package lb.simplebase.error;

import java.io.PrintStream;
import java.util.stream.Stream;

/**
 * An interface representing an element or section of an {@link ErrorReport}. This can be either
 * a single {@link ErrorMessage} or a {@link ErrorMessageContainer} containing multiple messages.
 */
public interface ErrorReportElement {

	/**
	 * Prints this element, or in case of containers, all subelements to the {@link PrintStream}.<br>
	 * The output should end with a blank line.
	 * @param stream The {@link PrintStream} that the element should be printed to.
	 * @param addDetails If enabled, the output should have more details and more explainations for the errors.
	 */
	public void printTo(PrintStream stream, boolean addDetails);
	
	/**
	 * Creates a <i>flat</i> {@link Stream} of all messages nested within this container. The returned stream must be <i>flat</i>,
	 * which means that it may not contain any nested structures.<br>
	 * The stream must still be open, that means no terminal operation may have been called for this stream yet. This method
	 * must also return a new stream instance for every new call.
	 * @return A stream of all elements
	 * @see Stream#flatMap(java.util.function.Function)
	 * @deprecated This method might not be useful (enough).
	 */
	@Deprecated
	public Stream<ErrorReportElement> streamElementsFlat();
}
