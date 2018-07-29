package net.osomahe.realapp.price.boundary;

import java.util.Collection;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import net.osomahe.realapp.price.control.PriceStorage;
import net.osomahe.realapp.price.entity.Price;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class PriceBoundary {

    @Inject
    private PriceStorage storage;

    public void addPrice(Price price) {
        storage.addPrice(price);
    }

    public Collection<Price> getPrices() {
        return storage.getPrices();
    }

    public Map<String, Long> getCounters() {
        return storage.getCounters();
    }
}
