package net.osomahe.realapp.price.entity;

import java.time.ZonedDateTime;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
public class PriceReceivedEvent extends PriceEvent {

    private String code;

    private ZonedDateTime dateTime;

    private Double price;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "PriceReceivedEvent{" +
                "code='" + code + '\'' +
                ", dateTime=" + dateTime +
                ", price=" + price +
                "} " + super.toString();
    }
}
