package net.osomahe.esk.control;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.kafka.common.serialization.Serializer;

import net.osomahe.esk.entity.AbstractEvent;
import net.osomahe.esk.entity.EventName;


/**
 * Serialize given {@link AbstractEvent} to {@link JsonObject} and to the utf-8 byte array.
 *
 * @author Antonin Stoklasek
 */
public class EventSerializer implements Serializer<AbstractEvent> {


    private final Jsonb jsonb;

    public EventSerializer() {
        this.jsonb = JsonbBuilder.create();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public void close() {

    }

    @Override
    public byte[] serialize(String topic, AbstractEvent event) {
        if (event == null) {
            return null;
        }

        String data = jsonb.toJson(event);

        JsonObject jo = Json.createObjectBuilder()
                .add("name", getEventName(event.getClass()))
                .add("data", jsonb.fromJson(data, JsonObject.class))
                .build();

        return jsonb.toJson(jo).getBytes(StandardCharsets.UTF_8);
    }

    private String getEventName(Class<? extends AbstractEvent> eventClass) {
        EventName eventName = eventClass.getAnnotation(EventName.class);
        if (eventName != null) {
            return eventName.value();
        }
        return eventClass.getSimpleName();
    }

}
