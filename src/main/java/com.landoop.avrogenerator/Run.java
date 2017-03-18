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

import com.landoop.avrogenerator.messages.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Run {

  private static final Logger log = LoggerFactory.getLogger(Run.class);

  /**
   * @param messages   Number of messages to generate per SET
   * @param partitions Number of partitions to use when creating topics
   * @param throttle   Whether to `throttle` message genaration to 10K messages/sec
   * @param filter     to control whether to run all examples - or just particular ones
   */
  private Run(int messages, int partitions, int throttle, String filter, String brokers, String zookeepers, String schemaregistry) {

    // Avro - Basics
    if (filter.equals("") || filter.contains("simple")) {
      log.info("The following topics will be generated [" +
              "generator-text " +
              "generator-types " +
              "generator-types-upsert " +
              "generator-sql]");
      runScenario(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-text", Generator.TEXT50);
      runScenario(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-text", Generator.TEXT100);
      runScenario(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-types", Generator.AVRO_TYPES);
      runScenario(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-types-upsert", Generator.AVRO_TYPES_UPSERT);
      runScenario(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-sql", Generator.SQL_RESERVED_WORDS);
    }
    if (filter.equals("") || filter.contains("evolution")) {
      log.info("The following topics will be generated [" +
              "generator-evolution-widen " +
              "generator-evolution-add]");
      // Avro - Evolution widening types
      runScenario(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-evolution-widen", Generator.EVOLUTION_WIDEN_INITIAL);
      runScenario(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-evolution-widen", Generator.EVOLUTION_WIDEN_TOLONG);
      // Avro - Evolution adding new fields
      runScenario(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-evolution-add", Generator.EVOLUTION_INITIAL);
      runScenario(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-evolution-add", Generator.EVOLUTION_ADD1);
      runScenario(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-evolution-add", Generator.EVOLUTION_ADD2);
      runScenario(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-evolution-add", Generator.EVOLUTION_ADD3);
    }
    if (filter.equals("") || filter.contains("ecommerce")) {
      log.info("The following topics will be generated [" +
              "generator-shipments " +
              "generator-sales]");
      // Avro - E-commerce scenario
      runEcommerce(messages, partitions, throttle, brokers, zookeepers, schemaregistry, "generator-shipments", "generator-sales", Generator.ECOMMERCE_SHIPMENTS, Generator.ECOMMERCE_SALES2);
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 2)
      throw new IllegalArgumentException("Requires 2 arguments <number of messages> and <partitions> and <throttle>");

    int messages = Integer.parseInt(args[0]);
    int partitions = Integer.parseInt(args[1]);
    int throttle = Integer.MAX_VALUE;
    if ((args.length == 2) || (args[2] + "").equals("")) {
      log.info("No <throttle> parameter used - so we will be going at our faster rate (!) Watch out ! Expect 1M messages/");
    } else {
      throttle = Integer.parseInt(args[2]);
      log.info("Throttling avro message generator to " + throttle + " messages / sec");
    }
    String filter = ""; //args[3];

    String brokers = System.getenv("BROKERS");
    String zookeepers = System.getenv("ZK");
    String schemaregistry = System.getenv("SCHEMA_REGISTRY");

    if (brokers == null || zookeepers == null | schemaregistry == null)
      throw new IllegalArgumentException("Please set environment variables and re-rerun" + "\n" +
              " export BROKERS=hostname:9092" + "\n" +
              " export ZK=hostname:2181" + "\n" +
              " export SCHEMA_REGISTRY='http://hostname:8081'".replace("'", "\""));

    log.info("Running <landoop-avro-generator> generating " + messages + " messages on " + partitions + " partitions");

    while(true) {
      new Run(messages, partitions, throttle, filter, brokers, zookeepers, schemaregistry);
    }
  }

  private void runScenario(Integer messages, Integer partitions, Integer throttle, String brokers, String zookeepers, String schemaregistry, String topicName, Generator avromessageType) {
    log.info("Running scenario for topic : " + topicName);
    AvroProducer avroGenerator = new AvroProducer(brokers, schemaregistry);
    KafkaTools.createTopic(zookeepers, topicName, partitions, 1);
    avroGenerator.sendMessages(messages, topicName, avromessageType, throttle);
  }

  private void runEcommerce(Integer messages, Integer partitions, Integer throttle, String brokers, String zookeepers, String schemaregistry, String topicShipments, String topicSales,
                            Generator shipmentMessage, Generator salesMessage) {
    AvroProducer avroGenerator = new AvroProducer(brokers, schemaregistry);
    KafkaTools.createTopic(zookeepers, topicShipments, partitions, 1);
    KafkaTools.createTopic(zookeepers, topicSales, partitions, 1);
    avroGenerator.sendEcommerceMessages(messages, throttle, topicShipments, topicSales, shipmentMessage, salesMessage);
  }

}