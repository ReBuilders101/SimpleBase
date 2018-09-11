package lb.simplebase.math;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface LimitedPrecision {
	
	Precision value();
	
	public static enum Precision {
		DOUBLE, FLOAT, LONG, INTEGER, SHORT, BYTE;
	}
	
}
