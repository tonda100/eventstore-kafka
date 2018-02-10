# Event Store Kafka
CDI extension for Java EE 8 application using [Apache Kafka](https://kafka.apache.org/) as Event Store with 
[Apache Tamaya](http://tamaya.incubator.apache.org/) used for configuration.

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
    <version>0.2.0</version>
</dependency>
<dependency>
    <groupId>org.apache.tamaya</groupId>
    <artifactId>tamaya-core</artifactId>
    <version>0.3-incubating</version>
</dependency>
<dependency>
    <groupId>org.apache.tamaya.ext</groupId>
    <artifactId>tamaya-cdi</artifactId>
    <version>0.3-incubating</version>
</dependency>
```
2. Added extensions `src/main/resources/META-INF/services/javax.enterprise.inject.spi.Extension`
```text
org.apache.tamaya.cdi.TamayaCDIInjectionExtension
net.osomahe.esk.EventStoreExtension
```

### Producing events
1. Extend `AbstractEvent` class
2. Use `EventStorePublisher#publish(event)` method for publishing event to Apache Kafka.

### Consuming events
1. Extend `AbstractEvent` class or use the same as for publishing.
2. Observes for CDI event
```java
public void newCredentials(@Observes CredentialsCreatedEvent event) {
    // do some magic with event
}
```

## Advanced
Configuration possibilities and default values.
### Configuration
Configuration is done via Apache Tamaya project e.g. `src/main/resources/META-INF/javaconfiguration.properties`
```properties
# urls of Apache Kafka cluster nodes. Default value 'localhost:9092'
event-store.kafka-urls=hostname1:9092,hostname2:9092

# name of your application used as kafka group.id instances of same application are using same application id. Default value 'client-application'
event-store.application-id=your-application

# name of default topic. Default value 'application-topic'
event-store.default-topic=my-topic
```
### Customization
* `@TopicName` annotation for an event class to set different topic for given event(s)
* `@EventName` annotation for an event class to set different event name
* `@AsyncEvent` annotation for an event class to set asynchronous processing of events `@ObeservesAsync` required for handling async events
