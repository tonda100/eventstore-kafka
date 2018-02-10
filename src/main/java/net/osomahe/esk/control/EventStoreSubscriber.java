package net.osomahe.esk.control;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.tamaya.inject.api.Config;

import net.osomahe.esk.entity.AbstractEvent;
import net.osomahe.esk.entity.AsyncEvent;
import net.osomahe.esk.entity.EventName;


/**
 * Handles subscription to correct Kafka topics and fires correct events which are observed.
 *
 * @author Antonin Stoklasek
 */
@Singleton
@Startup
public class EventStoreSubscriber {

    @Inject
    @Config(value = "event-store.kafka-urls", defaultValue = "localhost:9092")
    private String kafkaServer;

    @Inject
    @Config(value = "event-store.application-id", defaultValue = "client-application")
    private String applicationId;

    @Inject
    private EventSubscriptionDataStore eventDataStore;

    @Inject
    private TopicService topicService;

    @Inject
    private Event<AbstractEvent> events;

    @Resource
    private ManagedScheduledExecutorService mses;

    // map of topics used to find out what event should be fired for the application
    private Map<String, Map<String, Class<? extends AbstractEvent>>> mapTopics = new ConcurrentHashMap<>();

    private Jsonb jsonb;

    private KafkaConsumer<String, JsonObject> consumer;

    private ScheduledFuture<?> sfConsumerPoll;


    @PostConstruct
    public void init() {
        this.jsonb = JsonbBuilder.create();

        eventDataStore.getEventClasses().forEach(this::subscribeForTopic);
        if (mapTopics.size() > 0) {
            Properties props = new Properties();
            props.put(BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
            props.put(GROUP_ID_CONFIG, applicationId);
            props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
            this.consumer = new KafkaConsumer<>(props, new StringDeserializer(), new EventDeserializer());

            consumer.subscribe(mapTopics.keySet());
            this.sfConsumerPoll = this.mses.scheduleAtFixedRate(this::pollMessages, 1_000, 100, TimeUnit.MILLISECONDS);
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

    /**
     * Polling the messages from subscribed topics and fires the event further.
     */
    private void pollMessages() {
        synchronized (this.consumer) {
            ConsumerRecords<String, JsonObject> records = consumer.poll(100);
            for (ConsumerRecord<String, JsonObject> rcd : records) {
                JsonObject event = rcd.value();
                String eventName = event.getString("name");
                Map<String, Class<? extends AbstractEvent>> mapEvents = mapTopics.get(rcd.topic());
                if (mapEvents.containsKey(eventName)) {
                    Class<? extends AbstractEvent> eventClass = mapEvents.get(eventName);
                    AbstractEvent data = this.jsonb.fromJson(event.getJsonObject("data").toString(), eventClass);
                    if (eventClass.isAnnotationPresent(AsyncEvent.class)) {
                        this.events.fireAsync(data);
                    } else {
                        this.events.fire(data);
                    }
                }
            }
        }
    }

    @PreDestroy
    public void destroy() {
        if (this.sfConsumerPoll != null) {
            synchronized (this.consumer) {
                this.sfConsumerPoll.cancel(false);
                this.consumer.close(5, TimeUnit.SECONDS);
            }
        }
    }
}
