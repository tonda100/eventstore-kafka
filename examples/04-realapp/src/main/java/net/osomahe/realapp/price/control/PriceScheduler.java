package net.osomahe.realapp.price.control;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;

import net.osomahe.esk.eventstore.boundary.EventStorePublisher;
import net.osomahe.realapp.price.entity.PriceRequestedEvent;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Singleton
@Startup
public class PriceScheduler {

    private final String[] codes = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q"};

    @Inject
    private EventStorePublisher esPublisher;

    @Resource
    private ManagedScheduledExecutorService mses;

    private List<ScheduledFuture<?>> scheduleds = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        Arrays.stream(codes)
                .forEach(code -> {
                    int delay = (int) (Math.random() * 60);
                    Runnable runnable = () -> requestBitstampPrice(code);
                    scheduleds.add(this.mses.scheduleAtFixedRate(runnable, delay, 60, TimeUnit.SECONDS));
                });
    }

    private void requestBitstampPrice(String code) {
        this.esPublisher.publish(new PriceRequestedEvent(code));
    }

    @PreDestroy
    public void cleanUp() {
        this.scheduleds.forEach(future -> future.cancel(false));
    }


}
