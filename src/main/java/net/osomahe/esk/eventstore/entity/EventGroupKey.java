package net.osomahe.esk.eventstore.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Events with similar value in field annotated by this annotation will be saved in similar partition.
 *
 * @author Antonin Stoklasek
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventGroupKey {
}
