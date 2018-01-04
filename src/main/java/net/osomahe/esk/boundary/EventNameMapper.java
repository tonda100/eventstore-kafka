package net.osomahe.esk.boundary;

import java.util.Optional;

import net.osomahe.esk.entity.AbstractEvent;


/**
 * @author Antonin Stoklasek
 */
public interface EventNameMapper {

    Optional<Class<? extends AbstractEvent>> getClassForName(String eventName);
}
