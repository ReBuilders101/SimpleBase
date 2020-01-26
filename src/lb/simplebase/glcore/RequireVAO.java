package lb.simplebase.glcore;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lb.simplebase.glcore.oop.VertexArray;

/**
 * These methods should only be called while a {@link VertexArray} is cuurently bound/enabled
 */
@Retention(CLASS)
@Target( {METHOD, CONSTRUCTOR} )
public @interface RequireVAO {

}
