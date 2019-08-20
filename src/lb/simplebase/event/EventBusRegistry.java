package lb.simplebase.event;

import java.util.function.Consumer;

/**
 * Part of an event bus that allows handlers to be registered, but not events to be posted
 * @see EventBus
 */
public interface EventBusRegistry {

	/**
	 * Registers one or more event handlers that are methods in the class.
	 * Methods that should be registered as event handlers must
	 * <ul>
	 * <li>be public</li>
	 * <li>be static</li>
	 * <li>be defined in a class that is public</li>
	 * <li>not have a return value (<code>void</code>)</li>
	 * <li>not throw any checked exceptions (<code>throws</code>)</li>
	 * <li>accept a single parameter, which extends {@link Event}</li>
	 * <li>have the {@link EventHandler} annotation</li>
	 * </ul>
	 * The type of event that a method handles is determined by its single parameter's type.
	 * @param handlerContainer The class that contains the Event handlers
	 * @return The number of event handlers that were successfully registered
	 */
	public int register(final Class<?> handlerContainer);
	
	/**
	 * Registers one event handler. The handler will be called with default priority
	 * and will not be called for canceled events.
	 * @param handler The task that should be executed when the event is posted
	 * @param eventType The type of event that the handler is for
	 * @return Whether the handler was registered successfully
	 */
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType);
	
	/**
	 * Registers one event handler. The handler will be called with default priority.
	 * @param handler The task that should be executed when the event is posted
	 * @param eventType The type of event that the handler is for
	 * @param receiveCancelled Whether the handler should be called for events that have been canceled
	 * @return Whether the handler was registered successfully
	 */
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType, final boolean receiveCanceled);
	
	/**
	 * Registers one event handler. The handler will not be called for canceled events.
	 * @param handler The task that should be executed when the event is posted
	 * @param eventType The type of event that the handler is for
	 * @param priority An {@link EventPriority} that determines when this handler will be called
	 * @return Whether the handler was registered successfully
	 */
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType, final EventPriority priority);
	
	/**
	 * Registers one event handler.
	 * @param handler The task that should be executed when the event is posted
	 * @param eventType The type of event that the handler is for
	 * @param priority An {@link EventPriority} that determines when this handler will be called
	 * @param receiveCanceled Whether the handler should be called for events that have been canceled
	 * @return Whether the handler was registered successfully
	 */
	public <T extends Event> boolean register(final Consumer<T> handler, final Class<T> eventType, final EventPriority priority, final boolean receiveCanceled);
	
	/**
	 * Gets the <i>active</i> state for this event bus. If an  event bus is inactive,
	 * all calls to post() will immediately return false, and no event will be posted.<br>
	 * New handlers can be registered on an inactive bus.
	 * @return Whether this event bus is active
	 */
	public boolean isActive();
	
	/**
	 * If <code>true</code>, all event handlers will be invoked on the same thread that the call to {@link #post(Event)}
	 * occurred on. In that case, all event handlers will have run and finished when the {@link #post(Event)}
	 * method returns.<br>
	 * If <code>false</code>, handlers may still be running on other threads when the {@link #post(Event)}
	 * method returns.
	 * @return Whether this event bus implementation is executing handlers on the same thread as the post call
	 */
	public boolean isSynchronous();
	
	/**
	 * If <code>false</code>, the current thread cannot post events or register handlers for this event bus, because
	 * it is currently executing an event handler. The return value is different for every thread.
	 * @return Whether the calling thread can currently use this event bus
	 */
	public boolean canThreadInteract();
}
