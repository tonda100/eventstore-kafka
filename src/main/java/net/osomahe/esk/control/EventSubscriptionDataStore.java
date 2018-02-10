package net.osomahe.esk.control;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import net.osomahe.esk.entity.AbstractEvent;
import net.osomahe.esk.entity.EventSubscription;


/**
 * CDI Event subscription data store for events which were recognized by CDI extension to be observed.
 * This data store will later provide the event classes to {@link EventStoreSubscriber} which will handle the subscription.
 *
 * @author Antonin Stoklasek
 */
@ApplicationScoped
public class EventSubscriptionDataStore {

    private final List<Class<? extends AbstractEvent>> eventClasses = new CopyOnWriteArrayList<>();


    /**
     * Observes for {@link EventSubscription}
     *
     * @param event event which will be the application subscribed for
     */
    public void subscribeForEvent(@Observes EventSubscription event) {
        eventClasses.add(event.getEventClass());
    }

    /**
     * Provides list of events which supposed to be consumed by application.
     *
     * @return list of {@link AbstractEvent} sub-classes
     */
    public List<Class<? extends AbstractEvent>> getEventClasses() {
        return eventClasses;
    }
}
