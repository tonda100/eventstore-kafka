package net.osomahe.todolist;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

import java.util.Properties;

import javax.enterprise.inject.Produces;

import net.osomahe.esk.config.entity.KafkaConsumerConfig;
import net.osomahe.esk.config.entity.KafkaProducerConfig;


/**
 * Example of configuration for the event store.
 *
 * @author Antonin Stoklasek
 */
public class EventStoreConfiguration {

    private static final String KAFKA_SERVER_URLS = "kafkahost:9092";

    @Produces
    @KafkaProducerConfig
    public Properties producerConfig() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER_URLS);
        return props;
    }

    @Produces
    @KafkaConsumerConfig
    public Properties consumerConfig() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER_URLS);
        props.put(GROUP_ID_CONFIG, "todolist-advanced");
        return props;
    }

}
