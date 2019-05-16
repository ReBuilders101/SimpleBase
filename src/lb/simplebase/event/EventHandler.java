package lb.simplebase.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a method as an event hander. To register the handler to an {@link EventBus},
 * use {@link EventBus#register(Class)} with the class containing the annotated method as a parameter.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface EventHandler {
	
	/**
	 * Whether this event handler should be called for an event that has already been canceled. <code>false</code> by default.
	 */
	boolean receiveCancelled() default false;
	/**
	 * The priority with which the handler is called. <code>DEFAULT</code> is the default priority.
	 */
	EventPriority priority() default EventPriority.DEFAULT;
	
}
