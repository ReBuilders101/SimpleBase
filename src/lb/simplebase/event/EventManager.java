package lb.simplebase.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EventManager {

	private static final ExecutorService asyncFire = Executors.newSingleThreadExecutor();

	private Map<Class<?>, Event<?>> events;
	
	public int registerClass(Class<?> handlers) {
		int count = 0;
		for(Method method: handlers.getDeclaredMethods()) { //Test all methods
			if(method.isAnnotationPresent(EventHandler.class) && Modifier.isStatic(method.getModifiers())) { //Has the event handler annotation?, Is it static?
				if(method.getParameterCount() == 1) { //1 Parameter, the event type param
					Class<?> currentParam = method.getParameterTypes()[0]; //Since parameter count is 1, [0] must exist
					Event<?> event = events.get(currentParam);
					if(event != null) {
						method.setAccessible(true); //In case it is not public for some reason
						event.getHandlersModifiable().add((t) -> {
							try {
								method.invoke(null, t);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
							}
						});
						count++;
					}
				}
			}
		}
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public <T> boolean registerHandler(Class<T> eventClass, Consumer<T> handler) {
		Event<T> event = (Event<T>) events.get(eventClass); //Safe cast, type parameter of key and value are always the same
		if(event == null) return false;
		return event.getHandlersModifiable().add(handler);
	}
	
	public <T> boolean registerEvent(Class<T> eventClass) {
		if(events.containsKey(eventClass)) return false;
		Event<T> event = new Event<>(eventClass, new HashSet<>());
		events.put(eventClass, event);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public <T> boolean fireEvent(Class<T> eventClass, T data) {
		Event<T> event = (Event<T>) events.get(eventClass); //Safe cast, type parameter of key and value are always the same
		if(event == null) return false;
		event.fire(data);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public <T> boolean fireEventAsync(Class<T> eventClass, T data) {
		Event<T> event = (Event<T>) events.get(eventClass); //Safe cast, type parameter of key and value are always the same
		if(event == null) return false;
		asyncFire.submit(() -> {
			event.fire(data);
		});
		return true;
	}
	
}
