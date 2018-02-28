package net.osomahe.esk.eventstore.control;

import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Schedule;
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

import net.osomahe.esk.config.boundary.ConfigurationBoundary;
import net.osomahe.esk.eventstore.entity.AbstractEvent;
import net.osomahe.esk.eventstore.entity.AsyncEvent;
import net.osomahe.esk.eventstore.entity.EventName;
import net.osomahe.esk.eventstore.entity.LoggableEvent;


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

    @Resource
    private ManagedScheduledExecutorService mses;

    // map of topics used to find out what event should be fired for the application
    private Map<String, Map<String, Class<? extends AbstractEvent>>> mapTopics = new ConcurrentHashMap<>();

    private Jsonb jsonb;

    private KafkaConsumer<String, JsonObject> consumer;

    private ScheduledFuture<?> sfConsumerPoll;

    private String applicationName;


    @PostConstruct
    public void init() {
        this.jsonb = JsonbBuilder.create();

        eventDataStore.getEventClasses().forEach(this::subscribeForTopic);
        Properties config = this.config.getKafkaConsumerConfig();
        this.applicationName = config.getProperty(GROUP_ID_CONFIG);
        logger.info(String.format("Subscribing as %s for topics %s", applicationName, mapTopics));

        if (mapTopics.size() > 0) {
            this.consumer = new KafkaConsumer<>(
                    config,
                    new StringDeserializer(),
                    new EventDeserializer()
            );

            consumer.subscribe(mapTopics.keySet());
            this.sfConsumerPoll = this.mses.scheduleAtFixedRate(this::pollMessages, 1_000, 200, TimeUnit.MILLISECONDS);
        }
    }

    @Schedule(hour = "*", minute = "*", persistent = false)
    public void checkLiveness() {
        if (sfConsumerPoll.isCancelled() || sfConsumerPoll.isDone()) {
            logger.warning(String.format("KafkaConsumer polling has to be restarted for %s ", applicationName));
            this.sfConsumerPoll = this.mses.scheduleAtFixedRate(this::pollMessages, 1_000, 200, TimeUnit.MILLISECONDS);
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
            try {
                ConsumerRecords<String, JsonObject> records = consumer.poll(100);
                for (ConsumerRecord<String, JsonObject> rcd : records) {
                    JsonObject event = rcd.value();
                    String eventName = event.getString("name");
                    Map<String, Class<? extends AbstractEvent>> mapEvents = mapTopics.get(rcd.topic());
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
                        if (eventClass.isAnnotationPresent(AsyncEvent.class)) {
                            this.events.fireAsync(data);
                        } else {
                            this.events.fire(data);
                        }
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in polling kafka messages", e);
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
