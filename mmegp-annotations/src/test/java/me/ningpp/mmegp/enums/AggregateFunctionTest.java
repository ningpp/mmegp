/*
 *    Copyright 2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package me.ningpp.mmegp.enums;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class AggregateFunctionTest {

    @Test
    void parseArrayTest() {
        assertTrue(AggregateFunction.parseArray(null).isEmpty());
        assertTrue(AggregateFunction.parseArray("").isEmpty());
        assertTrue(AggregateFunction.parseArray("ABC").isEmpty());
        assertArrayEquals(new AggregateFunction[] {
            AggregateFunction.COUNT, AggregateFunction.SUM, AggregateFunction.MAX, AggregateFunction.MIN},
                AggregateFunction.parseArray("COUNT, SUM,  MAX, MIN,ABC").toArray(new AggregateFunction[0]));
    }

    @Test
    void parseTest() {
        assertNull(AggregateFunction.parse(null));
        assertNull(AggregateFunction.parse(""));
        assertNull(AggregateFunction.parse("abc"));
        assertNull(AggregateFunction.parse(UUID.randomUUID().toString()));

        assertEquals(AggregateFunction.MIN, AggregateFunction.parse("min"));
        assertEquals(AggregateFunction.MIN, AggregateFunction.parse("MIN"));
        assertEquals(AggregateFunction.MIN, AggregateFunction.parse("Min"));

        assertEquals(AggregateFunction.MAX, AggregateFunction.parse("max"));
        assertEquals(AggregateFunction.MAX, AggregateFunction.parse("MAX"));
        assertEquals(AggregateFunction.MAX, AggregateFunction.parse("Max"));

        assertEquals(AggregateFunction.SUM, AggregateFunction.parse("sum"));
        assertEquals(AggregateFunction.SUM, AggregateFunction.parse("SUM"));
        assertEquals(AggregateFunction.SUM, AggregateFunction.parse("Sum"));

        assertEquals(AggregateFunction.AVG, AggregateFunction.parse("avg"));
        assertEquals(AggregateFunction.AVG, AggregateFunction.parse("AVG"));
        assertEquals(AggregateFunction.AVG, AggregateFunction.parse("Avg"));
    }

}
