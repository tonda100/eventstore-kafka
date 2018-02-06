package net.osomahe.esk.control;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.tamaya.inject.api.Config;

import net.osomahe.esk.entity.AbstractEvent;
import net.osomahe.esk.entity.TopicName;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class TopicService {
    private final Map<String, Integer> mapPartitionCount = new ConcurrentHashMap<>();

    @Inject
    @Config(value = "event-store.kafka-urls", defaultValue = "localhost:9092")
    private String kafkaServer;

    @Inject
    @Config(value = "event-store.default-topic", defaultValue = "application-topic")
    private String defaultTopic;

    public String getTopicName(Class<? extends AbstractEvent> eventClass) {
        TopicName topicName = eventClass.getAnnotation(TopicName.class);
        if (topicName == null) {
            return defaultTopic;
        }
        return topicName.value();
    }

    public int getPartitionCount(Class<? extends AbstractEvent> eventClass) {
        return getPartitionCount(getTopicName(eventClass));
    }

    public synchronized int getPartitionCount(String topicName) {
        if (mapPartitionCount.containsKey(topicName)) {
            return mapPartitionCount.get(topicName);
        }
        Integer count = loadPartitionCount(topicName);
        mapPartitionCount.put(topicName, count);
        return count;
    }

    private Integer loadPartitionCount(String topicName) {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, this.kafkaServer);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        KafkaConsumer consumer = new KafkaConsumer(props);
        try {
            return consumer.partitionsFor(topicName).size();
        } finally {
            consumer.close(10, TimeUnit.SECONDS);
        }
    }

}
