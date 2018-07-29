package net.osomahe.realapp.price.control;

import java.time.ZonedDateTime;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.osomahe.esk.eventstore.boundary.EventStorePublisher;
import net.osomahe.realapp.price.entity.Price;
import net.osomahe.realapp.price.entity.PriceReceivedEvent;
import net.osomahe.realapp.price.entity.PriceRequestedEvent;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class PriceEventHandler {

    @Inject
    private EventStorePublisher esPublisher;

    @Inject
    private PriceStorage storage;

    public void handleRequestPrice(@Observes PriceRequestedEvent eventIn) {

        PriceReceivedEvent eventOut = new PriceReceivedEvent();
        eventOut.setCode(eventIn.getCode());
        eventOut.setPrice(Math.random() * 20_000);
        eventOut.setDateTime(ZonedDateTime.now());

        this.esPublisher.publish(eventOut);

        this.storage.plusRequested();
    }

    public void handleReceivePrice(@Observes PriceReceivedEvent event) {
        Price price = new Price();
        price.setCode(event.getCode());
        price.setDateTime(event.getDateTime());
        price.setPrice(event.getPrice());
        storage.addPrice(price);

        this.storage.plusReceived();
    }


}
