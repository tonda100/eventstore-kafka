package net.osomahe.esk.boundary;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import net.osomahe.esk.control.TopicService;
import net.osomahe.esk.entity.AbstractEvent;


/**
 * Provides publishing to Kafka topics.
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class EventStorePublisher {

    @Inject
    private KafkaProducer<String, AbstractEvent> kafkaProducer;

    @Inject
    private TopicService topicService;

    /**
     * Publishes the given event to the Kafka topic.
     *
     * @param event event to be published
     * @param <T> event supposed to extend {@link AbstractEvent}
     */
    public <T extends AbstractEvent> void publish(T event) {
        fillMetadata(event);
        int partition = getPartition(event);
        ProducerRecord<String, AbstractEvent> record = new ProducerRecord<>(
                topicService.getTopicName(event.getClass()),
                partition,
                event.getAggregateId(),
                event);
        this.kafkaProducer.send(record);
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
