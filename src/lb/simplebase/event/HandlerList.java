package lb.simplebase.event;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

//Package visibility
class HandlerList implements Iterable<EventHandlerImpl>{

	private final Set<EventHandlerImpl> handlers;
	
	
	protected HandlerList(Set<EventHandlerImpl> set) {
		if(!set.isEmpty()) set.clear();
		handlers = set;
	}
	
	@Override
	public Iterator<EventHandlerImpl> iterator() {
		return handlers.iterator();
	}

	public boolean registerHandler(EventHandlerImpl handler) {
		return handlers.add(handler);
	}
	
	public boolean isEmpty() {
		return handlers.isEmpty();
	}
	
	public static HandlerList createNaturalOrdered() {
		return new HandlerList(new TreeSet<>());
	}
	
	public static HandlerList createInsertionOrdered() {
		return new HandlerList(new LinkedHashSet<>());
	}
	
	public static HandlerList createUnordered() {
		return new HandlerList(new HashSet<>());
	}
}
