package net.osomahe.esk.control;

import static java.lang.System.Logger.Level.DEBUG;

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

import net.osomahe.config.entity.Config;
import net.osomahe.config.entity.ConfigDefaultValue;
import net.osomahe.config.entity.ConfigName;
import net.osomahe.esk.boundary.EventNameMapper;
import net.osomahe.esk.entity.AbstractEvent;
import net.osomahe.esk.entity.AsyncEvent;


/**
 * @author Antonin Stoklasek
 */
@Singleton
@Startup
public class EventStoreSubscriber {

    private static final System.Logger logger = System.getLogger(EventStoreSubscriber.class.getName());

    @Inject
    @Config
    @ConfigName("event-store.subscriber.group-id")
    private String groupId;

    @Inject
    @Config
    @ConfigName("event-store.subscriber.topics")
    private String topicName;

    @Inject
    @Config
    @ConfigName("event-store.kafka-url")
    @ConfigDefaultValue("localhost:9092")
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
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props, new StringDeserializer(), new EventDeserializer(eventNameMapper));
        this.consumer.subscribe(Arrays.asList(topicName));
        this.consumer.poll(100);
        List<PartitionInfo> partitions = this.consumer.partitionsFor(topicName);
        for (PartitionInfo pi : partitions) {
            this.consumer.seekToBeginning(Arrays.asList(new TopicPartition(topicName, pi.partition())));
        }
        this.sfConsumerPoll = this.mses.scheduleAtFixedRate(this::pollMessages, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void pollMessages() {
        synchronized (this.consumer) {
            ConsumerRecords<String, AbstractEvent> records = consumer.poll(100);
            for (ConsumerRecord<String, AbstractEvent> rcd : records) {
                if (rcd.value() != null) {
                    logger.log(DEBUG, "Polling event " + rcd.value().getClass().getSimpleName());
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
