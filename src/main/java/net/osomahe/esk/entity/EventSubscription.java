package net.osomahe.esk.entity;

/**
 * Event class used to subscribe for specific event in Kafka.
 *
 * @author Antonin Stoklasek
 */
public class EventSubscription {

    private final Class<? extends AbstractEvent> eventClass;

    public EventSubscription(Class<? extends AbstractEvent> eventClass) {
        this.eventClass = eventClass;
    }

    public Class<? extends AbstractEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public String toString() {
        return "EventSubscription{" +
                "eventClass=" + eventClass +
                '}';
    }
}
