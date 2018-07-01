package net.osomahe.esk.eventstore.control;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.json.JsonObject;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import net.osomahe.esk.config.boundary.ConfigurationBoundary;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class KafkaConsumerFactory {


    @Inject
    private ConfigurationBoundary config;

    @Produces

    public KafkaConsumer<String, JsonObject> kafkaConsumer() {
        return new KafkaConsumer<>(
                config.getKafkaConsumerConfig(),
                new StringDeserializer(),
                new EventDeserializer()
        );
    }
}
