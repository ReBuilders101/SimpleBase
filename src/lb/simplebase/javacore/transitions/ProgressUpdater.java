package lb.simplebase.javacore.transitions;

import lb.simplebase.javacore.Framework;

public abstract class ProgressUpdater {

	public abstract double updateProgress(double previous, boolean reverse);
	public void reset() {};
	
	public static ProgressUpdater fixedIncrement(final double increment) {
		return new ProgressUpdater() {
			private final double inc = increment;
			@Override
			public double updateProgress(double previous, boolean reverse) {
				return previous + (reverse ? -inc : inc);
			}
		};
	}
	
	public static ProgressUpdater fixedCount(final double amount) {
		return new ProgressUpdater() {
			private final double inc = 1.0D / amount;
			@Override
			public double updateProgress(double previous, boolean reverse) {
				return previous + (reverse ? -inc : inc);
			}
		};
	}
	
	public static ProgressUpdater systemTime(final long totalTimeMs) {
		return new ProgressUpdater() {
			private final double timeScale = 1.0D / ((double) totalTimeMs);
			private double resetTime;
			@Override
			public double updateProgress(double previous, boolean reverse) {
				return reverse 
				? (totalTimeMs - ((double) (Framework.getLastSystemTime() - resetTime) % totalTimeMs)) * timeScale
				: ((double) (Framework.getLastSystemTime() - resetTime) % totalTimeMs) * timeScale;
			}
			@Override
			public void reset() {
				resetTime = Framework.getLastSystemTime();
			}
		};
	}
	
}
