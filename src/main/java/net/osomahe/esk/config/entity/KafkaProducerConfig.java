package net.osomahe.esk.config.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;


/**
 * Qualifier annotation to provide custom properties for the event store producer configuration.
 * The default values will be replaced by the provided values.
 *
 * @author Antonin Stoklasek
 */
@Documented
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface KafkaProducerConfig {

}
