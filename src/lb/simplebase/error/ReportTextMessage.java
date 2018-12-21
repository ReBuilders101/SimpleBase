package lb.simplebase.error;

import java.io.PrintStream;
import java.util.stream.Stream;

/**
 * A text message that can appear in an {@link ErrorReport}. It contains only text, and no throwables or objects.
 * It is often used for additional details in the report's <i>information</i> section.
 */
public class ReportTextMessage implements ErrorReportElement{

	String normal;
	String detail;
	boolean addMode;
	
	/**
	 * Creates a new {@link ReportTextMessage}
	 * @param normal The text that should appear when detail mode is not enabled
	 * @param detail The text that should appear when detail mode is enabled
	 * @param addMode If <code>true</code>, both noremal and detail text will be dispalyed in detail mode.
	 * Otherwise, only the normal text will appear in normal mode and only the detail text will appear in detail mode
	 * @see #isInAddMode()
	 * @see ErrorReport#isDetailedReport()
	 */
	public ReportTextMessage(String normal, String detail, boolean addMode) {
		this.normal = normal;
		this.detail = detail;
		this.addMode = addMode;
	}

	/**
	 * Prints this message to the {@link PrintStream}.<br>
	 * The output ends with a blank line.
	 * @param stream The {@link PrintStream} that the element should be printed to.
	 * @param addDetails If enabled, the output should have more details and more explainations for the errors.
	 * @see #isInAddMode()
	 */
	@Override
	public void printTo(PrintStream stream, boolean addDetails) {
		if(addDetails) {
			if(addMode) stream.println(normal); //In add mode, print both normal and detail
			stream.println(detail); //if not, only detail
		} else {
			stream.println(normal); //without thw addDetails flag, only print normal
		}
		stream.println();
	}

	/**
	 * Creates a <i>flat</i> {@link Stream} of all messages nested within this container. The returned stream is <i>flat</i>,
	 * which means that it may not contain any nested structures.<br>
	 * The stream is still open, that means no terminal operation has been called for this stream yet. This method
	 * also returns a new stream instance for every new call.<br>
	 * The returned stream only contains one element, <code>this</code>.
	 * @return A stream of all elements
	 * @see Stream#flatMap(java.util.function.Function)
	 */
	@Override
	public Stream<ErrorReportElement> streamElementsFlat() {
		return Stream.of(this);
	}
	
	/**
	 * If <code>true</code>, both noremal and detail text will be dispalyed in detail mode.
	 * Otherwise, only the normal text will appear in normal mode and only the detail text will appear in detail mode
	 * @return If this message is in add mode
	 * @see ErrorReport#isDetailedReport()
	 */
	public boolean isInAddMode() {
		return addMode;
	}

}
