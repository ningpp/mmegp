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
package me.ningpp.mmegp.demo.entity3;

import me.ningpp.mmegp.annotations.Table;
import me.ningpp.mmegp.annotations.Column;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.apache.ibatis.type.JdbcType.VARCHAR;

@Table(table = "sys_company")
public record SysCompany(
        @Column(name = "id", jdbcType = VARCHAR, id = true)
        String id,

        @Column(name = "name", jdbcType = VARCHAR)
        String name,

        @Column(name = "start_date", jdbcType = JdbcType.DATE)
        LocalDate startDate,

        @Column(name = "market_cap", jdbcType = JdbcType.DECIMAL)
        BigDecimal marketCap,

        @Column(name = "unified_code", jdbcType = VARCHAR)
        String unifiedCode
) {
}
