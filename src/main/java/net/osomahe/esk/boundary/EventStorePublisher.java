package net.osomahe.esk.boundary;

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
        int partition = getPartition(event);
        ProducerRecord<String, AbstractEvent> record = new ProducerRecord<>(topicName, partition, event.getAggregateId(), event);
        this.kafkaProducer.send(record);
    }

    private <T extends AbstractEvent> int getPartition(T event) {
        if (event.getAggregateId().contains("-")) {
            String lastPart = event.getAggregateId().substring(event.getAggregateId().lastIndexOf('-') + 1);
            return Integer.parseInt(lastPart);
        }
        return 0;
    }
}
