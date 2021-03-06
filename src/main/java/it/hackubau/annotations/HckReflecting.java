package it.hackubau.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Marco Guassone <hck@hackubau.it>
 * this is an helper annotation used just to print out all the possible getters of your HckReflect classes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER,ElementType.TYPE})
public @interface HckReflecting {
	public boolean reflect() default true;
	public String[] toReflect() default {};


}
