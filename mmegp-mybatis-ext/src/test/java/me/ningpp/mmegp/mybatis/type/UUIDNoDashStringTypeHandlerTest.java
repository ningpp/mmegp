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

import me.ningpp.mmegp.mybatis.type.uuid.UUIDNoDashStringTypeHandler;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UUIDNoDashStringTypeHandlerTest extends BaseTypeHandlerCase {

    UUIDNoDashStringTypeHandler typeHandler = new UUIDNoDashStringTypeHandler();
    UUID nodashUuid = UUID.randomUUID();

    @Test
    void setParameterTest() throws SQLException {
        typeHandler.setParameter(ps, 1, nodashUuid, null);
        verify(ps).setString(1, nodashUuid.toString().replace("-", ""));
    }

    @Test
    void getNullableResultByNameTest() throws SQLException {
        when(rs.getString("uuid")).thenReturn(null);
        UUID uuid = typeHandler.getResult(rs, "uuid");
        assertNull(uuid);

        when(rs.getString("uuid2")).thenReturn("");
        UUID uuid2 = typeHandler.getResult(rs, "uuid2");
        assertNull(uuid2);

        when(rs.getString("uuid3")).thenReturn(nodashUuid.toString().replace("-", ""));
        UUID uuid3 = typeHandler.getResult(rs, "uuid3");
        assertEquals(nodashUuid, uuid3);
    }

    @Test
    void getNullableResultByIndexTest() throws SQLException {
        when(rs.getString(1)).thenReturn(null);
        UUID uuid = typeHandler.getResult(rs, 1);
        assertNull(uuid);

        when(rs.getString(1)).thenReturn("");
        UUID uuid2 = typeHandler.getResult(rs, 1);
        assertNull(uuid2);

        when(rs.getString(1)).thenReturn(nodashUuid.toString().replace("-", ""));
        UUID uuid3 = typeHandler.getResult(rs, 1);
        assertEquals(nodashUuid, uuid3);
    }

    @Test
    void getNullableResultCallableStatementTest() throws SQLException {
        when(cs.getString(1)).thenReturn(null);
        UUID uuid = typeHandler.getResult(cs, 1);
        assertNull(uuid);

        when(cs.getString(1)).thenReturn("");
        UUID uuid2 = typeHandler.getResult(cs, 1);
        assertNull(uuid2);

        when(cs.getString(1)).thenReturn(nodashUuid.toString().replace("-", ""));
        UUID uuid3 = typeHandler.getResult(cs, 1);
        assertEquals(nodashUuid, uuid3);
    }

}
