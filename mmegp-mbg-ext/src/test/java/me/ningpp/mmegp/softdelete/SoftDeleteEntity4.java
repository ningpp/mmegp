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
package me.ningpp.mmegp.softdelete;

import me.ningpp.mmegp.annotations.Table;
import me.ningpp.mmegp.annotations.Column;
import me.ningpp.mmegp.annotations.SoftDelete;
import me.ningpp.mmegp.enums.SoftDeleteStrategy;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@Table(table = "SoftDeleteEntity4")
@SoftDelete(column = "delete_time", strategy = SoftDeleteStrategy.DELETED_AT)
public record SoftDeleteEntity4(
    @Column(name = "id", jdbcType = JdbcType.VARCHAR, id = true)
    String id,
    @Column(name = "name", jdbcType = JdbcType.VARCHAR)
    String name,
    @Column(name = "delete_time", jdbcType = JdbcType.TIMESTAMP)
    LocalDateTime deleteTime
) {
}
