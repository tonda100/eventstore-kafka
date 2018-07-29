package net.osomahe.esk.eventstore.boundary;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import net.osomahe.esk.eventstore.control.TopicService;
import net.osomahe.esk.eventstore.entity.EventGroupKey;
import net.osomahe.esk.eventstore.entity.EventKey;
import net.osomahe.esk.eventstore.entity.EventNotPublishedException;
import net.osomahe.esk.eventstore.entity.EventStoreEvent;
import net.osomahe.esk.eventstore.entity.EventStoreException;
import net.osomahe.esk.eventstore.entity.LoggableEvent;


/**
 * Provides publishing to Kafka topics.
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class EventStorePublisher {

    private static final Logger logger = Logger.getLogger(EventStorePublisher.class.getName());

    @Inject
    private KafkaProducer<String, EventStoreEvent> kafkaProducer;

    @Inject
    private TopicService topicService;

    /**
     * Publishes the given event to the Kafka topic synchronously.
     *
     * @param <T> event supposed to extend {@link EventStoreEvent}
     * @param event event to be published
     * @return metadata about the record
     * @throws EventNotPublishedException when publish of event failed this runtime exception is thrown
     */
    public <T extends EventStoreEvent> RecordMetadata publish(T event) {
        try {
            RecordMetadata metadata = publishAsync(event).get();
            if (metadata == null) {
                throw new EventNotPublishedException(event);
            }
            return metadata;
        } catch (InterruptedException | ExecutionException e) {
            throw new EventStoreException("Cannot publish the event: " + event, e);
        }
    }

    /**
     * Publishes the given event to the Kafka topic asynchronously.
     *
     * @param <T> event supposed to extend {@link EventStoreEvent}
     * @param event event to be published
     * @return Future of record metadata
     */
    public <T extends EventStoreEvent> CompletableFuture<RecordMetadata> publishAsync(T event) {
        ProducerRecord<String, EventStoreEvent> record = new ProducerRecord<>(
                topicService.getTopicName(event.getClass()),
                getPartition(event),
                getEventKey(event),
                event);
        if (event.getClass().isAnnotationPresent(LoggableEvent.class)) {
            logger.fine(String.format("EventStoreEvent (%s) publishing %s", event.getClass().getSimpleName(), record));
        }
        CompletableFuture<RecordMetadata> futureMetadata = CompletableFuture.supplyAsync(() -> {
            try {
                return this.kafkaProducer.send(record).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new EventStoreException("Cannot publish the event: " + event, e);
            }
        }).exceptionally(throwable -> {
            logger.log(Level.SEVERE, "EventStoreEvent was NOT published", throwable);
            return null;
        });
        return futureMetadata;
    }

    private <T extends EventStoreEvent> String getEventKey(T event) {
        Object eventKey = getValueForAnnotation(event, event.getClass(), EventKey.class);
        if (eventKey != null) {
            return eventKey.toString();
        }
        return System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
    }

    /**
     * Parses the aggregateId of an event a takes part after las '-' which is partition number.
     *
     * @param event event whose partition number should be returned
     * @param <T>
     * @return partition number of given event
     */
    private <T extends EventStoreEvent> Integer getPartition(T event) {
        Object groupKeyValue = getGroupKeyValue(event);
        if (groupKeyValue == null) {
            return null;
        }
        int partitionCount = topicService.getPartitionCount(event.getClass());
        return Math.abs(groupKeyValue.hashCode()) % partitionCount;
    }

    private <T extends EventStoreEvent> Object getGroupKeyValue(T event) {
        return getValueForAnnotation(event, event.getClass(), EventGroupKey.class);
    }


    private <T extends EventStoreEvent> Object getValueForAnnotation(T event, Class<?> eventClass, Class<? extends Annotation> annotation) {
        Object valueFromField = getValueFromField(event, eventClass, annotation);
        if (valueFromField != null) {
            return valueFromField;
        }

        Object valueFromMethod = getValueFromMethod(event, eventClass, annotation);
        if (valueFromMethod != null) {
            return valueFromMethod;
        }

        if (eventClass.getSuperclass() != null) {
            return getValueForAnnotation(event, eventClass.getSuperclass(), annotation);
        }
        return null;
    }

    private <T extends EventStoreEvent> Object getValueFromField(T event, Class<?> eventClass, Class<? extends Annotation> annotation) {
        for (Field f : eventClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(annotation)) {
                try {
                    f.setAccessible(true);
                    return f.get(event);
                } catch (IllegalAccessException e) {
                    logger.log(Level.SEVERE, "Cannot get field value of " + annotation + " for event: " + event, e);
                }
            }
        }
        return null;
    }

    private <T extends EventStoreEvent> Object getValueFromMethod(T event, Class<?> eventClass, Class<? extends Annotation> annotation) {
        for (Method method : eventClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                try {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    return method.invoke(event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.log(Level.SEVERE, "Cannot get method value of " + annotation + " for event: " + event, e);
                }
            }
        }
        return null;
    }

    @PreDestroy
    public void destroy() {
        this.kafkaProducer.close(5, TimeUnit.SECONDS);
    }
}
