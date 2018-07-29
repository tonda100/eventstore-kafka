package net.osomahe.esk.eventstore.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Using this annotation on {@link EventStoreEvent} class you can set how long is the event valid and should be fired.
 * This can be used for not firing events when reseting topic offset.
 *
 * @author Antonin Stoklasek
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EventExpirationSecs {

    long value();

}
