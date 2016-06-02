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

     export BROKERS="cloudera.landoop.com:29092"
     export ZK="cloudera.landoop.com:22181"
     export SCHEMAREGISTRY="http://cloudera.landoop.com:28081"

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
