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
package me.ningpp.mmegp.mybatis.type;

import me.ningpp.mmegp.mybatis.type.set.LinkedHashSetCommaIntegerTypeHandler;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LinkedHashSetCommaIntegerTypeHandlerTest extends BaseTypeHandlerTest {

    LinkedHashSetCommaIntegerTypeHandler typeHandler = new LinkedHashSetCommaIntegerTypeHandler();

    @Test
    void setParameterTest() throws SQLException {
        typeHandler.setParameter(ps, 1, new LinkedHashSet<>(List.of(3, 1)), null);
        verify(ps).setString(1, "3,1");
    }

    @Test
    public void getNullableResultByNameTest() throws SQLException {
        when(rs.getString("ids")).thenReturn(null);
        LinkedHashSet<Integer> ids = typeHandler.getResult(rs, "ids");
        assertTrue(ids.isEmpty());

        when(rs.getString("ids2")).thenReturn("");
        LinkedHashSet<Integer> ids2 = typeHandler.getResult(rs, "ids2");
        assertTrue(ids2.isEmpty());

        when(rs.getString("ids3")).thenReturn("3,1,1,2,1,3");
        LinkedHashSet<Integer> ids3 = typeHandler.getResult(rs, "ids3");
        assertArrayEquals(new Integer[]{3, 1, 2}, ids3.toArray(new Integer[0]));
    }

    @Test
    public void getNullableResultByIndexTest() throws SQLException {
        when(rs.getString(1)).thenReturn(null);
        LinkedHashSet<Integer> ids = typeHandler.getResult(rs, 1);
        assertTrue(ids.isEmpty());

        when(rs.getString(1)).thenReturn("");
        LinkedHashSet<Integer> ids2 = typeHandler.getResult(rs, 1);
        assertTrue(ids2.isEmpty());

        when(rs.getString(1)).thenReturn("1,1,2,1,3");
        LinkedHashSet<Integer> ids3 = typeHandler.getResult(rs, 1);
        assertArrayEquals(new Integer[]{1, 2, 3}, ids3.toArray(new Integer[0]));
    }

    @Test
    public void getNullableResultCallableStatementTest() throws SQLException {
        when(cs.getString(1)).thenReturn(null);
        LinkedHashSet<Integer> ids = typeHandler.getResult(cs, 1);
        assertTrue(ids.isEmpty());

        when(cs.getString(1)).thenReturn("");
        LinkedHashSet<Integer> ids2 = typeHandler.getResult(cs, 1);
        assertTrue(ids2.isEmpty());

        when(cs.getString(1)).thenReturn("1,2,1,3");
        LinkedHashSet<Integer> ids3 = typeHandler.getResult(cs, 1);
        assertArrayEquals(new Integer[]{1, 2, 3}, ids3.toArray(new Integer[0]));
    }
}
