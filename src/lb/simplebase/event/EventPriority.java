package lb.simplebase.event;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import lb.simplebase.reflect.EnumUtils;
import lb.simplebase.reflect.Parameters;

/**
 * Priority with which an event handler will be called.
 * Handlers with higher priority will be called earlier.
 */
public enum EventPriority {
	LOWEST(-20), LOW(-10), DEFAULT(0), HIGH(10), HIGHEST(20);
	
	/**
	 * A {@link Comparator} that can be used to compare tow priorities. It sorts them low to high.
	 */
	public static final Comparator<EventPriority> COMPARATOR = new Comparator<EventPriority>() {

		@Override
		public int compare(EventPriority var1, EventPriority var2) {
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
	
	//Dynamically generate new elements if required
	
	private static AtomicInteger ordinal = new AtomicInteger(5);
	
	public static EventPriority createCustom(final int ranking) {
		return EnumUtils.getInstance(EventPriority.class,
				"Custom-Priority-" + ordinal + "-Rank-" + ranking,
				ordinal.getAndIncrement(), Parameters.of(int.class, ranking));
	}

}
