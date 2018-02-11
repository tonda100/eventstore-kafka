package net.osomahe.esk.eventstore.entity;

/**
 * TODO write JavaDoc
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
