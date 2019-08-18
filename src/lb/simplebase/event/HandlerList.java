package lb.simplebase.event;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import lb.simplebase.event.EventHandlerImpl.EventHandlerAwaitable;

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
	
	public HandlerListAwaitable awaitable(Class<? extends Event> checkType, EventPriority priority) {
		return new HandlerListAwaitable(new EventHandlerAwaitable(checkType, priority), this);
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
	
	
	static class HandlerListAwaitable implements Iterable<EventHandlerImpl> {

		private final EventHandlerAwaitable waiter;
		private final HandlerList delegate;
		
		protected HandlerListAwaitable(EventHandlerAwaitable waiter, HandlerList delegate) {
			this.waiter = waiter;
			this.delegate = delegate;
		}
		
		//Will be uninitialized
		public EventHandlerAwaitable getWaiter() {
			return waiter;
		}
		
		public HandlerList getDelegate() {
			return delegate;
		}
		
		@Override
		public Iterator<EventHandlerImpl> iterator() {
			return new Iterator<EventHandlerImpl>() {
				
				private boolean waited = false;
				private EventHandlerImpl scheduled = null;
				private final Iterator<EventHandlerImpl> iter = delegate.iterator();
				
				@Override
				public boolean hasNext() {
					return iter.hasNext() || !waited || scheduled != null; //If iter can supply, or if we have a scheduled value, or if the waiter must still be sent
				}

				@Override
				public EventHandlerImpl next() {
					if(scheduled != null) {		//Scheduled will always be returned in the next iteration
						final EventHandlerImpl r = scheduled;
						scheduled = null;
						return r;
					}
					//If nothing was scheduled, get the next thing from the delegate
					
					EventHandlerImpl impl = iter.next();
					if(impl.getPriority().getRanking() <= waiter.getPriority().getRanking()) { //If the next one should be after / at the same time with waiter
						scheduled = impl;	//Return waiter instead and schedule the handler for the next iteration
						waited = true;
						return waiter;
					}
					return impl;	//Otherwise, return normally
				}
			};
		}


	}
}
