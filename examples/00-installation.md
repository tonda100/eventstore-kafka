# Installation

Installation steps needs to be done prior the development.

1. Download Java EE 8 application server e.g. [OpenLiberty](https://openliberty.io/downloads/)
  * Extract the archive `unzip openliberty-all-18.0.0.1-20180207-0949.zip`
  * Install profile `wlp/bin/server create eventstore`
2. Download [Apache Kafka](https://kafka.apache.org/quickstart)
  * Extract the archive `tar -zxvf kafka_2.11-1.0.0.tgz`
  * Set properties as you will `kafka/config/server.properties` 
    ```properties
    log.retention.ms=-1
    num.partitions=32
    compression.type=gzip
    delete.topic.enable=false
    log.cleanup.policy=compact
    ```
    
## Kafka Docker run

```bash
docker network create eventstore

docker run -d --name es-zk --net eventstore -p 7072:7072 zookeeper:3.4.12

docker run -d --name es-kafka --net eventstore -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT=es-zk:2181 \
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_BROKER_ID=0 \
-e KAFKA_NUM_PARTITIONS=32 -e KAFKA_LOG_RETENTION_MS=-1 -e KAFKA_COMPRESSION_TYPE=gzip \
-e KAFKA_DELETE_TOPIC_ENABLE=false -e KAFKA_LOG_CLEANUP_POLICY=compact \
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 -e KAFKA_HEAP_OPTS="-Xmx512m -Xms512m" confluentinc/cp-kafka:4.1.1

docker exec es-kafka kafka-topics --zookeeper=es-zk:2181 --list

docker exec es-kafka kafka-streams-application-reset --application-id client-application --input-topics eventstore \
--bootstrap-servers localhost:9092 --dry-run

docker exec es-kafka kafka-run-class kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic eventstore

```