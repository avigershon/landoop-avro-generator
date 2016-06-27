# Landoop Avro Generator

A synthetic data generator that creates the following Kafka topics:

 + demo-simple
 + demo-simple100
 + demo-person
 + demo-person-1pc (1% of the records have the same value in the field `name`)
 + demo-evolution  (Avro evolution - just adding new columns)
 + demo-sql-inject
 + demo-reserved   (Uses reserved words) 

# Building
    
    mvn clean install 

# Running

Export 3 system variables 

     export BROKERS="broker.landoop.com:29092"
     export ZK="zookeeper.landoop.com:22181"
     export SCHEMA_REGISTRY="http://schema_registry.landoop.com:28081"

And then execute - by passing in <number of messages> and <partitions>

     cd target
     ./landoop-avro-generator 100000 1
    
# Clean up

    kafka-topics --delete --zookeeper $ZK --topic demo-simple
    kafka-topics --delete --zookeeper $ZK --topic demo-simple100
    kafka-topics --delete --zookeeper $ZK --topic demo-person
    kafka-topics --delete --zookeeper $ZK --topic demo-person-1pc
    kafka-topics --delete --zookeeper $ZK --topic demo-evolution
    kafka-topics --delete --zookeeper $ZK --topic demo-sql-inject
    kafka-topics --delete --zookeeper $ZK --topic demo-reserved
    sleep 2
    kafka-topics --zookeeper $ZK --list | grep "demo-"

Delete the _schemas topic

    kafka-topics --delete --zookeeper $ZK --topic _schemas
    kafka-run-class kafka.tools.ZooKeeperMainWrapper -server $ZK delete /schema_registry/schema_id_counter

    kafka-avro-console-consumer --zookeeper $ZK --topic _schemas --from-beginning