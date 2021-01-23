package seng202.teamsix.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for labelling which fields and methods can be used by DataQuery.
 * If a value is not specified then DataQuery will default to the field/method name.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryField {
    String value() default "";
}
