package lb.simplebase.error;

/**
 * An {@link ErrorReport} contains information about errors and exceptions caught by the program. These can be either
 * handled completely, allowing the came to continue, or cause a 'controlled' crash, where the came can not continue executing,
 * but is closed without an uncaught {@link RuntimeException}.
 * @see ErrorReport#isCrashReport()
 */
public class ErrorReport{
	
	private ErrorMessageContainer errors;
	private ErrorMessageContainer additionalInfos;
	private boolean detailed;
	private boolean crash;
	
	/**
	 * Creates a new, empty {@link ErrorReport}. 
	 * @param addDetails If <code>true</code>, more detail and information is added to the error report.
	 * @param crashReport If the report is about a program crash or a handled error
	 */
	public ErrorReport(boolean addDetails, boolean crashReport) {
		this.detailed = addDetails;
		this.crash = crashReport;
		this.errors          = new ErrorMessageContainer("All encountered Errors", "All errors contained in this report");
		this.additionalInfos = new ErrorMessageContainer("Additional Information", "Contains more details about the consequences of the "
				+ (crashReport ? "crash" : "errors"));
	}
	
	/**
	 * Private constructor. Used by {@link #createCrashReport()} and {@link #createWithDetails()} and {@link #createWithoutDetails()}
	 */
	private ErrorReport(boolean addDetails, boolean changeToCrash, ErrorReport original) {
		this.detailed = addDetails;
		this.crash = changeToCrash ? true : original.crash;
		this.errors = original.errors;
		this.additionalInfos = original.additionalInfos;
	}
	
	/**
	 * If this report is a crash report, this means that the program cannot contiue and exits after printing / writing the report<br>
	 * This flag only changes some words and phrases in the output, it does not affect the behavior in any way.
	 * @return If this is a crash report
	 */
	public boolean isCrashReport() {
		return crash;
	}
	
	/**
	 * If true, the report will contain more details and explainations, but will also be longer.
	 * @return If this report has additional details
	 */
	public boolean isDetailedReport() {
		return detailed;
	}
	
	/**
	 * Adds an {@link ErrorReportElement} to the <i>errors</i> section of the report.
	 * @param error The {@link ErrorReportElement} to add
	 * @see #addInformation(ErrorReportElement)
	 */
	public void addError(ErrorReportElement error) {
		errors.addElement(error);
	}
	
	/**
	 * Adds an {@link ErrorReportElement} to the <i>information</i> section of the report.
	 * @param error The {@link ErrorReportElement} to add
	 * @see #addError(ErrorReportElement)
	 */
	public void addInformation(ErrorReportElement error) {
		additionalInfos.addElement(error);
	}
	
	/**
	 * Creates an {@link ErrorReport} instance with the {@link #isCrashReport()} flag set to true.<br>
	 * The new report is backed by this report, so all changes made to this report will also appear in the
	 * newly created report.<br>
	 * if this report already has the {@link #isCrashReport()} flag set to true, this method will return <code>this</code>.
	 * @return A crash report for this error report
	 */
	public ErrorReport createCrashReport() {
		return new ErrorReport(detailed, true, this);
	}
	
	/**
	 * Creates an {@link ErrorReport} instance with the {@link #isDetailedReport()} flag set to true.<br>
	 * The new report is backed by this report, so all changes made to this report will also appear in the
	 * newly created report.<br>
	 * if this report already has the {@link #isDetailedReport()} flag set to true, this method will return <code>this</code>.
	 * @return A detailed report for this error report
	 */
	public ErrorReport createWithDetails() {
		if(detailed) {
			return this;
		} else {
			return new ErrorReport(true, false, this);
		}
	}
	
	/**
	 * Creates an {@link ErrorReport} instance with the {@link #isDetailedReport()} flag set to false.<br>
	 * The new report is backed by this report, so all changes made to this report will also appear in the
	 * newly created report.<br>
	 * if this report already has the {@link #isDetailedReport()} flag set to false, this method will return <code>this</code>.
	 * @return A not detailed report for this error report
	 */
	public ErrorReport createWithoutDetails() {
		if(detailed) {
			return new ErrorReport(false, false, this);
		} else {
			return this;
		}
	}
	
}
