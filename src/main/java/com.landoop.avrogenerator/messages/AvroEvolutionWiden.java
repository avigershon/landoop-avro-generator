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

public class AvroEvolutionWiden {

  static String EVOLUTION_WIDEN_INITIAL_SCHEMA = "{`type`:`record`,`name`:`com.landoop.EvolutionWiden_v1`," +
          "`doc`:`Avro schema that demonstrates Avro type widening evolution capabilities`," +
          "`fields`:[" +
          "{`name`:`name`,`type`:`string`}," +
          "{`name`:`number1`,`type`:`long`}," +
          "{`name`:`number2`,`type`:`float`, `doc`:`this field will become a double`}" + // -> to double
          "]}";

  static String EVOLUTION_WIDEN_TOLONG_SCHEMA = "{`type`:`record`,`name`:`com.landoop.EvolutionWiden_v2`," +
          "`doc`:`Avro schema that demonstrates Avro type widening evolution capabilities`," +
          "`fields`:[" +
          "{`name`:`name`,`type`:`string`}," +
          "{`name`:`number1`,`type`:`long`}," +
          "{`name`:`number2`,`type`:`double`,`doc`:`this field was a float once`}" +
          "]}";

  // "{`name`:`text`,`type`:[`string`,`null`],`default`: ``}" +

}
