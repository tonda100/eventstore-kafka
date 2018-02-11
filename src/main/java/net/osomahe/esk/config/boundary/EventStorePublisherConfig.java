package net.osomahe.esk.config.boundary;

import java.util.Properties;


/**
 * Provides publisher configuration.
 *
 * @author Antonin Stoklasek
 */
public interface EventStorePublisherConfig {

    /**
     * Complete configuration properties which will be passed to constructor of {@link org.apache.kafka.clients.producer.KafkaProducer}.
     * The key serializer and value serializer will be added (replaced) by library.
     *
     * @return configuration properties
     */
    Properties getKafkaProducerConfig();

}
