package net.osomahe.esk.eventstore.entity;

/**
 * Exception when publishing of an event failed.
 *
 * @author Antonin Stoklasek
 */
public class EventNotPublishedException extends RuntimeException {

    private final EventStoreEvent event;

    public EventNotPublishedException(EventStoreEvent event) {
        this.event = event;
    }

    public EventStoreEvent getEvent() {
        return event;
    }
}
