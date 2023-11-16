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
package me.ningpp.mmegp.entity;

import me.ningpp.mmegp.annotations.Generated;
import me.ningpp.mmegp.annotations.GeneratedColumn;
import org.apache.ibatis.type.JdbcType;

import static org.apache.ibatis.type.JdbcType.VARCHAR;

@Generated(table = "big_record_entity")
public record BigRecordEntity(
        @GeneratedColumn(name = "id", jdbcType = VARCHAR, id = true)
        String id,

        @GeneratedColumn(name = "name", jdbcType = JdbcType.VARCHAR)
        String name
) {
}
