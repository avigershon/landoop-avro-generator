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

import org.apache.avro.Schema;

public enum Generator {

  /***************************************************************************
   **      Basic Avro : text , all types, upserts , sql reserved words      **
   ***************************************************************************/
  // `text` with 50 & 100 char string
  TEXT50(getSchema(AvroBasic.TEXT_SCHEMA)),
  TEXT100(getSchema(AvroBasic.TEXT_SCHEMA)),
  // Basic Avro types : string, boolean, int, long, float, double
  AVRO_TYPES(getSchema(AvroBasic.AVROTYPES_SCHEMA)),
  // 1% of the records contain the same value in fild `text` - that if used as a primary key - can test UPSERT capabilities in Kafka Connectors that support UPSERT mode.
  AVRO_TYPES_UPSERT(getSchema(AvroBasic.AVROTYPES_SCHEMA)),
  // Contains reserved SQL words as fields and in contents
  SQL_RESERVED_WORDS(getSchema(AvroBasic.SQL_RESERVED_WORDS_SCHEMA)),

  /***************************************************************************
   **      Avro Evolution : Adding new fields with default values           **
   ***************************************************************************/
  EVOLUTION_INITIAL(getSchema(AvroEvolutionAdd.EVOLUTION_INITIAL_SCHEMA)),
  EVOLUTION_ADD1(getSchema(AvroEvolutionAdd.EVOLUTION_ADD1_SCHEMA)),
  EVOLUTION_ADD2(getSchema(AvroEvolutionAdd.EVOLUTION_ADD2_SCHEMA)),
  EVOLUTION_ADD3(getSchema(AvroEvolutionAdd.EVOLUTION_ADD3_SCHEMA)),

  /***************************************************************************
   **      Avro Evolution : Widening existing fields                        **
   ***************************************************************************/
  EVOLUTION_WIDEN_INITIAL(getSchema(AvroEvolutionWiden.EVOLUTION_WIDEN_INITIAL_SCHEMA)),
  EVOLUTION_WIDEN_TOLONG(getSchema(AvroEvolutionWiden.EVOLUTION_WIDEN_TOLONG_SCHEMA)),

  /***************************************************************************
   **      Ecommerce Avro : Example for 'inventory-in-hand' scenario        **
   ***************************************************************************/
  ECOMMERCE_SHIPMENTS(getSchema(AvroEcommerce.SHIPMENT_SCHEMA)),
  ECOMMERCE_SALES(getSchema(AvroEcommerce.SALES_SCHEMA)),
  ECOMMERCE_SALES2(getSchema(AvroEcommerce.SALES_SCHEMA2));


  private final Schema schema;

  Generator(Schema schema) {
    this.schema = schema;
  }

  public static Schema getSchema(String schemaString) {
    Schema.Parser parser = new Schema.Parser();
    //System.out.println("Parsing " + schemaString);
    return parser.parse(schemaString.replace('`', '"'));
  }

  public Schema getSchema() {
    return schema;
  }

}
