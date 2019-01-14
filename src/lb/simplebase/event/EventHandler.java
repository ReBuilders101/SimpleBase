package lb.simplebase.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The {@link EventHandler} annotation marks a method as an event handler method. The event that the method receives
 * depends on the parameter types of the method. Annotated methods must be static.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface EventHandler {
	
}
