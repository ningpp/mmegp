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
package me.ningpp.mmegp.mybatis.dsql.function;

import me.ningpp.mmegp.sql.time.SqlTimeFunction;
import org.mybatis.dynamic.sql.BindableColumn;
import org.mybatis.dynamic.sql.render.RenderingContext;
import org.mybatis.dynamic.sql.select.function.AbstractUniTypeFunction;
import org.mybatis.dynamic.sql.util.FragmentAndParameters;

import java.sql.JDBCType;
import java.util.Objects;
import java.util.Optional;

public class DateTimeFormat<T> extends AbstractUniTypeFunction<T, DateTimeFormat<T>> {

    private final String javaPattern;
    private final SqlTimeFunction timeFunction;

    private DateTimeFormat(BindableColumn<T> column, String javaPattern, SqlTimeFunction timeFunction) {
        super(column);
        this.javaPattern = javaPattern;
        this.timeFunction = timeFunction;
    }

    @Override
    public FragmentAndParameters render(RenderingContext renderingContext) {
        String format = timeFunction.tranlateFormat(javaPattern);
        if (format.indexOf('\'') != -1) {
            throw new IllegalArgumentException("SQL injection???, javaPattern = " + javaPattern);
        }
        return column.render(renderingContext).mapFragment(s -> timeFunction.formatFunctionName()
                + "(" + s + ", '" + format + "')");
    }

    @Override
    protected DateTimeFormat<T> copy() {
        return new DateTimeFormat<>(column, javaPattern, timeFunction);
    }

    @Override
    public Optional<JDBCType> jdbcType() {
        return Optional.empty();
    }

    @Override
    public Optional<String> typeHandler() {
        return Optional.empty();
    }

    public static <T> DateTimeFormat<T> of(BindableColumn<T> column, String javaPattern, SqlTimeFunction timeFunction) {
        Objects.requireNonNull(javaPattern, "javaPattern can't be null");
        Objects.requireNonNull(timeFunction, "timeFunction can't be null");
        return new DateTimeFormat<>(column, javaPattern, timeFunction);
    }
}
