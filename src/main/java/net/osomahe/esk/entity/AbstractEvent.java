package net.osomahe.esk.entity;

import java.time.ZonedDateTime;


/**
 * This class represents a general object which will be the parent object for all the events.
 *
 * @author Antonin Stoklasek
 */
public abstract class AbstractEvent {

    private String aggregateId;

    private ZonedDateTime dateTime;

    public AbstractEvent() {

    }

    public AbstractEvent(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getAggregateId() {
        return this.aggregateId;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "AbstractEvent{" +
                "aggregateId='" + aggregateId + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
