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

import org.apache.avro.Schema;

public enum AllAvroMessages {

  // My enumerations
  SIMPLE(SIMPLE_SCHEMA()),
  SIMPLE100(SIMPLE_SCHEMA()),
  PERSON(PERSON_SCHEMA()),
  EVOLUTION(EVOLUTION_SCHEMA()),
  EVOLUTION_ADD_TEXT(EVOLUTION_SCHEMA_1_ADD_TEXT()),
  SQL_INJECTION(SIMPLE_SCHEMA()),
  RESERVED_SQL_WORDS(RESERVED_SQL_WORDS()),
  UPSERT_PERSON_1PC(PERSON_SCHEMA());

  private final Schema schema;

  AllAvroMessages(Schema schema) {
    this.schema = schema;
  }

  public Schema getSchema() {
    return schema;
  }

  public static Schema PERSON_SCHEMA() {
    String schemaString = "{`type`:`record`,`name`:`com.landoop.Person`,`fields`:[" +
            "{`name`:`name`,`type`:`string`}," +
            "{`name`:`adult`,`type`:`boolean`}," +
            "{`name`:`integer8`,`type`:`int`}," +
            "{`name`:`integer16`,`type`:`int`}," +
            "{`name`:`integer32`,`type`:`long`}," +
            "{`name`:`integer64`,`type`:`long`}," +
            "{`name`:`float32`,`type`:`float`}," +
            "{`name`:`float64`,`type`:`double`}" +
            "]}";
    org.apache.avro.Schema.Parser parser = new org.apache.avro.Schema.Parser();
    return parser.parse(schemaString.replace('`', '"'));
  }

  public static Schema EVOLUTION_SCHEMA() {
    String schemaString = "{`type`:`record`,`name`:`com.landoop.Evolution`,`fields`:[" +
            "{`name`:`name`,`type`:`string`}," +
            "{`name`:`number1`,`type`:`int`}," + // -> to long
            "{`name`:`number2`,`type`:`float`}" + // -> to double
            "]}";
    org.apache.avro.Schema.Parser parser = new org.apache.avro.Schema.Parser();
    return parser.parse(schemaString.replace('`', '"'));
  }

  public static Schema EVOLUTION_SCHEMA_1_ADD_TEXT() {
    String schemaString = "{`type`:`record`,`name`:`com.landoop.Evolution`,`fields`:[" +
            "{`name`:`name`,`type`:`string`}," +
            "{`name`:`number1`,`type`:`int`}," +
            "{`name`:`number2`,`type`:`float`}," +
            "{`name`:`text`,`type`:[`string`,`null`],`default`: ``}" +
            "]}";
    org.apache.avro.Schema.Parser parser = new org.apache.avro.Schema.Parser();
    return parser.parse(schemaString.replace('`', '"'));
  }

  /*
  public static Schema EVOLUTION_WIDEN1_SCHEMA() {
    String schemaString = "{`type`:`record`,`name`:`com.landoop.Evolution`,`fields`:[" +
            "{`name`:`name`,`type`:`string`}," +
            "{`name`:`number1`,`type`:`long`}," +
            "{`name`:`number2`,`type`:`float`}" + // -> to double
            "]}";
    org.apache.avro.Schema.Parser parser = new org.apache.avro.Schema.Parser();
    return parser.parse(schemaString.replace('`', '"'));
  }

  public static Schema EVOLUTION_WIDEN2_SCHEMA() {
    String schemaString = "{`type`:`record`,`name`:`com.landoop.Evolution`,`fields`:[" +
            "{`name`:`name`,`type`:`string`}," +
            "{`name`:`number1`,`type`:`long`}," +
            "{`name`:`number2`,`type`:`double`}" +
            "]}";
    org.apache.avro.Schema.Parser parser = new org.apache.avro.Schema.Parser();
    return parser.parse(schemaString.replace('`', '"'));
  }

  public static Schema EVOLUTION_WIDEN3_SCHEMA() {
    String schemaString = "{`type`:`record`,`name`:`com.landoop.Evolution`,`fields`:[" +
            "{`name`:`name`,`type`:`string`}," +
            "{`name`:`number1`,`type`:`long`}," +
            "{`name`:`number2`,`type`:`double`}," +
            "{`name`:`text`,`type`:[`string`,`null`],`default`: ``}" +
            "]}";
    org.apache.avro.Schema.Parser parser = new org.apache.avro.Schema.Parser();
    return parser.parse(schemaString.replace('`', '"'));
  }
  */

  public static Schema SIMPLE_SCHEMA() {
    String schema = "{`type`:`record`,`name`:`com.landoop.simple`,`fields`:[{`name`:`text`,`type`:`string`}]}".replace('`', '"');
    Schema.Parser parser = new Schema.Parser();
    return parser.parse(schema);
  }

  public static Schema RESERVED_SQL_WORDS() {
    String schema = "{`type`:`record`,`name`:`com.landoop.CREATE`,`fields`:[{`name`:`as`,`type`:`string`},{`name`:`from`,`type`:`int`}]}".replace('`', '"');
    Schema.Parser parser = new Schema.Parser();
    return parser.parse(schema);
  }

}
