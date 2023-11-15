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

import me.ningpp.mmegp.mybatis.type.uuid.UUIDTypeHandler;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UUIDTypeHandlerTest extends BaseTypeHandlerTest {

    UUIDTypeHandler typeHandler = new UUIDTypeHandler();
    byte[] bytes = UUIDTypeHandler.toBytes(UUID.randomUUID());

    @Test
    void setParameterTest() throws SQLException {
        typeHandler.setParameter(ps, 1, UUIDTypeHandler.fromBytes(bytes), null);
        verify(ps).setBytes(1, bytes);
    }

    @Test
    void getNullableResultByNameTest() throws SQLException {
        when(rs.getBytes("uuid")).thenReturn(null);
        UUID uuid = typeHandler.getResult(rs, "uuid");
        assertNull(uuid);

        when(rs.getBytes("uuid2")).thenReturn(new byte[0]);
        UUID uuid2 = typeHandler.getResult(rs, "uuid2");
        assertNull(uuid2);

        when(rs.getBytes("uuid3")).thenReturn(bytes);
        UUID uuid3 = typeHandler.getResult(rs, "uuid3");
        assertArrayEquals(bytes, UUIDTypeHandler.toBytes(uuid3));
    }

    @Test
    void getNullableResultByIndexTest() throws SQLException {
        when(rs.getBytes(1)).thenReturn(null);
        UUID uuid = typeHandler.getResult(rs, 1);
        assertNull(uuid);

        when(rs.getBytes(1)).thenReturn(new byte[0]);
        UUID uuid2 = typeHandler.getResult(rs, 1);
        assertNull(uuid2);

        when(rs.getBytes(1)).thenReturn(bytes);
        UUID uuid3 = typeHandler.getResult(rs, 1);
        assertArrayEquals(bytes, UUIDTypeHandler.toBytes(uuid3));
    }

    @Test
    void getNullableResultCallableStatementTest() throws SQLException {
        when(cs.getBytes(1)).thenReturn(null);
        UUID uuid = typeHandler.getResult(cs, 1);
        assertNull(uuid);

        when(cs.getBytes(1)).thenReturn(new byte[0]);
        UUID uuid2 = typeHandler.getResult(cs, 1);
        assertNull(uuid2);

        when(cs.getBytes(1)).thenReturn(bytes);
        UUID uuid3 = typeHandler.getResult(cs, 1);
        assertArrayEquals(bytes, UUIDTypeHandler.toBytes(uuid3));
    }

}
