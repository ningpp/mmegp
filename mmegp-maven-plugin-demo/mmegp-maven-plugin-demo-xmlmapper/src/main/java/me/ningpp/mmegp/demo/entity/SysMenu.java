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

import me.ningpp.mmegp.annotations.Table;
import me.ningpp.mmegp.annotations.Column;
import me.ningpp.mmegp.mybatis.type.list.ListCommaIntegerTypeHandler;
import me.ningpp.mmegp.mybatis.type.list.ListCommaLongTypeHandler;
import me.ningpp.mmegp.mybatis.type.list.ListCommaStringTypeHandler;
import me.ningpp.mmegp.mybatis.type.set.LinkedHashSetCommaIntegerTypeHandler;
import me.ningpp.mmegp.mybatis.type.set.LinkedHashSetCommaLongTypeHandler;
import me.ningpp.mmegp.mybatis.type.set.LinkedHashSetCommaStringTypeHandler;
import me.ningpp.mmegp.mybatis.type.set.SetCommaIntegerTypeHandler;
import me.ningpp.mmegp.mybatis.type.set.SetCommaLongTypeHandler;
import me.ningpp.mmegp.mybatis.type.set.SetCommaStringTypeHandler;
import me.ningpp.mmegp.mybatis.type.uuid.UUIDNoDashStringTypeHandler;
import me.ningpp.mmegp.mybatis.type.uuid.UUIDStringTypeHandler;
import me.ningpp.mmegp.mybatis.type.uuid.UUIDTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table(table = "sys_menu")
public class SysMenu {
    @Column(name = "id", jdbcType = JdbcType.VARCHAR, id = true)
    private String id;

    @Column(name = "name", jdbcType = JdbcType.VARCHAR)
    private String name;

    @Column(name = "parent_id", jdbcType = JdbcType.VARCHAR)
    private String parentId;

    @Column(name = "integer_list", jdbcType = JdbcType.VARCHAR,
            typeHandler = ListCommaIntegerTypeHandler.class
    )
    private List<Integer> integerList;

    @Column(name = "long_list", jdbcType = JdbcType.VARCHAR,
            typeHandler = ListCommaLongTypeHandler.class
    )
    private List<Long> longList;

    @Column(name = "string_list", jdbcType = JdbcType.VARCHAR,
            typeHandler = ListCommaStringTypeHandler.class
    )
    private List<String> stringList;

    @Column(name = "integer_set", jdbcType = JdbcType.VARCHAR,
            typeHandler = SetCommaIntegerTypeHandler.class
    )
    private Set<Integer> integerSet;

    @Column(name = "long_set", jdbcType = JdbcType.VARCHAR,
            typeHandler = SetCommaLongTypeHandler.class
    )
    private Set<Long> longSet;

    @Column(name = "string_set", jdbcType = JdbcType.VARCHAR,
            typeHandler = SetCommaStringTypeHandler.class
    )
    private Set<String> stringSet;

    @Column(
            name = "integer_linkedhashset", jdbcType = JdbcType.VARCHAR,
            typeHandler = LinkedHashSetCommaIntegerTypeHandler.class
    )
    private LinkedHashSet<Integer> integerLinkedhashset;

    @Column(
            name = "long_linkedhashset", jdbcType = JdbcType.VARCHAR,
            typeHandler = LinkedHashSetCommaLongTypeHandler.class
    )
    private HashSet<Long> longLinkedhashset;

    @Column(
            name = "string_linkedhashset", jdbcType = JdbcType.VARCHAR,
            typeHandler = LinkedHashSetCommaStringTypeHandler.class
    )
    private Set<String> stringLinkedhashset;

    @Column(name = "bytes1", jdbcType = JdbcType.BINARY)
    private byte[] bytes1;
    @Column(name = "bytes2", jdbcType = JdbcType.BINARY)
    private Byte[] bytes2;

    @Column(
            name = "uuid", jdbcType = JdbcType.BINARY,
            typeHandler = UUIDTypeHandler.class
    )
    private UUID uuid;

    @Column(
            name = "with_dash_uuid", jdbcType = JdbcType.VARCHAR,
            typeHandler = UUIDStringTypeHandler.class
    )
    private UUID withDashUUID;

    @Column(
            name = "no_dash_uuid", jdbcType = JdbcType.VARCHAR,
            typeHandler = UUIDNoDashStringTypeHandler.class
    )
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

    public List<Integer> getIntegerList() {
        return integerList;
    }

    public void setIntegerList(List<Integer> integerList) {
        this.integerList = integerList;
    }

    public List<Long> getLongList() {
        return longList;
    }

    public void setLongList(List<Long> longList) {
        this.longList = longList;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public Set<Integer> getIntegerSet() {
        return integerSet;
    }

    public void setIntegerSet(Set<Integer> integerSet) {
        this.integerSet = integerSet;
    }

    public Set<Long> getLongSet() {
        return longSet;
    }

    public void setLongSet(Set<Long> longSet) {
        this.longSet = longSet;
    }

    public Set<String> getStringSet() {
        return stringSet;
    }

    public void setStringSet(Set<String> stringSet) {
        this.stringSet = stringSet;
    }

    public LinkedHashSet<Integer> getIntegerLinkedhashset() {
        return integerLinkedhashset;
    }

    public void setIntegerLinkedhashset(LinkedHashSet<Integer> integerLinkedhashset) {
        this.integerLinkedhashset = integerLinkedhashset;
    }

    public HashSet<Long> getLongLinkedhashset() {
        return longLinkedhashset;
    }

    public void setLongLinkedhashset(HashSet<Long> longLinkedhashset) {
        this.longLinkedhashset = longLinkedhashset;
    }

    public Set<String> getStringLinkedhashset() {
        return stringLinkedhashset;
    }

    public void setStringLinkedhashset(Set<String> stringLinkedhashset) {
        this.stringLinkedhashset = stringLinkedhashset;
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
