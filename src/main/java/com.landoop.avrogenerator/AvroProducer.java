/**
 * Copyright 2016 Landoop.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package com.landoop.avrogenerator;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;

public class AvroProducer {

  private static final Logger log = LoggerFactory.getLogger(AvroProducer.class);
  private String brokers, schemaregistry;

  public AvroProducer(String brokers, String schemaregistry) {
    this.brokers = brokers;
    this.schemaregistry = schemaregistry;
  }

  public Producer<Object, Object> getAvroProducer(String brokers, String schemaregistry) {

    log.info("Starting [AvroProducer] with brokers=" + brokers + " and schema-registry=" + schemaregistry);
    Properties producerProps = new Properties();
    producerProps.put("bootstrap.servers", brokers);
    producerProps.put("acks", "all");
    producerProps.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
    producerProps.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
    producerProps.put("schema.registry.url", schemaregistry);

    return new KafkaProducer<>(producerProps);
  }

  public void sendMessages(int num, String topic, AllAvroMessages message) {
    // Injection<GenericRecord, byte[]> recordInjection = GenericAvroCodecs.toBinary(message.getSchema());
    byte[] bytes = (new byte[]{-32, 124});

    Random random = new Random();
    int max = 100;
    int min = 1;

    try (Producer<Object, Object> producer = getAvroProducer(brokers, schemaregistry)) {
      log.info("Sending " + num / 1000 + "K messages to topic [" + topic + "]");
      long startTime = System.nanoTime();
      for (int i = 0; i < num; i++) {
        GenericRecord avroRecord = new GenericData.Record(message.getSchema());

        // Depends on the message we are constructing
        if (message == AllAvroMessages.SIMPLE) {
          avroRecord.put("text", randomString(30));
        } else if (message == AllAvroMessages.SIMPLE100) {
          avroRecord.put("text", randomString(100));
        } else if (message == AllAvroMessages.PERSON) {
          avroRecord.put("name", randomString(50));
          avroRecord.put("adult", true);
          avroRecord.put("integer8", 8);
          avroRecord.put("integer16", 16);
          avroRecord.put("integer32", 32L);
          avroRecord.put("integer64", 64L);
          avroRecord.put("float32", (float) 21.32);
          avroRecord.put("float64", (double) 21122.321221212121);
          // avroRecord.put("mybytes", "assaas");
        } else if (message == AllAvroMessages.UPSERT_PERSON_1PC) {
          int ranNumber = random.nextInt(max - min + 1) + min;
          if (ranNumber == 50) {
            avroRecord.put("name", "SAME");
            avroRecord.put("adult", false);
          } else {
            avroRecord.put("name", randomString(50));
            avroRecord.put("adult", true);
          }
          avroRecord.put("integer8", 8);
          avroRecord.put("integer16", 16);
          avroRecord.put("integer32", 32L);
          avroRecord.put("integer64", 64L);
          avroRecord.put("float32", (float) 21.32);
          avroRecord.put("float64", (double) 21122.321221212121);
        } else if (message == AllAvroMessages.EVOLUTION) {
          avroRecord.put("name", randomString(50));
          avroRecord.put("number1", 1000);
          avroRecord.put("number2", (float) 1000.0);
        } else if (message == AllAvroMessages.EVOLUTION_ADD_TEXT) {
          avroRecord.put("name", randomString(50));
          avroRecord.put("number1", 1000);
          avroRecord.put("number2", (float) 1000.0);
          avroRecord.put("text", "payload");
        } else if (message == AllAvroMessages.SQL_INJECTION) {
          avroRecord.put("text", "sql injection");
        } else if (message == AllAvroMessages.RESERVED_SQL_WORDS) {
          avroRecord.put("as", "sql injection");
          avroRecord.put("from", 10);
        }
        /*
        else if (message == AllAvroMessages.EVOLUTION_WIDEN_FLOAT) {
          avroRecord.put("name", randomString(50));
          avroRecord.put("number1", 100000000000L);
          avroRecord.put("number2", 100000000000.000000000001D);
        } else if (message == AllAvroMessages.EVOLUTION_ADD_TEXT) {
          avroRecord.put("name", randomString(50));
          avroRecord.put("number1", 100000000000L);
          avroRecord.put("number2", 100000000000.000000000001D);
          avroRecord.put("text", "payload");
        }
         */

        if (i % 10000 == 0)
          System.out.print(" . " + (i / 1000) + "K");

        producer.send(new ProducerRecord<Object, Object>(topic, "key", avroRecord));

        //byte[] recordInBytes = recordInjection.apply(avroRecord);
        //producer.send(new ProducerRecord<String, byte[]>(topic, recordInBytes));
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
  static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  static SecureRandom rnd = new SecureRandom();

  String randomString(int len) {
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++)
      sb.append(AB.charAt(rnd.nextInt(AB.length())));
    return sb.toString();
  }

}