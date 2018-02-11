package net.osomahe.esk.config.boundary;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;

import java.util.Properties;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import net.osomahe.esk.config.entity.KafkaConsumerConfig;
import net.osomahe.esk.config.entity.KafkaProducerConfig;


/**
 * Provides configuration for event store publisher and subscriber.
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class ConfigurationBoundary {

    private static final String DEFAULT_KAFKA_URL = "localhost:9092";

    private static final String DEFAULT_APPLICATION_ID = "client-application";

    @Inject
    @KafkaProducerConfig
    private Instance<Properties> instanceProducer;

    @Inject
    @KafkaConsumerConfig
    private Instance<Properties> instanceConsumer;

    @Inject
    private Instance<EventStorePublisherConfig> instancePublisher;

    @Inject
    private Instance<EventStoreSubscriberConfig> instanceSubscriber;


    /**
     * Provides configuration properties for kafka producer.
     *
     * @return properties for configuration.
     */
    public Properties getKafkaProducerConfig() {
        if (instancePublisher.isResolvable()) {
            return instancePublisher.get().getKafkaProducerConfig();
        }
        Properties producerConfig = getDefaultProducerConfig();
        if (instanceProducer.isResolvable()) {
            producerConfig.putAll(instanceProducer.get());
        }
        return producerConfig;
    }

    private Properties getDefaultProducerConfig() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, DEFAULT_KAFKA_URL);
        props.put(ACKS_CONFIG, "all");
        return props;
    }

    /**
     * Provides configuration properties for kafka consumer.
     *
     * @return properties for configuration.
     */
    public Properties getKafkaConsumerConfig() {
        if (instanceSubscriber.isResolvable()) {
            return instanceSubscriber.get().getKafkaConsumerConfig();
        }
        Properties consumerConfig = getDefaultConsumerConfig();
        if (instanceConsumer.isResolvable()) {
            consumerConfig.putAll(instanceConsumer.get());
        }
        return consumerConfig;
    }

    private Properties getDefaultConsumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, DEFAULT_KAFKA_URL);
        props.put(GROUP_ID_CONFIG, DEFAULT_APPLICATION_ID);
        props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}
