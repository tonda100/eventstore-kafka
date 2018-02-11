package net.osomahe.esk.eventstore.control;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;


/**
 * Deserialize the raw data from Kafka to {@link JsonObject}
 *
 * @author Antonin Stoklasek
 */
public class EventDeserializer implements Deserializer<JsonObject> {

    private static final Logger logger = Logger.getLogger(EventDeserializer.class.getName());

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public JsonObject deserialize(String topic, byte[] data) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(data)) {
            return Json.createReader(input).readObject();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not deserialize event: " + e.getMessage());
            throw new SerializationException("Could not deserialize event", e);
        }
    }

    @Override
    public void close() {

    }
}
