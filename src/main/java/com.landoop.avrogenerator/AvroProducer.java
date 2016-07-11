/*
 * Copyright 2016 Landoop.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.landoop.avrogenerator;

import com.landoop.avrogenerator.messages.AvroEcommerce;
import com.landoop.avrogenerator.messages.Generator;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;

class AvroProducer {

  private static final Logger log = LoggerFactory.getLogger(AvroProducer.class);
  private String brokers, schemaregistry;
  private Random random = new Random();

  /**
   * @param brokers        i.e. hostname:9092
   * @param schemaregistry i.e. http://schema_registry-hostname:8081
   */
  AvroProducer(String brokers, String schemaregistry) {
    this.brokers = brokers;
    this.schemaregistry = schemaregistry;
  }

  /**
   * Bootstrap a Kafka Producer using Avro for value & key serializer
   */
  private Producer<Object, Object> getAvroProducer(String brokers, String schemaregistry) {
    log.info("Starting [AvroProducer] with brokers=[" + brokers + "] and schema-registry=[" + schemaregistry + "]");
    Properties producerProps = new Properties();
    producerProps.put("bootstrap.servers", brokers);
    producerProps.put("acks", "all");
    producerProps.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
    producerProps.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
    producerProps.put("schema.registry.url", schemaregistry);
    return new KafkaProducer<>(producerProps);
  }

  /**
   * Sends X number of messages to 2 topic (shipments and sales) to simulate real-time streaming inventory app
   * <p>
   * It uses 715 stores x 100 products - and keeps an internalInventory for the shake of generating realistic data
   */
  void sendEcommerceMessages(int num, String topicShipments, String topicSales, Generator shipmentMessage, Generator salesMessage) {

    int totalItems = 100; // 715 * 100
    Map<String, Integer> internalInventory = new HashMap<>(); // key: storeCode-itemID value: count

    try (Producer<Object, Object> producer = getAvroProducer(brokers, schemaregistry)) {
      log.info("Sending " + num / 1000 + "K messages to topic [" + topicShipments + "] and [" + topicSales + "]");
      long startTime = System.nanoTime();
      for (int i = 0; i < num; i++) {

        // Figure out a product and store
        long itemID = Long.valueOf(random.nextInt(totalItems));
        int tradomStoreID = random.nextInt(AvroEcommerce.storeCodes.length - 1);
        String store = AvroEcommerce.storeCodes[tradomStoreID];

        // Get current inventory status
        int realInventory = 0;
        if (internalInventory.containsKey(store + "-" + itemID)) {
          realInventory = internalInventory.get(store + "-" + itemID);
        }

        // Figure out how many to ship
        int shippedItems = 0;
        if (realInventory <= 10) {
          shippedItems = 100;
        } else {
          shippedItems = (random.nextInt(9) + 1) * 10; // 10..100
        }

        realInventory = realInventory + shippedItems;

        // Create a shipment
        GenericRecord shipmentRecord = new GenericData.Record(shipmentMessage.getSchema());
        shipmentRecord.put("itemID", itemID);
        shipmentRecord.put("storeCode", store);
        shipmentRecord.put("count", 100);
        producer.send(new ProducerRecord<Object, Object>(topicShipments, 0, shipmentRecord));

        // Update internal inventory
        internalInventory.put(store + "-" + itemID, realInventory);

        // For every shipment generate 5 sales
        for (int s = 0; s < 5; s++) {
          // Figure out how many to sell
          int currentInventory = internalInventory.get(store + "-" + itemID);

          int numberSales = 0;
          if (currentInventory == 0) {
            // Do not make a sale
          } else if (currentInventory >= 4 && currentInventory <= 10) {
            numberSales = 1;
          } else {
            numberSales = random.nextInt(10) * currentInventory;
          }

          realInventory = realInventory - numberSales;

          if (numberSales > 0) {
            // Create a sales
            GenericRecord salesRecord = new GenericData.Record(shipmentMessage.getSchema());
            salesRecord.put("itemID", itemID);
            salesRecord.put("storeCode", store);
            salesRecord.put("count", numberSales);
            producer.send(new ProducerRecord<Object, Object>(topicSales, 0, salesRecord));

            // Update internal inventory
            internalInventory.put(store + "-" + itemID, realInventory);
          }
        }

        // Log out every 10K messages
        if (i % 10000 == 0) {
          System.out.print(" . " + (i / 1000) + "K");
        }

      }
      System.out.println();
      long endTime = System.nanoTime();
      long durationMsec = (endTime - startTime) / 1000000;
      log.info("Total time " + (durationMsec / 1000.0) + " sec ");
      log.info("Shipment message rate : " + (int) (num / (durationMsec / 1000.0)) + " msg / sec");
      log.info("Sales message rate : " + (int) (num * 5 / (durationMsec / 1000.0)) + " msg / sec");
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

  }

  /**
   * Sends X number of messages to a topic - with a particular message format
   */

  void sendMessages(int num, String topic, Generator message) {

    try (Producer<Object, Object> producer = getAvroProducer(brokers, schemaregistry)) {
      log.info("Sending " + num / 1000 + "K messages to topic [" + topic + "]");
      long startTime = System.nanoTime();
      for (int i = 0; i < num; i++) {
        GenericRecord avroRecord = new GenericData.Record(message.getSchema());

        // Match on message type
        if (message == Generator.TEXT50) {
          avroRecord.put("text", randomString(50));
        } else if (message == Generator.TEXT100) {
          avroRecord.put("text", randomString(100));
        } else if (message == Generator.AVRO_TYPES) {
          avroRecord.put("text", randomString(50));
          avroRecord.put("flag", false);
          avroRecord.put("integer8", 8);
          avroRecord.put("integer16", 16);
          avroRecord.put("integer32", 32L);
          avroRecord.put("integer64", 64L);
          avroRecord.put("float32", (float) 21.32);
          avroRecord.put("float64", 21122.321221212121); // double
        } else if (message == Generator.AVRO_TYPES_UPSERT) {
          int ranNumber = random.nextInt(100);
          if (ranNumber == 50) {
            avroRecord.put("text", "PRIMARY-KEY-" + random.nextInt(10));
          } else {
            avroRecord.put("text", randomString(50));
          }
          avroRecord.put("flag", true);
          avroRecord.put("integer8", 8);
          avroRecord.put("integer16", 16);
          avroRecord.put("integer32", 32L);
          avroRecord.put("integer64", 64L);
          avroRecord.put("float32", (float) 10.01);
          avroRecord.put("float64", 10000.001); // double
        } else if (message == Generator.SQL_RESERVED_WORDS) {
          String[] reservedList = {"SELECT * FROM TABLE1", "CREATE AS SELECT FROM", "DROP TABLE TABLE1"};
          avroRecord.put("as", reservedList[random.nextInt(reservedList.length)]);
          avroRecord.put("from", 10);
        } else if (message == Generator.EVOLUTION_WIDEN_INITIAL) {
          avroRecord.put("text", randomString(50));
          avroRecord.put("number1", 1000L);
          avroRecord.put("number2", (float) 1000.0);
        } else if (message == Generator.EVOLUTION_WIDEN_TOLONG) {
          avroRecord.put("text", randomString(50));
          avroRecord.put("number1", 1000L);
          avroRecord.put("number2", 100000000000.000000000001D);
        } else if (message == Generator.EVOLUTION_INITIAL) {
          avroRecord.put("text", randomString(50));
        } else if (message == Generator.EVOLUTION_ADD1) {
          avroRecord.put("text", randomString(50));
          avroRecord.put("flag", true);
        } else if (message == Generator.EVOLUTION_ADD2) {
          avroRecord.put("text", randomString(50));
          avroRecord.put("flag", true);
          avroRecord.put("number1", 100);
        } else if (message == Generator.EVOLUTION_ADD3) {
          avroRecord.put("text", randomString(50));
          avroRecord.put("flag", true);
          avroRecord.put("number1", 100);
          avroRecord.put("number2", (float) 100.001);
        }

        if (i % 10000 == 0)
          System.out.print(" . " + (i / 1000) + "K");

        producer.send(new ProducerRecord<Object, Object>(topic, 0, avroRecord));
      }
      System.out.println();
      long endTime = System.nanoTime();
      long durationMsec = (endTime - startTime) / 1000000;
      log.info("Total time " + (durationMsec / 1000.0) + " sec ");
      log.info("Message rate : " + (int) (num / (durationMsec / 1000.0)) + " msg / sec");
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  // Random string generator
  private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final SecureRandom rnd = new SecureRandom();

  private String randomString(int len) {
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++)
      sb.append(AB.charAt(rnd.nextInt(AB.length())));
    return sb.toString();
  }

}