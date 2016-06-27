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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Run {

  private static final Logger log = LoggerFactory.getLogger(Run.class);

  private int messages, partitions;

  private Run(int messages, int partitions, String brokers, String zookeepers, String schemaregistry) {
    this.messages = messages;
    this.partitions = partitions;

    runScenario(brokers, zookeepers, schemaregistry, "demo-simple", AllAvroMessages.SIMPLE);
    runScenario(brokers, zookeepers, schemaregistry, "demo-simple100", AllAvroMessages.SIMPLE100);
    runScenario(brokers, zookeepers, schemaregistry, "demo-person", AllAvroMessages.PERSON);
    runScenario(brokers, zookeepers, schemaregistry, "demo-person-1pc", AllAvroMessages.UPSERT_PERSON_1PC);
    runScenario(brokers, zookeepers, schemaregistry, "demo-evolution", AllAvroMessages.EVOLUTION);
    runScenario(brokers, zookeepers, schemaregistry, "demo-evolution", AllAvroMessages.EVOLUTION_ADD_TEXT);
    runScenario(brokers, zookeepers, schemaregistry, "demo-sql-inject", AllAvroMessages.SQL_INJECTION);
    runScenario(brokers, zookeepers, schemaregistry, "demo-reserved", AllAvroMessages.RESERVED_SQL_WORDS);
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2)
      throw new IllegalArgumentException("Requires 2 arguments <number of messages> and <partitions>");

    int messages = Integer.parseInt(args[0]);
    int partitions = Integer.parseInt(args[1]);

    String brokers = System.getenv("BROKERS");
    String zookeepers = System.getenv("ZK");
    String schemaregistry = System.getenv("SCHEMA_REGISTRY");

    if (brokers == null || zookeepers == null | schemaregistry == null)
      throw new IllegalArgumentException("Please set environment variables for 'BROKERS', 'ZK', 'SCHEMA_REGISTRY' \n" +
              " export BROKERS='broker.landoop.com:29092'\n export ZK='zookeeper.landoop.com:22181'\n export SCHEMA_REGISTRY='http://schema_registry.landoop.com:28081'\n".replace("'", "\""));

    log.info("Running <landoop-avro-generator> generating " + messages + " messages on " + partitions + " partitions");
    log.info("The following topics will be generated : demo-simple , demo-simple100, demo-person, demo-person-1pc, demo-evolution, demo-sql-inject, demo-reserved");

    new Run(messages, partitions, brokers, zookeepers, schemaregistry);
  }

  private void runScenario(String brokers, String zookeepers, String schemaregistry, String topicName, AllAvroMessages avromessageType) {
    AvroProducer avroGenerator = new AvroProducer(brokers, schemaregistry);
    KafkaTools.createTopic(zookeepers, topicName, partitions, 1);
    avroGenerator.sendMessages(messages, topicName, avromessageType);
  }

}