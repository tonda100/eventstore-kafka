package net.osomahe.esk.config.boundary;

import java.util.Properties;


/**
 * Provides subscriber configuration.
 *
 * @author Antonin Stoklasek
 */
public interface EventStoreSubscriberConfig {

    /**
     * Complete configuration properties which will be passed to constructor of {@link org.apache.kafka.clients.consumer.KafkaConsumer}.
     * The key deserializer and value deserializer will be added (replaced) by library.
     *
     * @return configuration properties
     */
    Properties getKafkaConsumerConfig();


}
