package lb.simplebase.net.done;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the element is only available on the server side of the application, and that
 * using it on client side can cause errors and crashes. This annotation has no effect on the program.
 * It is available in the compiled class code, but not at runtime.
 */
@Retention(CLASS)
@Target({ TYPE, METHOD, FIELD })
public @interface ServerSide {

}
