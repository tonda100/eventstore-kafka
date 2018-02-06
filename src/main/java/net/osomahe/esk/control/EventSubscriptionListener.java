package net.osomahe.esk.control;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import net.osomahe.esk.entity.EventSubscribeEvent;


/**
 * @author Antonin Stoklasek
 */
public class EventSubscriptionListener {

    @Inject
    private Instance<EventSubscriber> subscriber;

    @Resource
    private ManagedScheduledExecutorService mses;


    public void subscribeForEvent(@Observes EventSubscribeEvent event) {
        mses.schedule(() -> subscriber.get().subscribeForTopic(event.getEventClass()), 1, TimeUnit.SECONDS);
    }
}
