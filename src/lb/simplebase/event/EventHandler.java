package lb.simplebase.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface EventHandler {
	
	boolean receiveCancelled() default false;
	EventPriority priority() default EventPriority.DEFAULT;
	
}
