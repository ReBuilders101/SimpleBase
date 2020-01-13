package lb.simplebase.javacore.transitions;

@FunctionalInterface
public interface ProgressUpdater {

	public double updateProgress(double previous, boolean reverse);
	
	public static ProgressUpdater fixedIncrement(final double increment) {
		return (prev, rev) -> prev + (rev ? -increment : increment);
	}
	
	public static ProgressUpdater fixedCount(final double amount) {
		final double perTick = 1.0D / amount;
		return (prev, rev) -> prev + (rev ? -perTick : perTick);
	}
	
	@Deprecated
	public static ProgressUpdater systemTime(final long totalTimeMs) {
		final long startTime = System.currentTimeMillis();
		final double timeScale = 1.0D / ((double) totalTimeMs);
		return (prev, rev) -> rev 
				? (totalTimeMs - ((double) (System.currentTimeMillis() - startTime) % totalTimeMs)) * timeScale
				: ((double) (System.currentTimeMillis() - startTime) % totalTimeMs) * timeScale;
	}
	
}
