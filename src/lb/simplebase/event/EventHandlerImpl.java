package lb.simplebase.event;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Consumer;

//Package visibility
//Not generic, because there is no way for EventBus to cast the posted event to the required subtype due to type erasure
//They all implement hashcode because they will be used in hashsets / hashmaps
abstract class EventHandlerImpl implements Comparable<EventHandlerImpl>{

	private final WeakReference<Class<? extends Event>> checkType;
	private final EventPriority priority;
	private final boolean receiveCanceled;
	
	protected EventHandlerImpl(final Class<? extends Event> checkType, final EventPriority priority, final boolean receiveCanceled) {
		this.checkType = new WeakReference<>(checkType);
		this.priority = priority;
		this.receiveCanceled = receiveCanceled;
	}
	
	public void checkAndPostEvent(final Event instance, final EventBus bus, boolean mayStop) {
		if(instance == null) return;
		if(isBlocking() && !mayStop) return;	//Don't post on awaitableEventHandler when it should not block
		if(instance.getClass() != checkType.get()) return;	//If WeakReference doesn't exist anymore, it tests event != null, which will return
		if(instance.isCanceled() && !receiveCanceled) return;	//Don't process cancelled events unless requested
		postEventImpl(instance);
	}
	
	public Class<? extends Event> getEventType() {
		return checkType.get();
	}
	
	public boolean canReceiveCanceledEvents() {
		return receiveCanceled;
	}
	
	public EventPriority getPriority() {
		return priority;
	}
	
	protected abstract boolean isBlocking();

	@Override
	public int compareTo(EventHandlerImpl var1) {
		return EventPriority.COMPARATOR.compare(var1.getPriority(), this.getPriority());
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
		
		private EventHandlerReflection(final Method toCall, final Class<? extends Event> checkType, final EventPriority priority, final boolean receiveCancelled) {
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
		
		protected static EventHandlerReflection create(final Method toCall, final Class<? extends Event> checkType, final EventPriority priority, final boolean receiveCancelled) {
			if(checkType == null || toCall == null) return null;	//Objects must not be null
			if(priority == null) return null;
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

		@Override
		protected boolean isBlocking() {
			return false;
		}
	}
	
	private static final MethodType handlerType = MethodType.methodType(void.class, Event.class);
	private static final CallSite handlerCallSite = new MutableCallSite(handlerType); //Handlers are event -> void methods
	private static final MethodHandle handlerInvoker = handlerCallSite.dynamicInvoker();
	private static final Object setAndInvokeLock = new Object();
	
	static class EventHandlerInvoke extends EventHandlerImpl {
		
		private final MethodHandle handle;
		
		protected EventHandlerInvoke(final MethodHandle handle, final Class<? extends Event> checkType, final EventPriority priority,
				final boolean receiveCanceled) {
			super(checkType, priority, receiveCanceled);
			this.handle = handle.asType(handlerType);
		}

		@Override
		protected boolean isBlocking() {
			return false;
		}

		@Override
		protected void postEventImpl(Event instance) {
			try {
				//invoke through static callSite
				synchronized (setAndInvokeLock) { //Make sure we are not interrupted
					handlerCallSite.setTarget(handle); //Set the method to run through the callSite
					handlerInvoker.invokeExact(instance); 
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		protected static EventHandlerInvoke create(final MethodHandle toCall, final Class<? extends Event> checkType, final EventPriority priority, final boolean receiveCancelled) {
			if(checkType == null || toCall == null || priority == null) return null;	//Objects must not be null
			return new EventHandlerInvoke(toCall, checkType, priority, receiveCancelled);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((handle == null) ? 0 : handle.hashCode());
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
			EventHandlerInvoke other = (EventHandlerInvoke) obj;
			if (handle == null) {
				if (other.handle != null)
					return false;
			} else if (!handle.equals(other.handle))
				return false;
			return true;
		}
		
	}
	
	
	
	static class EventHandlerFunctional<T extends Event> extends EventHandlerImpl{

		private Consumer<T> handler;
		
		private EventHandlerFunctional(final Consumer<T> handler, final Class<? extends Event> checkType, final EventPriority priority, final boolean receiveCancelled) {
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
		
		public static <T extends Event> EventHandlerFunctional<T> create(final Consumer<T> toCall, final Class<T> checkType, final EventPriority priority, final boolean receiveCancelled) {
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

		@Override
		protected boolean isBlocking() {
			return false;
		}
	}


	@Deprecated
	static class EventHandlerAwaitable extends EventHandlerImpl {

		private CyclicBarrier waiter;
		private volatile boolean broken = false;
		
		protected EventHandlerAwaitable(Class<? extends Event> checkType, EventPriority priority) {
			super(checkType, priority, true);
		}

		public EventHandlerAwaitable init() {
			if(broken || waiter == null) {
				waiter = new CyclicBarrier(2);
			} else {
				waiter.reset();
			}
			return this;
		}
		
		public void breakBarrier() {
			broken = true;
		}
		
		public boolean isBroken() {
			return broken;
		}
		
		@Override
		protected void postEventImpl(Event instance) {
			
			try {
				waiter.await(); //Waits for a call to awaitPriority(); -> must wait for main thread or unlock main thread for its handler
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			} catch (BrokenBarrierException e) {
				broken = true;
				return;	//When the barrier is broken, let other handlers continue normally. It will be fixed for the next post() call
			}
			

			try {
				waiter.await(); //Waits for a call to allowCompletion(); -> must not run until allowed to complete the next handlers
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			} catch (BrokenBarrierException e) {
				broken = true;
				return;
			}	
		}
		
		public CyclicBarrier getWaiter() {
			return waiter;
		}

		@Override
		protected boolean isBlocking() {
			return true;
		}
	}
	
	
}
