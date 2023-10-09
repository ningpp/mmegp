/*
 *    Copyright 2021 the original author or authors.
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
package me.ningpp.mmegp.demo.entity;

import org.apache.ibatis.type.JdbcType;

import me.ningpp.mmegp.annotations.Generated;
import me.ningpp.mmegp.annotations.GeneratedColumn;

@Generated(table = "sys_menu")
public class SysMenu {
    @GeneratedColumn(name = "id", jdbcType = JdbcType.VARCHAR, id = true)
    private String id;

    @GeneratedColumn(name = "name", jdbcType = JdbcType.VARCHAR)
    private String name;

    @GeneratedColumn(name = "parent_id", jdbcType = JdbcType.VARCHAR)
    private String parentId;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getParentId() {
        return parentId;
    }
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
