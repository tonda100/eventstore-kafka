package net.osomahe.realapp;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

import java.util.Properties;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import net.osomahe.esk.config.entity.KafkaConsumerConfig;
import net.osomahe.esk.config.entity.KafkaProducerConfig;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Singleton
@Startup
public class EventStoreConfiguration {

    private static final String APPLICATION_NAME = "realapp";

    @Inject
    @ConfigProperty(name = "event-store.kafka-urls")
    private String kafkaServer;

    @Produces
    @KafkaProducerConfig
    public Properties producerConfig() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        return props;
    }

    @Produces
    @KafkaConsumerConfig
    public Properties consumerConfig() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(GROUP_ID_CONFIG, APPLICATION_NAME);
        return props;
    }
}
