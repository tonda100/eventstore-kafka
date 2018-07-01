package net.osomahe.esk.eventstore.control;

import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.kafka.common.serialization.StringDeserializer;

import net.osomahe.esk.config.boundary.ConfigurationBoundary;
import net.osomahe.esk.eventstore.entity.AbstractEvent;
import net.osomahe.esk.eventstore.entity.AsyncEvent;
import net.osomahe.esk.eventstore.entity.EventName;
import net.osomahe.esk.eventstore.entity.LoggableEvent;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;


/**
 * Handles subscription to correct Kafka topics and fires correct events which are observed.
 *
 * @author Antonin Stoklasek
 */
@Singleton
@Startup
public class EventStoreSubscriber {

    private static final Logger logger = Logger.getLogger(EventStoreSubscriber.class.getName());

    @Inject
    private ConfigurationBoundary config;

    @Inject
    private EventSubscriptionDataStore eventDataStore;

    @Inject
    private TopicService topicService;

    @Inject
    private Event<AbstractEvent> events;

    // map of topics used to find out what event should be fired for the application
    private Map<String, Map<String, Class<? extends AbstractEvent>>> mapTopics = new ConcurrentHashMap<>();

    private Jsonb jsonb;

    private String applicationName;

    private Disposable disposableKafka;


    @PostConstruct
    public void init() {
        this.jsonb = JsonbBuilder.create();

        eventDataStore.getEventClasses().forEach(this::subscribeForTopic);
        Properties config = this.config.getKafkaConsumerConfig();
        config.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(VALUE_DESERIALIZER_CLASS_CONFIG, EventDeserializer.class.getName());
        this.applicationName = config.getProperty(GROUP_ID_CONFIG);
        logger.info(String.format("Subscribing as %s for topics %s", applicationName, mapTopics));

        if (mapTopics.size() > 0) {
            ReceiverOptions<String, JsonObject> receiverOptions = ReceiverOptions.create(config);
            ReceiverOptions<String, JsonObject> options = receiverOptions.subscription(mapTopics.keySet());
            Flux<ReceiverRecord<String, JsonObject>> kafkaFlux = KafkaReceiver.create(options).receive();
            disposableKafka = kafkaFlux.subscribe(record -> {
                ReceiverOffset offset = record.receiverOffset();
                JsonObject event = record.value();
                String eventName = event.getString("name");
                Map<String, Class<? extends AbstractEvent>> mapEvents = mapTopics.get(record.topic());
                if (mapEvents.containsKey(eventName)) {
                    Class<? extends AbstractEvent> eventClass = mapEvents.get(eventName);
                    AbstractEvent data = this.jsonb.fromJson(event.getJsonObject("data").toString(), eventClass);
                    if (data.getClass().isAnnotationPresent(LoggableEvent.class)) {
                        logger.fine(String.format("Event id %s (%s) firing for %s %s",
                                data.getAggregateId(),
                                data.getClass().getSimpleName(),
                                applicationName,
                                data)
                        );
                    }
                    try {
                        if (eventClass.isAnnotationPresent(AsyncEvent.class)) {
                            this.events.fireAsync(data);
                        } else {
                            this.events.fire(data);
                        }
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error in firing polled kafka messages", e);
                    }
                }
                offset.acknowledge();
            });
        }
    }

    /**
     * Subscribes to kafka for given event.
     *
     * @param eventClass event which should be consumed from Kafka and fired to outside world
     */
    private void subscribeForTopic(Class<? extends AbstractEvent> eventClass) {
        String topicName = topicService.getTopicName(eventClass);
        if (!mapTopics.containsKey(topicName)) {
            mapTopics.put(topicName, new ConcurrentHashMap<>());
        }
        mapTopics.get(topicName).put(getEventName(eventClass), eventClass);
    }

    /**
     * Provides event name from event class.
     *
     * @param eventClass event class for which the event name will be returned
     * @return event name
     */
    private String getEventName(Class<? extends AbstractEvent> eventClass) {
        EventName eventName = eventClass.getAnnotation(EventName.class);
        if (eventName != null) {
            return eventName.value();
        }
        return eventClass.getSimpleName();
    }

    @PreDestroy
    public void destroy() {
        if (this.disposableKafka != null) {
            this.disposableKafka.dispose();
        }
    }


}
