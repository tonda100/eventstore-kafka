package net.osomahe.esk.eventstore.control;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import net.osomahe.esk.eventstore.entity.EventStoreEvent;
import net.osomahe.esk.eventstore.entity.EventSubscription;


/**
 * CDI EventStoreEvent subscription data store for events which were recognized by CDI extension to be observed.
 * This data store will later provide the event classes to {@link EventStoreSubscriber} which will handle the subscription.
 *
 * @author Antonin Stoklasek
 */
@ApplicationScoped
public class EventSubscriptionDataStore {
    private static final Logger logger = Logger.getLogger(EventSubscriptionDataStore.class.getName());

    private final List<Class<? extends EventStoreEvent>> eventClasses = new CopyOnWriteArrayList<>();


    /**
     * Observes for {@link EventSubscription}
     *
     * @param event event which will be the application subscribed for
     */
    public void subscribeForEvent(@Observes EventSubscription event) {
        eventClasses.add(event.getEventClass());
        logger.info("Observed subsription for event: " + event);
    }

    /**
     * Provides list of events which supposed to be consumed by application.
     *
     * @return list of {@link EventStoreEvent} sub-classes
     */
    public List<Class<? extends EventStoreEvent>> getEventClasses() {
        return eventClasses;
    }
}
