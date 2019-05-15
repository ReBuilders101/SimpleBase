package lb.simplebase.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

//Package visibility
//Not generic, because there is no way for EventBus to cast the posted event to the required subtype due to type erasure
//They all implement hashcode because they will be used in hashsets / hashmaps
abstract class EventHandlerImpl implements Comparable<EventHandlerImpl>{

	private final Class<? extends Event> checkType;
	private final AbstractEventPriority priority;
	private final boolean receiveCancelled;
	
	protected EventHandlerImpl(final Class<? extends Event> checkType, final AbstractEventPriority priority, final boolean receiveCancelled) {
		this.checkType = checkType;
		this.priority = priority;
		this.receiveCancelled = receiveCancelled;
	}
	
	public void checkAndPostEvent(final Event instance) {
		if(instance == null) return;
		if(instance.getClass() != checkType) return;
		postEventImpl(instance);
	}
	
	public Class<? extends Event> getEventType() {
		return checkType;
	}
	
	public boolean canReceiveCancelledEvents() {
		return receiveCancelled;
	}
	
	public AbstractEventPriority getPriority() {
		return priority;
	}
	


	@Override
	public int compareTo(EventHandlerImpl var1) {
		return EventPriority.COMPARATOR.compare(this.getPriority(), var1.getPriority());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((checkType == null) ? 0 : checkType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventHandlerImpl other = (EventHandlerImpl) obj;
		if (checkType == null) {
			if (other.checkType != null)
				return false;
		} else if (!checkType.equals(other.checkType))
			return false;
		return true;
	}
	
	protected abstract void postEventImpl(final Event instance);
	
	
	
	
	
	static class EventHandlerReflection extends EventHandlerImpl{

		private Method toCall;
		
		private EventHandlerReflection(final Method toCall, final Class<? extends Event> checkType, final AbstractEventPriority priority, final boolean receiveCancelled) {
			super(checkType, priority, receiveCancelled);
			this.toCall = toCall;
		}

		@Override
		protected void postEventImpl(final Event instance) {
			try {
				toCall.invoke(null, instance);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		protected static EventHandlerReflection create(final Method toCall, final Class<? extends Event> checkType, final AbstractEventPriority priority, final boolean receiveCancelled) {
			if(checkType == null || toCall == null) return null;	//Objects must not be null
			if(priority == null) return null;
			if(!toCall.isAccessible()) return null;	//If it's not accessible, don't try to force it
			return new EventHandlerReflection(toCall, checkType, priority, receiveCancelled);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((toCall == null) ? 0 : toCall.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EventHandlerReflection other = (EventHandlerReflection) obj;
			if (toCall == null) {
				if (other.toCall != null)
					return false;
			} else if (!toCall.equals(other.toCall))
				return false;
			return true;
		}
	}
	
	
	
	
	static class EventHandlerFunctional<T extends Event> extends EventHandlerImpl{

		private Consumer<T> handler;
		
		private EventHandlerFunctional(final Consumer<T> handler, final Class<? extends Event> checkType, final AbstractEventPriority priority, final boolean receiveCancelled) {
			super(checkType, priority, receiveCancelled);
			this.handler = handler;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void postEventImpl(Event instance) {
			try {
				handler.accept((T) instance);
			} catch(ClassCastException ex) {
				return;
			}
		}
		
		public static <T extends Event> EventHandlerFunctional<T> create(final Consumer<T> toCall, final Class<T> checkType, final AbstractEventPriority priority, final boolean receiveCancelled) {
			if(checkType == null || toCall == null) return null;	//Objects must not be null
			if(priority == null) return null;
			return new EventHandlerFunctional<>(toCall, checkType, priority, receiveCancelled);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((handler == null) ? 0 : handler.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			EventHandlerFunctional<?> other = (EventHandlerFunctional<?>) obj;
			if (handler == null) {
				if (other.handler != null)
					return false;
			} else if (!handler.equals(other.handler))
				return false;
			return true;
		}
	}


	
}
