package net.osomahe.esk.eventstore.boundary;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import net.osomahe.esk.eventstore.control.TopicService;
import net.osomahe.esk.eventstore.entity.AbstractEvent;
import net.osomahe.esk.eventstore.entity.EventNotPublishedException;
import net.osomahe.esk.eventstore.entity.EventStoreException;
import net.osomahe.esk.eventstore.entity.LoggableEvent;


/**
 * Provides publishing to Kafka topics.
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class EventStorePublisher {

    private static final Logger logger = Logger.getLogger(EventStorePublisher.class.getName());

    @Inject
    private KafkaProducer<String, AbstractEvent> kafkaProducer;

    @Inject
    private TopicService topicService;

    /**
     * Publishes the given event to the Kafka topic synchronously.
     *
     * @param <T> event supposed to extend {@link AbstractEvent}
     * @param event event to be published
     * @return metadata about the record
     * @throws EventNotPublishedException when publish of event failed this runtime exception is thrown
     */
    public <T extends AbstractEvent> RecordMetadata publish(T event) {
        try {
            RecordMetadata metadata = publishAsync(event).get();
            if (metadata == null) {
                throw new EventNotPublishedException(event);
            }
            return metadata;
        } catch (InterruptedException | ExecutionException e) {
            throw new EventStoreException("Cannot publish the event: " + event, e);
        }
    }

    /**
     * Publishes the given event to the Kafka topic asynchronously.
     *
     * @param <T> event supposed to extend {@link AbstractEvent}
     * @param event event to be published
     * @return Future of record metadata
     */
    public <T extends AbstractEvent> CompletableFuture<RecordMetadata> publishAsync(T event) {
        fillMetadata(event);
        int partition = getPartition(event);
        ProducerRecord<String, AbstractEvent> record = new ProducerRecord<>(
                topicService.getTopicName(event.getClass()),
                partition,
                event.getAggregateId(),
                event);
        if (event.getClass().isAnnotationPresent(LoggableEvent.class)) {
            logger.fine(String.format("Event id %s (%s) publishing %s", event.getAggregateId(), event.getClass().getSimpleName(), record));
        }
        CompletableFuture<RecordMetadata> futureMetadata = CompletableFuture.supplyAsync(() -> {
            try {
                return this.kafkaProducer.send(record).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new EventStoreException("Cannot publish the event: " + event, e);
            }
        }).exceptionally(throwable -> {
            logger.log(Level.SEVERE, "Event was NOT published", throwable);
            return null;
        });
        return futureMetadata;
    }

    /**
     * Parses the aggregateId of an event a takes part after las '-' which is partition number.
     *
     * @param event event whose partition number should be returned
     * @param <T>
     * @return partition number of given event
     */
    private <T extends AbstractEvent> int getPartition(T event) {
        if (event.getAggregateId().contains("-")) {
            String lastPart = event.getAggregateId().substring(event.getAggregateId().lastIndexOf('-') + 1);
            return Integer.parseInt(lastPart);
        }
        throw new IllegalArgumentException("Event aggregateId does NOT contain info about partition number. event: " + event);
    }

    /**
     * Fills necessary metadata of given event to be successfully published.
     * It fills aggregateId in format UUID-epochMillis-partitionNumber and dateTime with current value.
     *
     * @param event
     * @param <T>
     */
    private <T extends AbstractEvent> void fillMetadata(T event) {
        if (event.getAggregateId() == null) {
            int partitionCount = topicService.getPartitionCount(event.getClass());
            String uuid = UUID.randomUUID().toString();
            int partition = Math.abs(uuid.hashCode()) % partitionCount;
            String aggregateId = String.format("%s-%s-%s", uuid, System.currentTimeMillis(), partition);
            event.setAggregateId(aggregateId);
        }
        if (event.getDateTime() == null) {
            event.setDateTime(ZonedDateTime.now(ZoneOffset.UTC));
        }
    }

    @PreDestroy
    public void destroy() {
        this.kafkaProducer.close(5, TimeUnit.SECONDS);
    }
}
