package net.osomahe.esk.healthcheck.boundary;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.json.JsonObject;

import org.apache.kafka.clients.consumer.KafkaConsumer;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class HealthCheckBoundary {

    private static final Logger logger = Logger.getLogger(HealthCheckBoundary.class.getName());

    @Resource
    private ManagedExecutorService mes;


    @Inject
    private Instance<KafkaConsumer<String, JsonObject>> instanceKafkaConsumer;

    public boolean checkKafkaProducer() {
        try (KafkaConsumer<String, JsonObject> kafka = instanceKafkaConsumer.get()) {
            CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(() -> kafka.listTopics() != null, mes);
            return result.get(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot connect to kafka", e);
        }
        return false;
    }

}
