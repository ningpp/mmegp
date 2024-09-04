/*
 *    Copyright 2021-2022 the original author or authors.
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
package me.ningpp.mmegp.demo.model;

import org.apache.ibatis.type.JdbcType;

import me.ningpp.mmegp.annotations.Table;
import me.ningpp.mmegp.annotations.Column;

@Table(table = "sys_user_role")
public class SysUserRole {
    @Column(name = "id", jdbcType = JdbcType.BIGINT, id = true, generatedValue = true)
    private Long id;
    @Column(name = "user_id", jdbcType = JdbcType.VARCHAR)
    private String userId;
    @Column(name = "role_id", jdbcType = JdbcType.VARCHAR, id = false)
    private String roleId;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
