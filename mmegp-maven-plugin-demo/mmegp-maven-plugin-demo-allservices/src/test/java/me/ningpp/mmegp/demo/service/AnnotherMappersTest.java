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
package me.ningpp.mmegp.demo.service;

import me.ningpp.mmegp.demo.DemoApplicationStarter;
import me.ningpp.mmegp.demo.annothermapper1.SysAutoUserDynamicSqlSupport;
import me.ningpp.mmegp.demo.annothermapper1.SysAutoUserAnnotherMapper;
import me.ningpp.mmegp.demo.annothermapper1.SysMenuDynamicSqlSupport;
import me.ningpp.mmegp.demo.annothermapper1.SysMenuAnnotherMapper;
import me.ningpp.mmegp.demo.annothermapper1.SysRoleDynamicSqlSupport;
import me.ningpp.mmegp.demo.annothermapper1.SysRoleAnnotherMapper;
import me.ningpp.mmegp.demo.annothermapper1.SysRoleMenuDynamicSqlSupport;
import me.ningpp.mmegp.demo.annothermapper1.SysRoleMenuAnnotherMapper;
import me.ningpp.mmegp.demo.annothermapper2.SysUserDynamicSqlSupport;
import me.ningpp.mmegp.demo.annothermapper2.SysUserAnnotherMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

class AnnotherMappersTest extends DemoApplicationStarter {

    @Autowired
    SysAutoUserAnnotherMapper sysAutoUserAnnotherMapper;

    @Autowired
    SysMenuAnnotherMapper sysMenuAnnotherMapper;

    @Autowired
    SysRoleAnnotherMapper sysRoleAnnotherMapper;

    @Autowired
    SysRoleMenuAnnotherMapper sysRoleMenuAnnotherMapper;

    @Autowired
    SysUserAnnotherMapper sysUserAnnotherMapper;

    @Test
    void test() {
        assertEquals(0, sysAutoUserAnnotherMapper.count(dsl ->
                dsl.where().and(SysAutoUserDynamicSqlSupport.firstName,
                        isEqualTo(UUID.randomUUID().toString()))));

        assertEquals(0, sysMenuAnnotherMapper.count(dsl ->
                dsl.where().and(SysMenuDynamicSqlSupport.id,
                        isEqualTo(UUID.randomUUID().toString()))));

        assertEquals(0, sysRoleAnnotherMapper.count(dsl ->
                dsl.where().and(SysRoleDynamicSqlSupport.id,
                        isEqualTo(UUID.randomUUID().toString()))));

        assertEquals(0, sysRoleMenuAnnotherMapper.count(dsl ->
                dsl.where().and(SysRoleMenuDynamicSqlSupport.roleId,
                        isEqualTo(UUID.randomUUID().toString()))));

        assertEquals(0, sysUserAnnotherMapper.count(dsl ->
                dsl.where().and(SysUserDynamicSqlSupport.id,
                        isEqualTo(UUID.randomUUID().toString()))));
    }

}
