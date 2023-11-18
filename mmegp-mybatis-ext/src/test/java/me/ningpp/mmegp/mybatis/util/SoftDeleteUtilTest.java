/*
 *    Copyright 2021-2023 the original author or authors.
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
package me.ningpp.mmegp.mybatis.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoftDeleteUtilTest {

    @Test
    void test() {
        assertEquals((byte)1, SoftDeleteUtil.byteValue("1"));
        assertEquals((byte)1, SoftDeleteUtil.empty2ByteValue(null, "1"));
        assertEquals((byte)3, SoftDeleteUtil.empty2ByteValue((byte)3, "1"));

        assertEquals('1', SoftDeleteUtil.characterValue("1"));
        assertThrows(IllegalArgumentException.class, () -> SoftDeleteUtil.characterValue(null));
        assertThrows(IllegalArgumentException.class, () -> SoftDeleteUtil.characterValue("111"));
        assertEquals('N', SoftDeleteUtil.empty2CharacterValue(null, "N"));
        assertEquals('Y', SoftDeleteUtil.empty2CharacterValue('Y', "1"));

        assertTrue(SoftDeleteUtil.booleanValue("true"));
        assertTrue(SoftDeleteUtil.empty2BooleanValue(null, "true"));
        assertFalse(SoftDeleteUtil.empty2BooleanValue(null, "false"));
        assertFalse(SoftDeleteUtil.empty2BooleanValue(false, "true"));
        assertTrue(SoftDeleteUtil.empty2BooleanValue(true, "false"));

        assertEquals((short)1, SoftDeleteUtil.shortValue("1"));
        assertEquals((short)1, SoftDeleteUtil.empty2ShortValue(null, "1"));
        assertEquals((short)3, SoftDeleteUtil.empty2ShortValue((short)3, "1"));

        assertEquals(1, SoftDeleteUtil.integerValue("1"));
        assertEquals(1, SoftDeleteUtil.empty2IntegerValue(null, "1"));
        assertEquals(311, SoftDeleteUtil.empty2IntegerValue(null, "311"));
        assertEquals(3, SoftDeleteUtil.empty2IntegerValue(3, "1"));

        assertEquals(1L, SoftDeleteUtil.longValue("1"));
        assertEquals(1L, SoftDeleteUtil.empty2LongValue(null, "1"));
        assertEquals(3L, SoftDeleteUtil.empty2LongValue(3L, "1"));

        assertEquals("1", SoftDeleteUtil.stringValue("1"));
        assertEquals("1", SoftDeleteUtil.empty2StringValue(null, "1"));
        assertEquals("1", SoftDeleteUtil.empty2StringValue("", "1"));
        assertEquals("3", SoftDeleteUtil.empty2StringValue("3", "1"));
    }

}
