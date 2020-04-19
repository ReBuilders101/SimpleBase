package lb.simplebase.gl;

public class GlThreadException extends RuntimeException {
	private static final long serialVersionUID = -4169635330798187113L;

	private final Thread current;

	public GlThreadException(String message) {
		super(message);
		this.current = Thread.currentThread();
	}
	
	public Thread getCallerThread() {
		return current;
	}
	
	public Thread getGlThread() {
		return GlUtils.getMainThread();
	}
	
}
