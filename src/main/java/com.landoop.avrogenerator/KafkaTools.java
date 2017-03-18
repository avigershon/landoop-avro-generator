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

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.Properties;

import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KafkaTools {

  private static final Logger log = LoggerFactory.getLogger(KafkaTools.class);

  static void createTopic(String zookeepers, String topicName, int noOfPartitions, int noOfReplication) {
    ZkClient zkClient = null;
    int sessionTimeOutInMs = 3 * 1000;
    int connectionTimeOutInMs = 3 * 1000;
    try {
      log.info("Creating topic [ " + topicName + " ] with " + noOfPartitions + " partitions and " + noOfReplication + " replication");
      log.info("Using zookeeper [ " + zookeepers + " ]");
      // Note: You must initialize the ZkClient with ZKStringSerializer.  If you don't, then
      // createTopic() will only seem to work (it will return without error).  The topic will exist in
      // only ZooKeeper and will be returned when listing topics, but Kafka itself does not create the
      // topic.
      zkClient = new ZkClient(zookeepers, sessionTimeOutInMs, connectionTimeOutInMs, ZKStringSerializer$.MODULE$);
      // Security for Kafka was added in Kafka 0.9.0.0
      boolean isSecureKafkaCluster = false;
      ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeepers), isSecureKafkaCluster);
      Properties topicConfiguration = new Properties();
      AdminUtils.createTopic(zkUtils, topicName, noOfPartitions, noOfReplication, topicConfiguration, RackAwareMode.Disabled$.MODULE$);
    } catch (TopicExistsException tex) {
      // TODO - Display current topic metadata ( partitions, replication )
      log.info("Topic already exists");
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      if (zkClient != null) {
        zkClient.close();
      }
    }
  }
}