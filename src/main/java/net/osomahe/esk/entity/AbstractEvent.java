package net.osomahe.esk.entity;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;


/**
 * This class represents a general object which will be the parent object for all the events.
 *
 * @author Antonin Stoklasek
 * @since 0.3
 */
public abstract class AbstractEvent {

    private static final int PARTITION_COUNT = 128;

    private String aggregateId;

    private ZonedDateTime dateTime;

    public AbstractEvent() {
        String uuid = UUID.randomUUID().toString();
        int partition = Math.abs(uuid.hashCode()) % PARTITION_COUNT;
        this.aggregateId = String.format("%s-%s-%s", uuid, System.currentTimeMillis(), partition);
        dateTime = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public String getAggregateId() {
        return this.aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
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
