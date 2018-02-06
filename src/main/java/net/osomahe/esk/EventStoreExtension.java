package net.osomahe.esk;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessObserverMethod;

import net.osomahe.esk.entity.AbstractEvent;
import net.osomahe.esk.entity.EventSubscription;


/**
 * Kafka event store extension. Handles subscription to for events which are observed.
 *
 * @author Antonin Stoklasek
 */
public class EventStoreExtension implements Extension {

    private final List<Class<? extends AbstractEvent>> events = new ArrayList<>();

    /**
     * Processes observation of events extended from {@link AbstractEvent}.
     * Puts found events to list of events for later subscription.
     *
     * @param processObserverMethod process observer object
     */
    public void processObserverMethod(@Observes final ProcessObserverMethod<? extends AbstractEvent, ?> processObserverMethod) {
        Method method = processObserverMethod.getAnnotatedMethod().getJavaMember();
        Class<? extends AbstractEvent> eventClass = (Class<? extends AbstractEvent>) method.getParameterTypes()[0];
        events.add(eventClass);
    }

    /**
     * After deployment method which fires events, what event should the subscription happened.
     *
     * @param afterDeploymentValidation hook for after deployment validation stage
     * @param beanManager bean manger
     */
    public void afterDeploymentValidation(@Observes final AfterDeploymentValidation afterDeploymentValidation, final BeanManager beanManager) {
        events.forEach(eventClass -> {
            beanManager.fireEvent(new EventSubscription(eventClass));
        });
    }
}
