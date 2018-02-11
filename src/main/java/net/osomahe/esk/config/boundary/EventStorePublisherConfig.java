package net.osomahe.esk.config.boundary;

import java.util.Properties;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
public interface EventStorePublisherConfig {

    Properties getKafkaProducerConfig();

}
