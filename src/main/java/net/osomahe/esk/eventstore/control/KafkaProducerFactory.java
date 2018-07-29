package net.osomahe.esk.eventstore.control;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.Properties;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

import net.osomahe.esk.config.boundary.ConfigurationBoundary;
import net.osomahe.esk.eventstore.entity.EventStoreEvent;


/**
 * Produces {@link KafkaProducer} instances.
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class KafkaProducerFactory {

    @Inject
    private ConfigurationBoundary config;

    /**
     * Produces {@link KafkaProducer} according to given configuration.
     *
     * @return KafkaProducer instance
     */
    @Produces
    public KafkaProducer<String, EventStoreEvent> getKafkaProducer() {
        Properties props = config.getKafkaProducerConfig();
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class.getName());

        return new KafkaProducer<>(props);
    }
}
