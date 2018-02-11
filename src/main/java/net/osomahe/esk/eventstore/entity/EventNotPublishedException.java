package net.osomahe.esk.eventstore.entity;

/**
 * Exception when publishing of an event failed.
 *
 * @author Antonin Stoklasek
 */
public class EventNotPublishedException extends RuntimeException {

    private final AbstractEvent event;

    public EventNotPublishedException(AbstractEvent event) {
        this.event = event;
    }

    public AbstractEvent getEvent() {
        return event;
    }
}
