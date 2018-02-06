package net.osomahe.esk.control;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import net.osomahe.esk.entity.EventSubscription;


/**
 * CDI listener for event subscriptions.
 *
 * @author Antonin Stoklasek
 */
public class EventSubscriptionListener {

    @Inject
    private Instance<EventStoreSubscriber> subscriber;

    @Resource
    private ManagedScheduledExecutorService mses;

    /**
     * Observes for {@link EventSubscription}
     *
     * @param event event which will be the application subscribed for
     */
    public void subscribeForEvent(@Observes EventSubscription event) {
        mses.schedule(() -> subscriber.get().subscribeForTopic(event.getEventClass()), 1, TimeUnit.SECONDS);
    }
}
