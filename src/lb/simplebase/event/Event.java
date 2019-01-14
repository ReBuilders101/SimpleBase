package lb.simplebase.event;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

public class Event<T> {

	private Class<T> clazz;
	private Set<Consumer<T>> hand;
	private Set<Consumer<T>> handView;
	
	public Event(Class<T> clazz, Set<Consumer<T>> hand) {
		this.clazz = clazz;
		this.hand = hand;
		this.handView = Collections.unmodifiableSet(hand);
	}
	
	public Class<T> getEventClass() {
		return clazz;
	}
	
	public Set<Consumer<T>> getHandlers() {
		return handView;
	}
	
	protected Set<Consumer<T>> getHandlersModifiable() {
		return hand;
	}
	
	public void fire(T param) {
		
	}
	
}
