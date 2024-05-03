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

import me.ningpp.mmegp.sql.time.H2TimeFunction;
import me.ningpp.mmegp.sql.time.MySqlTimeFunction;
import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.configuration.StatementConfiguration;
import org.mybatis.dynamic.sql.render.ExplicitTableAliasCalculator;
import org.mybatis.dynamic.sql.render.RenderingContext;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.render.TableAliasCalculator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateTimeFormatTest {

    @Test
    void formatTest() {
        SqlTable table = SqlTable.of("article");
        SqlColumn<LocalDateTime> createTime = SqlColumn.of("create_time", table);
        TableAliasCalculator calculator = ExplicitTableAliasCalculator.of(table, null);
        TableAliasCalculator calculatorAlias = ExplicitTableAliasCalculator.of(table, "_article_");
        assertThrows(IllegalArgumentException.class,
                () -> DateTimeFormat.of(createTime,
                        "yyyy-MM-dd'T'HH:mm:ss", new H2TimeFunction()).render(buildRenderingContext(calculator)),
                "SQL injection???, javaPattern = yyyy-MM-dd'T'HH:mm:ss");
        assertEquals("DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s')", DateTimeFormat.of(createTime,
                "yyyy-MM-dd HH:mm:ss", new MySqlTimeFunction()).render(buildRenderingContext(calculator)).fragment());
        assertEquals("DATE_FORMAT(_article_.create_time, '%Y-%m-%d %H:%i:%s')", DateTimeFormat.of(createTime,
                "yyyy-MM-dd HH:mm:ss", new MySqlTimeFunction()).render(buildRenderingContext(calculatorAlias)).fragment());

        DateTimeFormat<LocalDateTime> dtfFunction = DateTimeFormat.of(createTime, "yyyy-MM-dd HH:mm:ss", new H2TimeFunction());
        assertNotNull(dtfFunction.copy());
        assertTrue(dtfFunction.typeHandler().isEmpty());
        assertTrue(dtfFunction.jdbcType().isEmpty());
    }

    private static final StatementConfiguration STATEMENT_CONFIGURATION = new StatementConfiguration();

    private RenderingContext buildRenderingContext(TableAliasCalculator calculator) {
        return RenderingContext.withRenderingStrategy(RenderingStrategies.MYBATIS3)
                .withStatementConfiguration(STATEMENT_CONFIGURATION)
                .withTableAliasCalculator(calculator).build();
    }

}
