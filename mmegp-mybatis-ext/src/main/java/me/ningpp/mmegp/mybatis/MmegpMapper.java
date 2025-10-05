/*
 *    Copyright 2025 the original author or authors.
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

import me.ningpp.mmegp.query.CountDTO;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.select.aggregate.CountAll;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MmegpMapper {

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    List<Integer> selectManyIntegers(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    List<Long> selectManyLongs(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    List<BigDecimal> selectManyBigDecimals(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    List<String> selectManyStrings(SelectStatementProvider selectStatement);

    default <T> BasicColumn[] buildCountGroupByColumn(SqlColumn<T> column) {
        return new BasicColumn[] {
                new CountAll().as("cnt"),
                column.as("column_value")
        };
    }

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results({
            @Result(column="cnt", property="count", jdbcType= JdbcType.BIGINT),
            @Result(column="column_value", property="value", jdbcType=JdbcType.INTEGER, javaType = Integer.class),
    })
    List<CountDTO<Integer>> countGroupByIntegerColumn(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results({
            @Result(column="cnt", property="count", jdbcType= JdbcType.BIGINT),
            @Result(column="column_value", property="value", jdbcType=JdbcType.BIGINT, javaType = Long.class),
    })
    List<CountDTO<Long>> countGroupByBigIntColumn(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results({
            @Result(column="cnt", property="count", jdbcType= JdbcType.BIGINT),
            @Result(column="column_value", property="value", jdbcType=JdbcType.DATE, javaType = LocalDate.class),
    })
    List<CountDTO<LocalDate>> countGroupByDateColumn(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results({
            @Result(column="cnt", property="count", jdbcType= JdbcType.BIGINT),
            @Result(column="column_value", property="value", jdbcType=JdbcType.VARCHAR, javaType = String.class),
    })
    List<CountDTO<String>> countGroupByStringColumn(SelectStatementProvider selectStatement);

}
