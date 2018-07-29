package net.osomahe.realapp.price.control;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.ejb.Singleton;

import net.osomahe.realapp.price.entity.Price;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Singleton
public class PriceStorage {

    private Map<String, Price> prices = new ConcurrentHashMap<>();

    private AtomicLong counterRequested = new AtomicLong(0);

    private AtomicLong counterReceived = new AtomicLong(0);

    public void addPrice(Price price) {
        prices.put(price.getCode(), price);
    }

    public Collection<Price> getPrices() {
        return prices.values();
    }

    public Map<String, Long> getCounters() {
        Map<String, Long> map = new HashMap<>();
        map.put("counterRequested", counterRequested.longValue());
        map.put("counterReceived", counterReceived.longValue());
        return map;
    }

    public void plusReceived() {
        counterReceived.addAndGet(1);
    }

    public void plusRequested() {
        counterRequested.addAndGet(1);
    }
}
