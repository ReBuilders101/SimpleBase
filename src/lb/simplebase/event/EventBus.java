package lb.simplebase.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

import lb.simplebase.event.EventHandlerImpl.EventHandlerFunctional;
import lb.simplebase.event.EventHandlerImpl.EventHandlerReflection;

/**
 * An EventBus is an object where events can be posted by the application, and all listeners registered
 * for that event on this EventBus will be invoked.
 * <p>
 * This basic implementation invokes all registered handlers on the same thread that the event was posted on.
 * For a concurrent implementation, see {@link AsyncEventBus}.
 */
public class EventBus {
	
	//TODO: A WeakHashMap could be used, but that could lead to ConcurrentModificationExceptions
	private final Map<WeakReference<Class<? extends Event>>, Set<EventHandlerImpl>> handlersMap;
	private boolean isActive;
	
	protected final ThreadLocal<Boolean> isHandlingEvents;	//Check for each thread separately
	
	/**
	 * Protected constructor. Use {@link #create()} to create a new instance.
	 */
	protected EventBus() {
		handlersMap = new HashMap<>();
		isActive = true;
		isHandlingEvents = ThreadLocal.withInitial(() -> false);
	}
	
	/**
	 * Creates a new EventBus without any registered listeners.
	 * @return The new EventBus
	 */
	public static EventBus create() {
		return new EventBus();
	}
	
	/**
	 * Registers one or more event handlers that are methods in the class.
	 * Methods that should be registered as event handlers must
	 * <ul>
	 * <li>be public</li>
	 * <li>be static</li>
	 * <li>not have a return value (<code>void</code>)</li>
	 * <li>not throw any checked exceptions (<code>throws</code>)</li>
	 * <li>accept a single parameter, which extends {@link Event}</li>
	 * <li>have the {@link EventHandler} annotation</li>
	 * </ul>
	 * The type of event that a method handles is determined by its single parameter's type.
	 * @param handlerContainer The class that contains the Event handlers
	 * @return The number of event handlers that were successfully registered
	 */
	public int register(final Class<?> handlerContainer) {
		//returns the amount of registered methods
		if(handlerContainer == null) return 0;
		if(isHandlerThread()) return 0;
		final Method[] declared;
		try {
			 declared = handlerContainer.getDeclaredMethods();
		} catch (SecurityException e) {	
			return 0;	//Can't regsiter when we don't have access to the methods
		}
		int num = 0; //Number of methods successfully regsitered
		for(Method method : declared) {
			if(registerMethod(method)) num++;//Increase only if successful. All checks are made withing regsiterMethod
		}
		return num;//Number of regsitered methods
	}
	
	/**
	 * Registers one event handler. The handler will be called with default priority
	 * and will not be called for canceled events.
	 * @param handler The task that should be executed when the event is posted
	 * @param eventType The type of event that the handler is for
	 * @return Whether the handler was registered successfully
	 */
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType) {
		return register(handler, eventType, EventPriority.DEFAULT, false);
	}
	
	/**
	 * Registers one event handler. The handler will be called with default priority.
	 * @param handler The task that should be executed when the event is posted
	 * @param eventType The type of event that the handler is for
	 * @param receiveCancelled Whether the handler should be called for events that have been canceled
	 * @return Whether the handler was registered successfully
	 */
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType, final boolean receiveCanceled) {
		return register(handler, eventType, EventPriority.DEFAULT, receiveCanceled);
	}
	
	/**
	 * Registers one event handler. The handler will not be called for canceled events.
	 * @param handler The task that should be executed when the event is posted
	 * @param eventType The type of event that the handler is for
	 * @param priority An {@link EventPriority} that determines when this handler will be called
	 * @return Whether the handler was registered successfully
	 */
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType, final AbstractEventPriority priority) {
		return register(handler, eventType, priority, false);
	}
	
	/**
	 * Registers one event handler.
	 * @param handler The task that should be executed when the event is posted
	 * @param eventType The type of event that the handler is for
	 * @param priority An {@link EventPriority} that determines when this handler will be called
	 * @param receiveCanceled Whether the handler should be called for events that have been canceled
	 * @return Whether the handler was registered successfully
	 */
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType, final AbstractEventPriority priority, final boolean receiveCanceled) {
		if(handler == null) return false;	//Handler can't be null (obv)
		if(isHandlerThread()) return false; //If handlers can register new handlers, this would lead to a concurrent modification exception (this is the same thread, so synchronized doesn't prevent that
		final EventHandlerImpl eventHandler = EventHandlerFunctional.create(handler, eventType, priority, receiveCanceled);
		if(eventHandler == null) return false;
		return registerHandler(eventHandler);
	}
	
	/**
	 * Posts an event to this {@link EventBus}. The event instance will be passed to all handlers for
	 * this event type. The handlers will be called in order of their priority, meaning that e.g. a handler with the
	 * <code>HIGH</code> priority will be called before a handler with <code>DEFAULT</code> priority.
	 * Handlers of the same priority are not guaranteed to be called in a fixed order.
	 * <p>
	 * If {@link #isSynchronous()} returns <code>true</code>, all handlers will have returned when this method returns.
	 * @param event The event to be posted
	 * @return Whether the event was handled by at least one event handler
	 * @see #isSynchronous()
	 */
	//TODO can we just sync the whole method and get rid of the map / set syncs?
	public synchronized boolean post(final Event event) {
		if(event == null) return false;
		if(!isActive()) return false;
		if(isHandlerThread()) return false;	//Can't post an event from an event handler (at least for single-thread busses) 
		final Class<? extends Event> eventClass = event.getClass();
		//Find the key. We cant use e.getClass() as a key because we need the exact WeakReference instance in the map / key set
		final Set<EventHandlerImpl> handlerSet;
//		synchronized (handlersMap) {//Make sure the handlers map is not changed while traversing keys and finding the handlers set for this event
			WeakReference<Class<? extends Event>> key = getKey(eventClass, false, null);
			if(key == null) return false;
			//Next, get all Handlers
			handlerSet = handlersMap.get(key);
//		}//Syncronisation not needed anymore. We now have a local reference to the handler list.
		if(handlerSet == null) return false; //Just to be safe
		if(handlerSet.isEmpty()) return false;
//		synchronized (handlerSet) { //Because HashSet's iterator is fail-fast, we have to prevent concurrent modification here too
			try {	//Protection against bad sync (should not be necessary)
				for(EventHandlerImpl handler : handlerSet) { //Now iterate over the handlers
					if(handler == null) continue; //HashSet allows a null value
					postEvent(handler, event);	//This is in a separate method so we can have an async implemetation in a subclass
				}
			} catch(ConcurrentModificationException ex) {
				return true; //It is not really a success, but false would indicate that no handlers have been called, which may also be not correct
			}
//		}//End sync on set, iterator is done
		return true;
	}
	
	//Registers a single method as handler through reflection. Used by register(Class<?>)
	private boolean registerMethod(final Method method) {
		//Validate method
		if(method == null) return false;
		if(!Modifier.isStatic(method.getModifiers())) return false; //Method must be static
		if(!Modifier.isPublic(method.getModifiers())) return false; //And also public
//		if(!method.isAccessible()) return false;	//--Must be accessible to work-- IsAccessible refers to the flag, not general accessibility
		if(!method.isAnnotationPresent(EventHandler.class)) return false; //Method must have EventHandler annotation
		if(method.getExceptionTypes().length != 0) return false; //The method must not throw checked exceptions
		if(method.getReturnType() != void.class) return false; //The method must return void
		//Check params for event type
		final Class<?>[] params = method.getParameterTypes();
		if(params.length != 1) return false;	//There must be exactly one type parameter
		final Class<?> param = params[0]; //This is the only parameter, which should be the event type
		if(param == null) return false;	//Make sure it's not null
		if(!Event.class.isAssignableFrom(param)) return false;	//If param is not a subclass of Event, abort
		//Now read annotation content for craete params
		EventHandler handlerAnno = method.getDeclaredAnnotation(EventHandler.class);
		if(handlerAnno == null) return false;
		@SuppressWarnings("unchecked")	//Checks have been made above
		final EventHandlerImpl eventHandler = EventHandlerReflection.create(method, (Class<? extends Event>) param, handlerAnno.priority(), handlerAnno.receiveCancelled());
		if(eventHandler == null) return false;
		return registerHandler(eventHandler);
	}
	
	//Registers an EventHandler of any implementation. Does interaction with the map and is therefor synchronized. Used by all register() methods
	private synchronized boolean registerHandler(final EventHandlerImpl handler) {//Sync only needed here, the other methods don't use the map
		final WeakReference<Class<? extends Event>> key = getKey(handler.getEventType(), true, TreeSet::new); //Get the key, or create it if necessary
		if(key == null) return false;
		Set<EventHandlerImpl> handlerSet = handlersMap.get(key);
		if(handlerSet == null) return false;
		return handlerSet.add(handler);//Add the handler to the set. add methods checks contains() before adding.
		 //Don't add a handler twice: contains() uses equals, so the same reflected method can not be added twice
	}
	
	//Gets the WeakReference instance that is used as the key for a class type. //TODO Remove when HashMap is replaced ny WeakHashMap
	private WeakReference<Class<? extends Event>> getKey(final Class<? extends Event> type, final boolean mayCreateKey, final Supplier<Set<EventHandlerImpl>> newSet) {
		WeakReference<Class<? extends Event>> key = null;
		for(WeakReference<Class<? extends Event>> ref : handlersMap.keySet()) {
			Class<? extends Event> weakValue = ref.get();
			if(weakValue == null) { //If reference is cleared, remove it from the map
				handlersMap.remove(ref); //If a WeakReference is unloaded, it cannot come back. This means that the key (and value) can be removed
				continue;
			}
			if(weakValue == type) { //Class.equals() also uses ==, also a class can only be loaded once
				key = ref; //The key is found
				break;
			}
		}
		if(key == null && mayCreateKey && newSet != null) { //If no key was found, but we can make one, and we have a way of creating sets
			key = new WeakReference<>(type);	//Create a new Weak Reference to the type, to allow the class to unload. The newly created weakReferece is also the returned value
			final Set<EventHandlerImpl> value = newSet.get(); //Then make a new set (there should be no keys with null values)
			handlersMap.put(key, value); //And then add it to the map
		}
		return key;
	}
	
	//Overridable for concurrent implementation: post on another thread
	protected void postEvent(final EventHandlerImpl handler, final Event event) {
		try {
			isHandlingEvents.set(true);//Moved this here so it can be overridden to set in different threads
			handler.checkAndPostEvent(event);
		} finally {
			isHandlingEvents.set(false); //Event handling is done, either throung normal code path or through exception, so make sure it is reset
		}
	}
	
	//If true, this thread is currently executing an event handler any may not post events / register handlers on this bus
	private boolean isHandlerThread() {
		return isHandlingEvents.get();
	}
	
	/**
	 * If <code>false</code>, the current thread cannot post events or register handlers for this event bus, because
	 * it is currently executing an event handler. The return value is different for every thread.
	 * @return Whether the calling thread can currently use this event bus
	 */
	public boolean canInteract() {
		return !isHandlerThread();
	}
	
	/**
	 * Sets the <i>active</i> state for this event bus. If an  event bus is inactive,
	 * all calls to post() will immediately return false, and no event will be posted.<br>
	 * New handlers can be registered on an inactive bus.
	 * @param active The new state value
	 */
	public void setActive(final boolean active) {
		isActive = active;
	}
	
	/**
	 * Gets the <i>active</i> state for this event bus. If an  event bus is inactive,
	 * all calls to post() will immediately return false, and no event will be posted.<br>
	 * New handlers can be registered on an inactive bus.
	 * @return Whether this event bus is active
	 */
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * If <code>true</code>, all event handlers will be invoked on the same thread that the call to {@link #post(Event)}
	 * occurred on. In that case, all event handlers will have run and finished when the {@link #post(Event)}
	 * method returns.<br>
	 * If <code>false</code>, handlers may still be running on other threads when the {@link #post(Event)}
	 * method returns.
	 * @return Whether this event bus implementation is executing handlers on the same thread as the post call
	 */
	public boolean isSynchronous() {
		return true;
	}
}
