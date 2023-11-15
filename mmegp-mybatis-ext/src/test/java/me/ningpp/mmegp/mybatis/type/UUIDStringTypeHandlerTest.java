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

import me.ningpp.mmegp.mybatis.type.uuid.UUIDStringTypeHandler;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UUIDStringTypeHandlerTest extends BaseTypeHandlerTest {

    UUIDStringTypeHandler typeHandler = new UUIDStringTypeHandler();
    UUID strUuid = UUID.randomUUID();

    @Test
    void setParameterTest() throws SQLException {
        typeHandler.setParameter(ps, 1, strUuid, null);
        verify(ps).setString(1, strUuid.toString());
    }

    @Test
    void getNullableResultByNameTest() throws SQLException {
        when(rs.getString("uuid")).thenReturn(null);
        UUID uuid = typeHandler.getResult(rs, "uuid");
        assertNull(uuid);

        when(rs.getString("uuid2")).thenReturn("");
        UUID uuid2 = typeHandler.getResult(rs, "uuid2");
        assertNull(uuid2);

        when(rs.getString("uuid3")).thenReturn(strUuid.toString());
        UUID uuid3 = typeHandler.getResult(rs, "uuid3");
        assertEquals(strUuid.toString(), uuid3.toString());
    }

    @Test
    void getNullableResultByIndexTest() throws SQLException {
        when(rs.getString(1)).thenReturn(null);
        UUID uuid = typeHandler.getResult(rs, 1);
        assertNull(uuid);

        when(rs.getString(1)).thenReturn("");
        UUID uuid2 = typeHandler.getResult(rs, 1);
        assertNull(uuid2);

        when(rs.getString(1)).thenReturn(strUuid.toString());
        UUID uuid3 = typeHandler.getResult(rs, 1);
        assertEquals(strUuid.toString(), uuid3.toString());
    }

    @Test
    void getNullableResultCallableStatementTest() throws SQLException {
        when(cs.getString(1)).thenReturn(null);
        UUID uuid = typeHandler.getResult(cs, 1);
        assertNull(uuid);

        when(cs.getString(1)).thenReturn("");
        UUID uuid2 = typeHandler.getResult(cs, 1);
        assertNull(uuid2);

        when(cs.getString(1)).thenReturn(strUuid.toString());
        UUID uuid3 = typeHandler.getResult(cs, 1);
        assertEquals(strUuid.toString(), uuid3.toString());
    }

}
