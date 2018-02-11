package net.osomahe.esk.config.control;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.Properties;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import net.osomahe.esk.config.boundary.EventStorePublisherConfig;
import net.osomahe.esk.config.boundary.EventStoreSubscriberConfig;
import net.osomahe.esk.config.entity.KafkaConsumerConfig;
import net.osomahe.esk.config.entity.KafkaProducerConfig;


/**
 * This
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class DefaultEventStoreConfig implements EventStorePublisherConfig, EventStoreSubscriberConfig {

    private static final String DEFAULT_KAFKA_URL = "localhost:9092";

    private static final String DEFAULT_APPLICATION_ID = "client-application";

    @Inject
    @KafkaProducerConfig
    private Instance<Properties> instanceProducer;

    @Inject
    @KafkaConsumerConfig
    private Instance<Properties> instanceConsumer;


    @Override
    public Properties getKafkaProducerConfig() {
        if (instanceProducer.isResolvable()) {
            return instanceProducer.get();
        }
        return getDefaultProducerConfig();
    }

    private Properties getDefaultProducerConfig() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, DEFAULT_KAFKA_URL);
        props.put(ACKS_CONFIG, "all");
        return props;
    }

    @Override
    public Properties getKafkaConsumerConfig() {
        if (instanceProducer.isResolvable()) {
            return instanceProducer.get();
        }
        return getDefaultConsumerConfig();
    }

    private Properties getDefaultConsumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, DEFAULT_KAFKA_URL);
        props.put(GROUP_ID_CONFIG, DEFAULT_APPLICATION_ID);
        props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}
