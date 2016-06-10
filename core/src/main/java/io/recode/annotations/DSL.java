package io.recode.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The DSL annotation is used on classes that define domain-specific language methods. It's a hint for the code
 * generated to e.g. omit the class name and/or "wordify" camel-cased methods methods, for example:
 * <code>expect(myObject).toBe(equalTo(myOtherObject))</code> => <code>expect myObject to be equal to myOtherObject</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DSL {
}
