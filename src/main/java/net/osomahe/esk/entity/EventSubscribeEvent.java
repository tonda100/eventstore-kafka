package net.osomahe.esk.entity;

/**
 * @author Antonin Stoklasek
 */
public class EventSubscribeEvent {

    private final Class<? extends AbstractEvent> eventClass;

    public EventSubscribeEvent(Class<? extends AbstractEvent> eventClass) {
        this.eventClass = eventClass;
    }

    public Class<? extends AbstractEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public String toString() {
        return "EventSubscribeEvent{" +
                "eventClass=" + eventClass +
                '}';
    }
}
