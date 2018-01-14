package net.osomahe.esk.boundary;

import static java.lang.System.Logger.Level.DEBUG;

import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import net.osomahe.config.entity.Config;
import net.osomahe.config.entity.ConfigName;
import net.osomahe.esk.entity.AbstractEvent;


/**
 * @author Antonin Stoklasek
 */
@Stateless
public class EventStorePublisher {

    private static final System.Logger logger = System.getLogger(EventStorePublisher.class.getName());

    @Inject
    @Config
    @ConfigName("event-store.publisher.default-topic")
    private String topicName;

    @Inject
    private KafkaProducer<String, AbstractEvent> kafkaProducer;

    public <T extends AbstractEvent> void publish(T event) {
        publish(event, topicName);
    }

    public <T extends AbstractEvent> void publish(T event, String topicName) {
        logger.log(DEBUG, "Publishing event " + event.getClass().getSimpleName());
        int partition = getPartition(event);
        ProducerRecord<String, AbstractEvent> record = new ProducerRecord<>(topicName, partition, event.getAggregateId(), event);
        this.kafkaProducer.send(record);
    }

    private <T extends AbstractEvent> int getPartition(T event) {
        if (event.getAggregateId().contains("-")) {
            String lastPart = event.getAggregateId().substring(event.getAggregateId().lastIndexOf('-') + 1);
            return Integer.parseInt(lastPart);
        }
        throw new IllegalArgumentException("Event aggregateId does NOT contain info about partition number. event: " + event);
    }

    @PreDestroy
    public void destroy() {
        this.kafkaProducer.close(5, TimeUnit.SECONDS);
    }
}
