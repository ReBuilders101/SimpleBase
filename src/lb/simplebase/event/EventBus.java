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

public class EventBus {
	
	//TODO: A WeakHashMap could be used, but that could lead to ConcurrentModificationExceptions
	private final Map<WeakReference<Class<? extends Event>>, Set<EventHandlerImpl>> handlersMap;
	private boolean isActive;
	
	protected final ThreadLocal<Boolean> isHandlingEvents;	//Check for each thread separately
	
	protected EventBus() {
		handlersMap = new HashMap<>();
		isActive = true;
		isHandlingEvents = ThreadLocal.withInitial(() -> false);
	}
	
	public static EventBus create() {
		return new EventBus();
	}
	
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
	
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType) {
		return register(handler, eventType, EventPriority.DEFAULT, false);
	}
	
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType, final boolean receiveCancelled) {
		return register(handler, eventType, EventPriority.DEFAULT, receiveCancelled);
	}
	
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType, final AbstractEventPriority priority) {
		return register(handler, eventType, priority, false);
	}
	
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType, final AbstractEventPriority priority, final boolean receiveCancelled) {
		if(handler == null) return false;	//Handler can't be null (obv)
		if(isHandlerThread()) return false; //If handlers can register new handlers, this would lead to a concurrent modification exception (this is the same thread, so synchronized doesn't prevent that
		final EventHandlerImpl eventHandler = EventHandlerFunctional.create(handler, eventType, priority, receiveCancelled);
		if(eventHandler == null) return false;
		return registerHandler(eventHandler);
	}
	
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
	
	private synchronized boolean registerHandler(final EventHandlerImpl handler) {//Sync only needed here, the other methods don't use the map
		final WeakReference<Class<? extends Event>> key = getKey(handler.getEventType(), true, TreeSet::new); //Get the key, or create it if necessary
		if(key == null) return false;
		Set<EventHandlerImpl> handlerSet = handlersMap.get(key);
		if(handlerSet == null) return false;
		return handlerSet.add(handler);//Add the handler to the set. add methods checks contains() before adding.
		 //Don't add a handler twice: contains() uses equals, so the same reflected method can not be added twice
	}
	
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
	
	private boolean isHandlerThread() {
		return isHandlingEvents.get();
	}
	
	public void setActive(final boolean active) {
		isActive = active;
	}
	
	public boolean isActive() {
		return isActive;
	}
}
