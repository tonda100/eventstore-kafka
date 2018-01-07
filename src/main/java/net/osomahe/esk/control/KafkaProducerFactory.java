package net.osomahe.esk.control;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.Properties;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

import net.osomahe.config.entity.Config;
import net.osomahe.config.entity.ConfigDefaultValue;
import net.osomahe.config.entity.ConfigName;
import net.osomahe.esk.entity.AbstractEvent;


/**
 * @author Antonin Stoklasek
 */
@Stateless
public class KafkaProducerFactory {

    private KafkaProducer<String, AbstractEvent> kafkaProducer;

    @Inject
    @Config
    @ConfigName("event-store.kafka-url")
    @ConfigDefaultValue("localhost:9092")
    private String kafkaServer;


    @Produces
    public KafkaProducer<String, AbstractEvent> getKafkaProducer() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, this.kafkaServer);
        props.put(ACKS_CONFIG, "all");
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class.getName());

        return new KafkaProducer<>(props);
    }
}
