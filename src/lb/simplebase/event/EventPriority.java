package lb.simplebase.event;

import java.util.Comparator;
import java.util.SortedSet;

/**
 * Priority with which an event handler will be called.
 * Handlers with higher priority will be called earlier.
 */
public enum EventPriority implements AbstractEventPriority{
	LOWEST(-20), LOW(-10), DEFAULT(0), HIGH(10), HIGHEST(20);
	
	/**
	 * A {@link Comparator} that can be used to compare tow priorities. It sorts them low to high.
	 */
	public static final Comparator<AbstractEventPriority> COMPARATOR = new Comparator<AbstractEventPriority>() {

		@Override
		public int compare(AbstractEventPriority var1, AbstractEventPriority var2) {
			return Integer.compare(var1.getRanking(), var2.getRanking());
		}
		
	};
	
	private final int ranking;
	
	private EventPriority(final int ranking) {
		this.ranking = ranking;
	}
	
	/**
	 * Used to sort the priorities in {@link SortedSet}s.
	 * Lower priorities have lower rankings, default priority has ranking 0.
	 */
	public int getRanking() {
		return ranking;
	}

}
