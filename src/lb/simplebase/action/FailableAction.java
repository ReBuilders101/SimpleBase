package lb.simplebase.action;

public interface FailableAction {

	public Exception getError();
	
	public String getErrorMessage();
	
}
