package net.osomahe.esk.entity;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.json.bind.annotation.JsonbTransient;


/**
 * This class represents a general object which will be the parent object for all the events.
 *
 * @author Antonin Stoklasek
 * @since 0.3
 */
public abstract class AbstractEvent {

    private static final Duration DEFAULT_VALIDITY = Duration.of(1024, ChronoUnit.YEARS);

    private static final int PARTITION_COUNT = 128;

    private String aggregateId;

    private ZonedDateTime dateTime;

    public AbstractEvent() {
        String uuid = UUID.randomUUID().toString();
        int partition = Math.abs(uuid.hashCode()) % PARTITION_COUNT;
        this.aggregateId = String.format("%s-%s-%s", uuid, System.currentTimeMillis(), partition);
        dateTime = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public AbstractEvent(String aggregateId) {
        if (aggregateId.contains("-")) {
            this.aggregateId = aggregateId;
        } else {
            int partition = Math.abs(aggregateId.hashCode()) % PARTITION_COUNT;
            this.aggregateId = String.format("%s-%s", aggregateId, partition);
        }
        dateTime = ZonedDateTime.now(ZoneOffset.UTC);
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

    @JsonbTransient
    public boolean isValid() {
        return this.dateTime.plus(getValidity()).isAfter(ZonedDateTime.now());
    }

    @JsonbTransient
    public Duration getValidity() {
        return DEFAULT_VALIDITY;
    }

    @Override
    public String toString() {
        return "AbstractEvent{" +
                "aggregateId='" + aggregateId + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
