package net.osomahe.esk.eventstore.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to mark event class that it supposed to be processed asynchronously.
 *
 * @author Antonin Stoklasek
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncEvent {
}
