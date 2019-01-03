package lb.simplebase.error;

import java.io.PrintStream;
import java.util.stream.Stream;

/**
 * Saves information about an error or exception that occurred during level creation.
 * Contains a {@link Throwable} that caused this error, as well as information about the cause of
 * the error and how it was handled.
 */
public class ErrorMessage implements ErrorReportElement{
	
	private Throwable throwable;
	private String message;
	private Object payload;
	private NameDescriptionProvider reason;
	private NameDescriptionProvider status;
	
	/**
	 * Creates a new {@link ErrorMessage} that can be used in an {@link ErrorReport}.<br>
	 * <b>All parameters except the custom payload must not be <code>null</code>.</b>
	 * @param throwable The {@link Throwable} that should be represented by this message in the error report
	 * @param message A custom error message, that may be different from the throwable's message
	 * @param payload A custom object that may be printed in the crash report by its toString() method 
	 * @param reason A text component specifying the reason for this error.
	 * @param status A text component giving information about how the error was handed by the program
	 */
	public ErrorMessage(Throwable throwable, String message, Object payload, NameDescriptionProvider reason, NameDescriptionProvider status) {
		this.throwable = throwable;
		this.message = message;
		this.payload = payload;
		this.reason = reason;
		this.status = status;
	}

	/**
	 * The throwable that caused this error message to appear is accessible through this method
	 * It countains further information about the cause of this exception that is useful for debugging, for example the stacktrace.
	 * The message in {@link Throwable#getMessage()} may be different from the message in {@link ErrorMessage#getMessage()}
	 * @return The contained {@link Throwable}
	 */
	public Throwable getThrowable() {
		return throwable;
	}
	
	/**
	 * Contains an error message that can give more details about the cause of this error.
	 * This message may be different form the message in {@link Throwable#getMessage()}.
	 * @return The error message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Error messages can contain any object as payload that may give more information about the cause of the error.
	 * The type of this object is not saved and cannot be assumed by the {@link #getReason()} or {@link #getStatus()} methods.
	 * @return The custom payload object
	 */
	public Object getCustomObject() {
		return payload;
	}

	/**
	 * Contains information about the reason why this error occurred.
	 * @return The reason for this error
	 */
	public NameDescriptionProvider getReason() {
		return reason;
	}
	
	/**
	 * Contains information about how the error was handled by the program
	 * @return The status of this error
	 */
	public NameDescriptionProvider getStatus() {
		return status;
	}

	/**
	 * Prints this error message to the {@link PrintStream}.
	 * @param stream The {@link PrintStream} that the element should be printed to.
	 * @param addDetails If enabled, the output should have more details and more explainations for the errors.
	 */
	@Override
	public void printTo(PrintStream stream, boolean addDetails) {
		//message
		stream.println("== Start of error message ==");
		stream.println("Throwable causing this error: " + throwable.getClass().getName());
		if(addDetails) stream.println("-> Message of throwable: " + throwable.getMessage());
		stream.println("Error message: " + message);
		stream.println();
		if(addDetails) {
			stream.println("-> Throwable stack trace:");
			throwable.printStackTrace(stream);
		} else {
			stream.println("Head of stack trace: " + throwable.getStackTrace()[0].toString());
		}
		stream.println();
		//handling
		stream.println("Reason for this error: " + reason.getName());
		if(addDetails) stream.println("-> Description: " + reason.getDescription()); 
		stream.println("Handling status for this error: " + status.getName());
		if(addDetails) stream.println("-> Description: " + status.getDescription());
		//other
		if(payload == null) {
			stream.println("A custom object was not attached to this message");
		} else {
			stream.println("This custom object was attached to this message: ");
			stream.println("-> Object type: " + payload.getClass().getName());
			stream.println("-> Object representation: " + payload.toString());
		}
		//end
		stream.println("== End of error message ==");
		stream.println();
	}

	/**
	 * Creates a stream that only contains <code>this</code>
	 * @return A stream of this element
	 */
	@Override
	public Stream<ErrorReportElement> streamElementsFlat() {
		return Stream.of(this);
	}
	
}
