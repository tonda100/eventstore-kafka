# Event Store Kafka
CDI extension for Java EE 8 application using Apache Kafka as Event Store with 
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
    <version>0.1.0</version>
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


