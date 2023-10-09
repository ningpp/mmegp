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

@Generated(table = "sys_role_menu")
public class SysRoleMenu {
    @GeneratedColumn(name = "id", jdbcType = JdbcType.BIGINT, id = true, generatedValue = true)
    private Long id;
    @GeneratedColumn(name = "role_id", jdbcType = JdbcType.VARCHAR, id = false)
    private String roleId;
    @GeneratedColumn(name = "menu_id", jdbcType = JdbcType.VARCHAR)
    private String menuId;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    public String getMenuId() {
        return menuId;
    }
    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
