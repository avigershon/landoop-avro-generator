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
package com.landoop.avrogenerator.messages;

public class AvroBasic {

  static String TEXT_SCHEMA = "{`type`:`record`,`name`:`com.landoop.text`," +
          "`doc`:`A set of 50 character records, followed by a set of 100 characters text`," +
          "`fields`:[{`name`:`text`,`type`:`string`}]}";

  static String AVROTYPES_SCHEMA = "{`type`:`record`,`name`:`com.landoop.AvroTypes`," +
          "`doc`:`This record contains most basic Avro types : string, boolean, int, long, float, double`," +
          "`fields`:[" +
          "{`name`:`text`,`type`:`string`}," +
          "{`name`:`flag`,`type`:`boolean`}," +
          "{`name`:`integer8`,`type`:`int`}," +
          "{`name`:`integer16`,`type`:`int`}," +
          "{`name`:`integer32`,`type`:`long`}," +
          "{`name`:`integer64`,`type`:`long`}," +
          "{`name`:`float32`,`type`:`float`}," +
          "{`name`:`float64`,`type`:`double`}" +
          "]}";

  static String SQL_RESERVED_WORDS_SCHEMA = "{`type`:`record`,`name`:`com.landoop.reservedSQLwords`," +
          "`doc`:`This avro record contains two fields AS (text) and FROM (int). The generator creates text data with random strings such as: SELECT * FROM TABLE1. This synthetic data generation is for testing, evaluating and certifying Kafka Connect JDBC sinks`," +
          "`fields`:[{`name`:`as`,`type`:`string`},{`name`:`from`,`type`:`int`}]}";

}
