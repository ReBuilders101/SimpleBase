package lb.simplebase.event;

public interface AbstractEventPriority {
	
	public int getRanking();
	
	public static AbstractEventPriority custom(final int ranking) {
		for(EventPriority e :  EventPriority.values()) {
			if(e.getRanking() == ranking) return e;
		}
		return () -> ranking;
	}
	
}
