package net.osomahe.esk.control;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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
@Singleton
@Startup
public class KafkaProducerFactory {

    private KafkaProducer<String, AbstractEvent> kafkaProducer;

    @Inject
    @Config
    @ConfigName("event-store.kafka-url")
    @ConfigDefaultValue("localhost:9092")
    private String kafkaServer;

    @PostConstruct
    public void init(){
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, this.kafkaServer);
        props.put(ACKS_CONFIG, "all");
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class.getName());

        this.kafkaProducer = new KafkaProducer<>(props);
    }


    @Produces
    public KafkaProducer<String, AbstractEvent> getKafkaProducer(){
        return this.kafkaProducer;
    }

    @PreDestroy
    public void destroy(){
        this.kafkaProducer.close(5, TimeUnit.SECONDS);
    }
}
