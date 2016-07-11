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

  private int messages, partitions;

  private Run(int messages, int partitions, String brokers, String zookeepers, String schemaregistry, String filter) {
    this.messages = messages;
    this.partitions = partitions;

    // Avro - Basics
    if (filter.equals("") || filter.contains("simple")) {
      runScenario(brokers, zookeepers, schemaregistry, "generator-text", Generator.TEXT50);
      runScenario(brokers, zookeepers, schemaregistry, "generator-text", Generator.TEXT100);
      runScenario(brokers, zookeepers, schemaregistry, "generator-types", Generator.AVRO_TYPES);
      runScenario(brokers, zookeepers, schemaregistry, "generator-types-upsert", Generator.AVRO_TYPES_UPSERT);
      runScenario(brokers, zookeepers, schemaregistry, "generator-sql", Generator.SQL_RESERVED_WORDS);
    }
    if (filter.equals("") || filter.contains("evolution")) {
      // Avro - Evolution widening types
      runScenario(brokers, zookeepers, schemaregistry, "generator-evolution-widen", Generator.EVOLUTION_WIDEN_INITIAL);
      runScenario(brokers, zookeepers, schemaregistry, "generator-evolution-widen", Generator.EVOLUTION_WIDEN_TOLONG);

      // Avro - Evolution adding new fields
      runScenario(brokers, zookeepers, schemaregistry, "generator-evolution-add", Generator.EVOLUTION_INITIAL);
      runScenario(brokers, zookeepers, schemaregistry, "generator-evolution-add", Generator.EVOLUTION_ADD1);
      runScenario(brokers, zookeepers, schemaregistry, "generator-evolution-add", Generator.EVOLUTION_ADD2);
      runScenario(brokers, zookeepers, schemaregistry, "generator-evolution-add", Generator.EVOLUTION_ADD3);
    }
    if (filter.equals("") || filter.contains("ecommerce")) {
      // Avro - E-commerce scenario
      runEcommerce(brokers, zookeepers, schemaregistry, "generator-shipments", "generator-sales", Generator.ECOMMERCE_SHIPMENTS, Generator.ECOMMERCE_SALES);
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2)
      throw new IllegalArgumentException("Requires 2 arguments <number of messages> and <partitions>");

    int messages = Integer.parseInt(args[0]);
    int partitions = Integer.parseInt(args[1]);
    String filter = args[2];

    String brokers = System.getenv("BROKERS");
    String zookeepers = System.getenv("ZK");
    String schemaregistry = System.getenv("SCHEMA_REGISTRY");

    if (brokers == null || zookeepers == null | schemaregistry == null)
      throw new IllegalArgumentException("Please set environment variables for 'BROKERS', 'ZK', 'SCHEMA_REGISTRY' \n" +
              " export BROKERS='broker.landoop.com:29092'\n export ZK='zookeeper.landoop.com:22181'\n export SCHEMA_REGISTRY='http://schema_registry.landoop.com:28081'\n".replace("'", "\""));

    log.info("Running <landoop-avro-generator> generating " + messages + " messages on " + partitions + " partitions");
    log.info("The following topics will be generated [" +
            "generator-text " +
            "generator-types " +
            "generator-types-upsert " +
            "generator-sql " +
            "generator-evolution-widen " +
            "generator-evolution-add " +
            "generator-shipments " +
            "generator-sales]");

    new Run(messages, partitions, brokers, zookeepers, schemaregistry, filter);
  }

  private void runScenario(String brokers, String zookeepers, String schemaregistry, String topicName, Generator avromessageType) {
    log.info("Running scenario for topic : " + topicName);
    AvroProducer avroGenerator = new AvroProducer(brokers, schemaregistry);
    KafkaTools.createTopic(zookeepers, topicName, partitions, 1);
    avroGenerator.sendMessages(messages, topicName, avromessageType);
  }

  private void runEcommerce(String brokers, String zookeepers, String schemaregistry, String topicShipments, String topicSales,
                            Generator shipmentMessage, Generator salesMessage) {
    AvroProducer avroGenerator = new AvroProducer(brokers, schemaregistry);
    KafkaTools.createTopic(zookeepers, topicShipments, partitions, 1);
    KafkaTools.createTopic(zookeepers, topicSales, partitions, 1);
    avroGenerator.sendEcommerceMessages(messages, topicShipments, topicSales, shipmentMessage, salesMessage);
  }

}