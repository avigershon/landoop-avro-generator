# Landoop Avro Generator
[![Build Status](https://jenkins.landoop.com/buildStatus/icon?job=Avro-Generator&.png)](https://jenkins.landoop.com/job/Avro-Generator)
[![Join the Landoop chat](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Landoop/support)

> Synthetic avro message generator

* Creates new Kafka topics with particular number of `partitions` and `replication`
* Registers Avro schemas into the [schema-registry](https://schema-registry-ui.landoop.com)
* Generates sets of i.e. 1 Million Avro messages of particular specifications

The following topics are populated

Kafka Topic               | Avro messages                                                                         | Versions
------------------------- | ------------------------------------------------------------------------------------- | :------:
generator-text            | Single field `text` with 50 to 100 chars payload                                      |    1
generator-types           | Basic Avro types `string`, `boolean`, `int`, `long`, `float`, `double`                |    1
generator-types-upsert    | As above, but 1% of the messages contain the **same** value in field `text`           |    1
generator-sql             | Contains reserved `SQL` words as fields and also in contents                          |    1
generator-shipments       | Item shipments to stores for e-commerce use-case                                      |    1
generator-sales           | Item sales for e-commerce use-case                                                    |    1
generator-evolution-widen | Testing `Avro Evolution` type widenning ¹                                             |    2
generator-evolution-add   | Testing `Avro Evolution` adding new fields with default value ²                       |    4

¹ `float` is widenned to `double` both *old* and *new* records are generated

² Keeps adding new fields

# Building

    mvn clean install

# Running

Export 3 system variables

     export BROKERS="broker-hostname:9092"
     export ZK="zookeeper-hostname:2181"
     export SCHEMA_REGISTRY="http://schema-registry-hostname:8081"

And then execute - by passing in **number of messages per set** and **partitions**

     cd target
     ./landoop-avro-generator 1000000 10

# Clean up

    kafka-topics --delete --zookeeper $ZK --topic generator-text
    kafka-topics --delete --zookeeper $ZK --topic generator-types
    kafka-topics --delete --zookeeper $ZK --topic generator-types-upsert
    kafka-topics --delete --zookeeper $ZK --topic generator-sql
    kafka-topics --delete --zookeeper $ZK --topic generator-shipments
    kafka-topics --delete --zookeeper $ZK --topic generator-sales
    kafka-topics --delete --zookeeper $ZK --topic generator-evolution-widen
    kafka-topics --delete --zookeeper $ZK --topic generator-evolution-add
    sleep 2
    kafka-topics --zookeeper $ZK --list | grep "generator-"

Delete the _schemas topic

    kafka-topics --delete --zookeeper $ZK --topic _schemas
    kafka-run-class kafka.tools.ZooKeeperMainWrapper -server $ZK delete /schema_registry/schema_id_counter

    kafka-avro-console-consumer --zookeeper $ZK --topic _schemas --from-beginning
