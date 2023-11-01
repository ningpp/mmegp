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
package me.ningpp.mmegp.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UUIDTypeHandler extends BaseTypeHandler<UUID> {

    public static byte[] toBytes(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        byte[] bytes = new byte[16];
        toBytes(uuid.getMostSignificantBits(), bytes, 0);
        toBytes(uuid.getLeastSignificantBits(), bytes, 8);
        return bytes;
    }

    public static UUID fromBytes(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new UUID(toLong(bytes, 0 ), toLong(bytes, 8 ));
    }

    public static long toLong(byte[] readBuffer, int start) {
        return (((long) readBuffer[start] << 56) +
                ((long) (readBuffer[start+1] & 255) << 48) +
                ((long) (readBuffer[start+2] & 255) << 40) +
                ((long) (readBuffer[start+3] & 255) << 32) +
                ((long) (readBuffer[start+4] & 255) << 24) +
                ((readBuffer[start+5] & 255) << 16) +
                ((readBuffer[start+6] & 255) << 8) +
                ((readBuffer[start+7] & 255) << 0));
    }

    private static void toBytes(long v, byte[] bytes, int start) {
        bytes[start] = (byte)(v >>> 56);
        bytes[start+1] = (byte)(v >>> 48);
        bytes[start+2] = (byte)(v >>> 40);
        bytes[start+3] = (byte)(v >>> 32);
        bytes[start+4] = (byte)(v >>> 24);
        bytes[start+5] = (byte)(v >>> 16);
        bytes[start+6] = (byte) (v >>> 8);
        bytes[start+7] = (byte) (v);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID uuid, JdbcType jdbcType) throws SQLException {
        ps.setBytes(i, toBytes(uuid));
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromBytes(rs.getBytes(columnName));
    }

    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromBytes(rs.getBytes(columnIndex));
    }

    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromBytes(cs.getBytes(columnIndex));
    }
}
