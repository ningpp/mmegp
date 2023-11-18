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
import me.ningpp.mmegp.demo.entity2.SysOrg;
import me.ningpp.mmegp.demo.entity2.SysOrg2;
import me.ningpp.mmegp.demo.mapper.SysOrg2DynamicSqlSupport;
import me.ningpp.mmegp.demo.mapper.SysOrg2Mapper;
import me.ningpp.mmegp.demo.mapper.SysOrgDynamicSqlSupport;
import me.ningpp.mmegp.demo.mapper.SysOrgMapper;
import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoftDeleteTest extends DemoApplicationStarter {

    @Autowired
    SysOrgMapper sysOrgMapper;
    @Autowired
    SysOrg2Mapper sysOrg2Mapper;

    @Test
    void fixedValueStrategyTest() {
        SysOrg org = new SysOrg(
                UUID.randomUUID().toString(),
                "name",
                (byte)0
        );
        sysOrgMapper.insert(org);

        sysOrgMapper.softDeleteByPrimaryKey(org.id());
        Optional<SysOrg> opOrg = sysOrgMapper.selectOne(dsl ->
                dsl.where().and(
                        SysOrgDynamicSqlSupport.id,
                        SqlBuilder.isEqualTo(org.id())
                )
        );
        assertTrue(opOrg.isPresent());
        assertEquals((byte)1, opOrg.get().deleted());

        sysOrgMapper.cancelSoftDeleteByPrimaryKey(org.id());
        opOrg = sysOrgMapper.selectOne(dsl ->
                dsl.where().and(
                        SysOrgDynamicSqlSupport.id,
                        SqlBuilder.isEqualTo(org.id())
                )
        );
        assertTrue(opOrg.isPresent());
        assertEquals((byte)0, opOrg.get().deleted());
    }

    @Test
    void deletedAtStrategyTest() {
        SysOrg2 org2 = new SysOrg2(
                UUID.randomUUID().toString(),
                "name",
                null
        );
        sysOrg2Mapper.insert(org2);

        sysOrg2Mapper.softDeleteByPrimaryKey(org2.id());
        Optional<SysOrg2> opOrg2 = sysOrg2Mapper.selectOne(dsl ->
                dsl.where().and(
                        SysOrg2DynamicSqlSupport.id,
                        SqlBuilder.isEqualTo(org2.id())
                )
        );
        assertTrue(opOrg2.isPresent());
        assertNotNull(opOrg2.get().deleteTime());

        sysOrg2Mapper.cancelSoftDeleteByPrimaryKey(org2.id());
        opOrg2 = sysOrg2Mapper.selectOne(dsl ->
                dsl.where().and(
                        SysOrg2DynamicSqlSupport.id,
                        SqlBuilder.isEqualTo(org2.id())
                )
        );
        assertTrue(opOrg2.isPresent());
        assertNull(opOrg2.get().deleteTime());
    }

}
