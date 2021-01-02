/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package flinkCEP.cases;

import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import flinkCEP.events.Event;
import flinkCEP.events.Generate;

// Automatic pattern generation and processing
public class CEPCase_Generate {

    public static void main (String[] args) throws Exception {
        boolean finished = false;

        // Set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Set parallelism to 1
        env.setParallelism(1);

        // Create input sequence
        DataStream<Event> input = env.fromCollection(Generate.createInput("/home/eleni/seq.txt"));

        // Set wanted pattern and contiguity condition
        // (1 = strict, 2 = relaxed, 3 = non deterministic relaxed)
        Generate wanted = new Generate("a b{1,2} c", 2);

        // Set after match skip strategy
        // (1 = no skip, 2 = skip to next, 3 = skip past last event, 4 = skip to first, 5 = skip to last)
        wanted.setStrategy(1, ""); // no skip

        // Create wanted pattern
        Pattern<Event, ?> pattern = wanted.createPattern();

        // Print info
        env.fromElements(wanted.toString()).print();

        PatternStream<Event> patternStream = CEP.pattern(input, pattern);

        // Create result with matches
        DataStream<String> result = wanted.createResult(patternStream);

        // Print matches
        result.print();

        env.execute("Flink CEP Pattern Detection Automation");
    }
}


