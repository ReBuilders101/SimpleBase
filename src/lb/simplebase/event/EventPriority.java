package lb.simplebase.event;

import java.util.Comparator;

public enum EventPriority implements AbstractEventPriority{
	LOWEST(-20), LOW(-10), DEFAULT(0), HIGH(10), HIGHEST(20);
	
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
	
	public int getRanking() {
		return ranking;
	}

}
