# Landoop Avro Generator
[![Build Status](https://jenkins.landoop.com/buildStatus/icon?job=Avro-Generator&.png)](https://jenkins.landoop.com/job/Avro-Generator)

> Synthetic avro message generator

* Creates new Kafka topics with particular number of `partitions` and `replication`
* Registers Avro schemas into the `schema-registry`
* Generates sets of i.e. 1 Million Avro messages of particular specifications

The following topics are populated

Kafka Topic               | Avro messages
------------------------- | ------------------------------------------------------------------------------
generator-text            | Avro messages with single field `text` with characters 50 chars to 100 chars ¹
generator-types           | Avro messages with most common Avro types : `string`, `boolean`, `int`, `long`, `float`, `double`
generator-types-upsert    | Same as above, but 1% of the messages contain the **same** value in field `text`. This is used to test connectors that support the `UPSERT` mode
generator-sql             | Contains reserved `SQL` words as fields and also in contents
generator-evolution-widen | Testing `Avro Evolution` type widenning ²
generator-evolution-add   | Testing `Avro Evolution` adding new fields with default value ³
generator-shipments       | Item shipments to stores for e-commerce use-case
generator-sales           | Item sales for e-commerce use-case

¹ (2 sets) First set 50 chars - second set 100 chars

² (2 sets) First set has a `float` then we widen to `double` and generate both old and new data

³  (4 sets) First set `text` then add a new `boolean` field, then a third `int` and a fourth `float`

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
    kafka-topics --delete --zookeeper $ZK --topic generator-evolution-widen
    kafka-topics --delete --zookeeper $ZK --topic generator-evolution-add
    sleep 2
    kafka-topics --zookeeper $ZK --list | grep "generator-"

Delete the _schemas topic

    kafka-topics --delete --zookeeper $ZK --topic _schemas
    kafka-run-class kafka.tools.ZooKeeperMainWrapper -server $ZK delete /schema_registry/schema_id_counter

    kafka-avro-console-consumer --zookeeper $ZK --topic _schemas --from-beginning
