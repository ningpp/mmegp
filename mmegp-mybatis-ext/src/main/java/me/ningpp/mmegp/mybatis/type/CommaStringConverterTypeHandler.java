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

import me.ningpp.mmegp.mybatis.type.converter.StringConverter;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommaStringConverterTypeHandler<T, C extends Collection<T>> extends BaseTypeHandler<C> {

    private static final String SEPERATOR = ",";

    protected final StringConverter<T> converter;
    protected final Supplier<C> collectionFactory;

    public CommaStringConverterTypeHandler(StringConverter<T> converter, Supplier<C> collectionFactory) {
        this.converter = converter;
        this.collectionFactory = collectionFactory;
    }

    public T convert(String str) {
        return converter.convert(str);
    }

    public C fromString(String str) {
        if (str == null || str.isEmpty()) {
            return collectionFactory.get();
        }
        return Stream.of(str.split(SEPERATOR))
                .filter(s -> s != null && !s.isEmpty())
                .map(this::convert).filter(Objects::nonNull)
                .collect(Collectors.toCollection(collectionFactory));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, C parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.stream().map(T::toString).collect(Collectors.joining(SEPERATOR)));
    }

    @Override
    public C getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromString(rs.getString(columnName));
    }

    @Override
    public C getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromString(rs.getString(columnIndex));
    }

    @Override
    public C getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromString(cs.getString(columnIndex));
    }

}
