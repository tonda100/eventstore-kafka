package net.osomahe.esk.control;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import net.osomahe.esk.boundary.EventNameMapper;
import net.osomahe.esk.entity.AbstractEvent;


/**
 * @author Antonin Stoklasek
 */
public class EventDeserializer implements Deserializer<AbstractEvent> {

    private static final Logger logger = Logger.getLogger(EventDeserializer.class.getName());

    private final EventNameMapper eventNameMapper;

    private final Jsonb jsonb;

    public EventDeserializer(EventNameMapper eventNameMapper) {
        this.eventNameMapper = eventNameMapper;
        this.jsonb = JsonbBuilder.create();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public AbstractEvent deserialize(String topic, byte[] data) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(data)) {
            final JsonObject jsonObject = Json.createReader(input).readObject();
            Optional<Class<? extends AbstractEvent>> oEventClass = this.eventNameMapper.getClassForName(jsonObject.getString("name"));
            if (oEventClass.isPresent()) {
                return jsonb.fromJson(jsonObject.getJsonObject("data").toString(), oEventClass.get());
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not deserialize event: " + e.getMessage());
            throw new SerializationException("Could not deserialize event", e);
        }
        return null;
    }

    @Override
    public void close() {

    }
}
