package net.osomahe.todolist;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;

import java.util.Properties;

import javax.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.tamaya.inject.api.Config;

import net.osomahe.esk.config.boundary.EventStorePublisherConfig;
import net.osomahe.esk.config.boundary.EventStoreSubscriberConfig;


/**
 * @author Antonin Stoklasek
 */
public class EventStoreConfiguration implements EventStorePublisherConfig, EventStoreSubscriberConfig {

    private static final String APPLICATION_NAME = "todolist-expert";

    @Inject
    @Config("event-store.kafka-urls")
    private String kafkaServer;

    @Override
    public Properties getKafkaProducerConfig() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ACKS_CONFIG, "all");
        return props;
    }

    @Override
    public Properties getKafkaConsumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(GROUP_ID_CONFIG, APPLICATION_NAME);
        props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}
