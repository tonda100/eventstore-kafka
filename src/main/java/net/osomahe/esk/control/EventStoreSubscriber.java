package net.osomahe.esk.control;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
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

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.tamaya.inject.api.Config;

import net.osomahe.esk.boundary.EventNameMapper;
import net.osomahe.esk.entity.AbstractEvent;
import net.osomahe.esk.entity.AsyncEvent;


/**
 * @author Antonin Stoklasek
 */
@Singleton
@Startup
public class EventStoreSubscriber {

    @Inject
    @Config(value = "event-store.subscriber.group-id", defaultValue = "default-group")
    private String groupId;

    @Inject
    @Config(value = "event-store.subscriber.client-id", defaultValue = "default-client")
    private String clientId;

    @Inject
    @Config(value = "event-store.subscriber.topics", defaultValue = "default-topic")
    private String topicName;

    @Inject
    @Config(value = "event-store.subscriber.reset", defaultValue = "true")
    private boolean reset;

    @Inject
    @Config(value = "event-store.kafka-url", defaultValue = "localhost:9092")
    private String kafkaServer;

    private KafkaConsumer<String, AbstractEvent> consumer;

    private ScheduledFuture<?> sfConsumerPoll;

    @Resource
    private ManagedScheduledExecutorService mses;

    @Inject
    private Event<AbstractEvent> event;

    @Inject
    private EventNameMapper eventNameMapper;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, this.kafkaServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId != null ? clientId : UUID.randomUUID().toString());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props, new StringDeserializer(), new EventDeserializer(eventNameMapper));
        this.consumer.subscribe(Arrays.asList(topicName));
        this.consumer.poll(100);
        if (reset) {
            List<PartitionInfo> partitions = this.consumer.partitionsFor(topicName);
            for (PartitionInfo pi : partitions) {
                this.consumer.seekToBeginning(Arrays.asList(new TopicPartition(topicName, pi.partition())));
            }
        }
        this.sfConsumerPoll = this.mses.scheduleAtFixedRate(this::pollMessages, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void pollMessages() {
        synchronized (this.consumer) {
            ConsumerRecords<String, AbstractEvent> records = consumer.poll(100);
            for (ConsumerRecord<String, AbstractEvent> rcd : records) {
                if (rcd.value() != null) {
                    if (rcd.value().getClass().isAnnotationPresent(AsyncEvent.class)) {
                        this.event.fireAsync(rcd.value());
                    } else {
                        this.event.fire(rcd.value());
                    }
                }
            }
        }
    }

    @PreDestroy
    public void destroy() {
        synchronized (this.consumer) {
            this.sfConsumerPoll.cancel(false);
        }
    }
}
