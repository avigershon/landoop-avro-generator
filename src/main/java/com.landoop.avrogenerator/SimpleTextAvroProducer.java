package com.landoop.avrogenerator;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;

public class SimpleTextAvroProducer {

  public static void main(String args[]) throws InterruptedException {

    Random r = new Random();
    long counter = 0;

    String brokers = System.getenv("BROKERS");
    String zookeepers = System.getenv("ZK");
    String schemaregistry = System.getenv("SCHEMA_REGISTRY");

    // Producer myProducer = getAvroProducer("192.168.99.100:9092", "http://192.168.99.100:8081");
    // String zookeepers = "192.168.99.100:2181";
    Producer myProducer = getStringAvroProducer(brokers, schemaregistry);
    String topicName = "SalesStringExample";
    Schema valueSchema = getSchema(VALUE_SCHEMA);
    KafkaTools.createTopic(zookeepers, topicName, 3, 1);

    while (true) {

      Long randomInt = new Long(r.nextInt(10));

      GenericRecord valueRecord = new GenericData.Record(valueSchema);
      valueRecord.put("itemID", randomInt);
      valueRecord.put("storeCode", "store-code-" + randomInt);
      valueRecord.put("count", randomInt);

      ProducerRecord newRecord = new ProducerRecord<String, GenericRecord>(topicName, "Key-" + randomInt, valueRecord);

      Integer dice = r.nextInt(1 + (int) counter % 100);
      if ((dice < 20) && (dice % 2 == 1)) {
        // noop
      } else {
        myProducer.send(newRecord);
      }
      myProducer.flush();
      Thread.sleep(randomInt);
      counter++;
      // Log out every 1K messages
      if (counter % 1000 == 0) {
        System.out.print(" . " + (counter / 1000) + "K");
      }

    }
  }

  private static final String VALUE_SCHEMA = "{`type`:`record`,`name`:`com.saleValue`," +
          "`doc`:`Sale object stored as value`," +
          "`fields`:[" +
          "{`name`:`itemID`,`type`:`long`,`doc`:`unique item ID`}," +
          "{`name`:`storeCode`,`type`:`string`,`doc`:`store code `}," +
          "{`name`:`count`,`type`:`long`,`doc`:`number of products shipped to store`}" +
          "]}";

  private static Schema getSchema(String schemaString) {
    Schema.Parser parser = new Schema.Parser();
    return parser.parse(schemaString.replace('`', '"'));
  }

  private static Producer<String, Object> getStringAvroProducer(String brokers, String schemaregistry) {
    System.out.println("Starting [AvroProducer] with brokers=[" + brokers + "] and schema-registry=[" + schemaregistry + "]");
    Properties producerProps = new Properties();
    producerProps.put("bootstrap.servers", brokers);
    producerProps.put("acks", "all");
    producerProps.put("key.serializer", StringSerializer.class.getName());
    producerProps.put("value.serializer", KafkaAvroSerializer.class.getName());
    producerProps.put("linger.ms", "10"); // ?
    producerProps.put("schema.registry.url", schemaregistry);
    return new KafkaProducer<>(producerProps);
  }

}
