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

import me.ningpp.mmegp.mybatis.UUIDNoDashStringTypeHandler;
import me.ningpp.mmegp.mybatis.UUIDStringTypeHandler;
import me.ningpp.mmegp.mybatis.UUIDTypeHandler;
import org.apache.ibatis.type.JdbcType;

import me.ningpp.mmegp.annotations.Generated;
import me.ningpp.mmegp.annotations.GeneratedColumn;

import java.util.UUID;

@Generated(table = "sys_menu")
public class SysMenu {
    @GeneratedColumn(name = "id", jdbcType = JdbcType.VARCHAR, id = true)
    private String id;

    @GeneratedColumn(name = "name", jdbcType = JdbcType.VARCHAR)
    private String name;

    @GeneratedColumn(name = "parent_id", jdbcType = JdbcType.VARCHAR)
    private String parentId;

    @GeneratedColumn(name = "bytes1", jdbcType = JdbcType.BINARY)
    private byte[] bytes1;
    @GeneratedColumn(name = "bytes2", jdbcType = JdbcType.BINARY)
    private Byte[] bytes2;

    @GeneratedColumn(name = "uuid", jdbcType = JdbcType.BINARY, typeHandler = UUIDTypeHandler.class)
    private UUID uuid;
    @GeneratedColumn(name = "with_dash_uuid", jdbcType = JdbcType.VARCHAR, typeHandler = UUIDStringTypeHandler.class)
    private UUID withDashUUID;
    @GeneratedColumn(name = "no_dash_uuid", jdbcType = JdbcType.VARCHAR, typeHandler = me.ningpp.mmegp.mybatis.UUIDNoDashStringTypeHandler.class)
    private UUID noDashUUID;

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

    public byte[] getBytes1() {
        return bytes1;
    }

    public void setBytes1(byte[] bytes1) {
        this.bytes1 = bytes1;
    }

    public Byte[] getBytes2() {
        return bytes2;
    }

    public void setBytes2(Byte[] bytes2) {
        this.bytes2 = bytes2;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getWithDashUUID() {
        return withDashUUID;
    }

    public void setWithDashUUID(UUID withDashUUID) {
        this.withDashUUID = withDashUUID;
    }

    public UUID getNoDashUUID() {
        return noDashUUID;
    }

    public void setNoDashUUID(UUID noDashUUID) {
        this.noDashUUID = noDashUUID;
    }
}
