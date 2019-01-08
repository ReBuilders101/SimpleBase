package lb.simplebase.reflect;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This class or method requires an undocumented API that may be changed or removed from the JRE/JDK in future releases
 */
@Retention(CLASS)
@Target({ TYPE, METHOD })
public @interface RequireUndocumented {
	/**
	 * The name of the reqired API. This can be a package or class name. 
	 * @return The name of the API
	 */
	String value() default "";
}
