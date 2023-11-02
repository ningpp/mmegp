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
package me.ningpp.mmegp.mybatis.type.uuid;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UUIDNoDashStringTypeHandler extends BaseTypeHandler<UUID> {

    private static final String DASH = "-";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID uuid, JdbcType jdbcType) throws SQLException {
        String withDashString = uuid.toString();
        String noDashString = withDashString.substring(0, 8)
                + withDashString.substring(9, 13)
                + withDashString.substring(14, 18)
                + withDashString.substring(19, 23)
                + withDashString.substring(24);
        ps.setString(i, noDashString);
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromNoDashString(rs.getString(columnName));
    }

    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromNoDashString(rs.getString(columnIndex));
    }

    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromNoDashString(cs.getString(columnIndex));
    }

    private UUID fromNoDashString(String str) {
        if (str == null) {
            return null;
        }
        final String withDashString = str.substring(0, 8)
                + DASH
                + str.substring(8, 12)
                + DASH
                + str.substring(12, 16)
                + DASH
                + str.substring(16, 20)
                + DASH
                + str.substring(20);
        return UUID.fromString(withDashString);
    }
}
