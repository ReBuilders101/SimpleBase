package lb.simplebase.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

//Package visibility
//Not generic, because there is no way for EventBus to cast the posted event to the required subtype due to type erasure
//They all implement hashcode because they will be used in hashsets / hashmaps
abstract class EventHandlerImpl {

	private Class<? extends Event> checkType;
	
	protected EventHandlerImpl(Class<? extends Event> checkType) {
		this.checkType = checkType;
	}
	
	public void checkAndPostEvent(Event instance) {
		if(instance == null) return;
		if(instance.getClass() != checkType) return;
		postEventImpl(instance);
	}
	
	public Class<? extends Event> getEventType() {
		return checkType;
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
	
	protected abstract void postEventImpl(Event instance);
	
	
	
	
	
	static class EventHandlerReflection extends EventHandlerImpl{

		private Method toCall;
		
		private EventHandlerReflection(Method toCall, Class<? extends Event> checkType) {
			super(checkType);
			this.toCall = toCall;
		}

		@Override
		protected void postEventImpl(Event instance) {
			try {
				toCall.invoke(null, instance);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		protected static EventHandlerReflection create(Method toCall, Class<? extends Event> checkType) {
			if(checkType == null || toCall == null) return null;	//Objects must not be null
			if(!Modifier.isStatic(toCall.getModifiers())) return null;//Method must be static
			if(!toCall.isAccessible()){	//Only change accessibility flag when necessary, to avoid security checks
				try {
					toCall.setAccessible(true);
				} catch(SecurityException ex) {
					return null;
				}
			}
			return new EventHandlerReflection(toCall, checkType);
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
		
		private EventHandlerFunctional(Consumer<T> handler, Class<? extends Event> checkType) {
			super(checkType);
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
		
		public static <T extends Event> EventHandlerFunctional<T> create(Consumer<T> toCall, Class<T> checkType) {
			if(checkType == null || toCall == null) return null;	//Objects must not be null
			return new EventHandlerFunctional<>(toCall, checkType);
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
