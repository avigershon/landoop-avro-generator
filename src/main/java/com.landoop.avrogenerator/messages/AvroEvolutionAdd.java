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

// Schema Evolution `Adding new fields`
class AvroEvolutionAdd {

  static String EVOLUTION_INITIAL_SCHEMA = "{`type`:`record`,`name`:`com.landoop.EvolutionAdd`," +
          "`doc`:`Initial record contains a single string field 'text'`," +
          "`fields`:[" +
          "{`name`:`text`,`type`:`string`}" +
          "]}";

  static String EVOLUTION_ADD1_SCHEMA = "{`type`:`record`,`name`:`com.landoop.Evolution`," +
          "`doc`:`Initial record contains a single string field 'text'`," +
          "`fields`:[" +
          "{`name`:`text`,`type`:`string`}," +
          "{`name`:`flag`,`type`:`boolean`,`default`:true}" + // New boolean field
          "]}";

  static String EVOLUTION_ADD2_SCHEMA = "{`type`:`record`,`name`:`com.landoop.Evolution`," +
          "`doc`:`A set of 50 character records, followed by a set of 100 characters text`," +
          "`fields`:[" +
          "{`name`:`text`,`type`:`string`}," +
          "{`name`:`flag`,`type`:`boolean`,`default`:true}" +
          "{`name`:`number1`,`type`:`int`,`default`:0}," + // New integer field
          "]}";

  static String EVOLUTION_ADD3_SCHEMA = "{`type`:`record`,`name`:`com.landoop.Evolution`," +
          "`doc`:`A set of 50 character records, followed by a set of 100 characters text`," +
          "`fields`:[" +
          "{`name`:`text`,`type`:`string`}," +
          "{`name`:`flag`,`type`:`boolean`,`default`:`true`}" +
          "{`name`:`number1`,`type`:`int`,`default`:0}," +
          "{`name`:`number1`,`type`:`float`,`default`:0}," + // New float field
          "]}";

}
