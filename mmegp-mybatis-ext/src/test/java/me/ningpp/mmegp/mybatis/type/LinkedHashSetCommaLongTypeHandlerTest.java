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

import me.ningpp.mmegp.mybatis.type.set.LinkedHashSetCommaLongTypeHandler;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LinkedHashSetCommaLongTypeHandlerTest extends BaseTypeHandlerCase {

    LinkedHashSetCommaLongTypeHandler typeHandler = new LinkedHashSetCommaLongTypeHandler();

    @Test
    void setParameterTest() throws SQLException {
        typeHandler.setParameter(ps, 1, new LinkedHashSet<>(List.of(222L, 311L, 17L)), null);
        verify(ps).setString(1, "222,311,17");
    }

    @Test
    void getNullableResultByNameTest() throws SQLException {
        when(rs.getString("ids")).thenReturn(null);
        LinkedHashSet<Long> ids = typeHandler.getResult(rs, "ids");
        assertTrue(ids.getClass().isAssignableFrom(LinkedHashSet.class));

        when(rs.getString("ids2")).thenReturn("");
        LinkedHashSet<Long> ids2 = typeHandler.getResult(rs, "ids2");
        assertTrue(ids2.isEmpty());

        when(rs.getString("ids3")).thenReturn("3,1,3,2,3");
        LinkedHashSet<Long> ids3 = typeHandler.getResult(rs, "ids3");
        assertArrayEquals(new Long[]{3L, 1L, 2L}, ids3.toArray(new Long[0]));
    }

    @Test
    void getNullableResultByIndexTest() throws SQLException {
        when(rs.getString(1)).thenReturn(null);
        LinkedHashSet<Long> ids = typeHandler.getResult(rs, 1);
        assertTrue(ids.isEmpty());

        when(rs.getString(1)).thenReturn("");
        LinkedHashSet<Long> ids2 = typeHandler.getResult(rs, 1);
        assertTrue(ids2.isEmpty());

        when(rs.getString(1)).thenReturn("1,2,3,2");
        LinkedHashSet<Long> ids3 = typeHandler.getResult(rs, 1);
        assertArrayEquals(new Long[]{1L,2L,3L}, ids3.toArray(new Long[0]));
    }

    @Test
    void getNullableResultCallableStatementTest() throws SQLException {
        when(cs.getString(1)).thenReturn(null);
        LinkedHashSet<Long> ids = typeHandler.getResult(cs, 1);
        assertTrue(ids.isEmpty());

        when(cs.getString(1)).thenReturn("");
        LinkedHashSet<Long> ids2 = typeHandler.getResult(cs, 1);
        assertTrue(ids2.isEmpty());

        when(cs.getString(1)).thenReturn("3,1,3,2,3");
        LinkedHashSet<Long> ids3 = typeHandler.getResult(cs, 1);
        assertArrayEquals(new Long[]{3L,1L,2L}, ids3.toArray(new Long[0]));
    }

}
