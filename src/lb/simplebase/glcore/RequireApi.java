package lb.simplebase.glcore;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This class or method requires an external API that is not part of the JRE standard library.
 */
@Retention(CLASS)
@Target({ TYPE, METHOD })
public @interface RequireApi {

	/**
	 * A descriptive name of the api, which may contain a version number.
	 * @return The name of the api
	 */
	String value();
	
}
