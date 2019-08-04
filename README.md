# Event Store Kafka
CDI extension for Java EE 8 application using [Apache Kafka](https://kafka.apache.org/) as Event Store.

[![Build Status](https://travis-ci.org/tonda100/eventstore-kafka.svg?branch=dev)](https://travis-ci.org/tonda100/eventstore-kafka)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Five minute start
How to quickly start using the Event Store Kafka with your Java EE 8 project.
### Setup
1. Add maven dependencies to your pom.xml
    ```xml
    <dependency>
        <groupId>net.osomahe</groupId>
        <artifactId>eventstore-kafka</artifactId>
        <version>0.4.3</version>
    </dependency>
    ```
2. Added extensions `src/main/resources/META-INF/services/javax.enterprise.inject.spi.Extension`
    ```text
    net.osomahe.esk.EventStoreExtension
    ```

### Producing events
1. Extend `AbstractEvent` class
2. Use `EventStorePublisher#publish(event)` method for publishing event to Apache Kafka synchronously.
or
2. Use `EventStorePublisher#publishAsync(event)` method for publishing event to Apache Kafka asynchronously.

### Consuming events
1. Extend `AbstractEvent` class or use the same as for publishing.
2. Observes for CDI event
    ```java
    public void handleCreate(@Observes TodoCreatedEvent event) {
        // do some magic with event
    }
    ```

## Advanced
Configuration possibilities and default values. Unfortunately JavaEE 8 does not have any standard means of configuration and
I didn't want to  
### Configuration
Unfortunately JavaEE 8 does not provide any standard way. There are two ways of configuring the eventstore-kafka.
1. Application has to produce using `@Produces` and correct qualifier see [example](examples/02-todolist-advanced/src/main/java/net/osomahe/todolist/net.osomahe.realapp.EventStoreConfiguration.java):
  * `java.util.Properties` with qualifiers `@KafkaProducerConfig` for overriding producer properties otherwise default values will be in place.
    ```java
    Properties props = new Properties();
    props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ACKS_CONFIG, "all");
    ```
  * `java.util.Properties` with qualifiers `@KafkaConsumerConfig` for overriding consumer properties otherwise default values will be in place.
    ```java
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(GROUP_ID_CONFIG, "client-application");
    props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
    ```
2. Application has to implement `EventStorePublisherConfig` and (or) `EventStoreConsumerConfig` to provide non-default properties

### Customization
* `@TopicName` annotation for an event class to set different topic for given event(s)
* `@EventName` annotation for an event class to set different event name
* `@AsyncEvent` annotation for an event class to set asynchronous processing of events `@ObeservesAsync` required for handling async events
