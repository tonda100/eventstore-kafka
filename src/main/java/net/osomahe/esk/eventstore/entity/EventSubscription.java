package net.osomahe.esk.eventstore.entity;

/**
 * EventStoreEvent class used to subscribe for specific event in Kafka.
 *
 * @author Antonin Stoklasek
 */
public class EventSubscription {

    private final Class<? extends EventStoreEvent> eventClass;

    public EventSubscription(Class<? extends EventStoreEvent> eventClass) {
        this.eventClass = eventClass;
    }

    public Class<? extends EventStoreEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public String toString() {
        return "EventSubscription{" +
                "eventClass=" + eventClass +
                '}';
    }
}
