# Installation

Installation steps needs to be done prior the development.

1. Download Java EE 8 application server e.g. [OpenLiberty](https://openliberty.io/downloads/)
  * Extract the archive `unzip openliberty-all-18.0.0.1-20180207-0949.zip`
  * Install profile `wlp/bin/server create eventstore`
2. Download [Apache Kafka](https://kafka.apache.org/quickstart)
  * Extract the archive `tar -zxvf kafka_2.11-1.0.0.tgz`
  * Set properties as you will `kafka/config/server.properties` 
    ```properties
    log.retention.hours=-1
    num.partitions=128
    compression.type=gzip
    delete.topic.enable=false
    ```