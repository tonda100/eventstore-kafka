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
import net.osomahe.esk.entity.EventSubscribeEvent;


/**
 * @author Antonin Stoklasek
 */
public class EventStoreExtension implements Extension {

    private final List<Class<? extends AbstractEvent>> topics = new ArrayList<>();

    public void processObserverMethod(@Observes final ProcessObserverMethod<? extends AbstractEvent, ?> processObserverMethod) {
        Method method = processObserverMethod.getAnnotatedMethod().getJavaMember();
        Class<? extends AbstractEvent> eventClass = (Class<? extends AbstractEvent>) method.getParameterTypes()[0];
        topics.add(eventClass);
    }

    public void afterDeploymentValidation(@Observes final AfterDeploymentValidation afterDeploymentValidation, final BeanManager beanManager) {
        topics.forEach(eventClass -> {
            beanManager.fireEvent(new EventSubscribeEvent(eventClass));
        });
    }
}
