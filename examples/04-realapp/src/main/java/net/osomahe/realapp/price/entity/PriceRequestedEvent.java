package net.osomahe.realapp.price.entity;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import net.osomahe.esk.eventstore.entity.EventExpirationSecs;
import net.osomahe.esk.eventstore.entity.EventKey;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@EventExpirationSecs(120)
public class PriceRequestedEvent extends PriceEvent {

    @EventKey
    private String code;

    @JsonbCreator
    public PriceRequestedEvent(@JsonbProperty("code") String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "PriceRequestedEvent{" +
                "code='" + code + '\'' +
                "} " + super.toString();
    }
}
