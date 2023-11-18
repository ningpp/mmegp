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

import me.ningpp.mmegp.demo.DemoApplicationStarterHsqldb;
import me.ningpp.mmegp.demo.entity3.SysCompany;
import me.ningpp.mmegp.demo.mapper.SysCompanyMapper;
import me.ningpp.mmegp.mybatis.dsql.pagination.LimitOffset;
import me.ningpp.mmegp.mybatis.dsql.pagination.Page;
import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.exception.InvalidSqlException;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static me.ningpp.mmegp.demo.mapper.SysCompanyDynamicSqlSupport.id;
import static me.ningpp.mmegp.demo.mapper.SysCompanyDynamicSqlSupport.sysCompany;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SysCompanyTest extends DemoApplicationStarterHsqldb {

    @Autowired
    SysCompanyMapper sysCompanyMapper;

    @Test
    void selectPageTest() {
        List<SysCompany> companys = List.of(
                new SysCompany(uuid(), uuid(), LocalDate.now(), uuid()),
                new SysCompany(uuid(), uuid(), LocalDate.now(), uuid()),
                new SysCompany(uuid(), uuid(), LocalDate.now(), uuid())
        ).stream().sorted((c1, c2) -> c2.id().compareTo(c1.id())).toList();
        sysCompanyMapper.insertMultiple(companys);

        for (int i = 0; i < companys.size(); i++) {
            SelectDSL<SelectModel> selectDsl = SelectDSL
                    .select(SysCompanyMapper.selectList)
                    .from(sysCompany)
                    .orderBy(SqlBuilder.sortColumn("id").descending());
            Page<SysCompany> page = sysCompanyMapper
                    .selectPage(selectDsl, LimitOffset.of(1L, (long)i), renderer);
            assertEquals(companys.size(), page.getTotalCount());
            assertEquals(1, page.getItems().size());
            assertEquals(companys.get(i), page.getItems().get(0));
        }

        int deleted = sysCompanyMapper.delete(dsl -> dsl.where()
                .and(id, SqlBuilder.isIn(companys.stream().map(SysCompany::id).toList())));
        assertEquals(companys.size(), deleted);
    }

    @Test
    void insertUpdateTest() {
        SysCompany company = new SysCompany(
                uuid(),
                "name",
                LocalDate.now(),
                "1"
        );
        sysCompanyMapper.insert(company);

        assertEquals(company, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        SysCompany company2 = new SysCompany(
                company.id(), uuid(), LocalDate.of(2023, 1, 1), uuid()
        );
        assertEquals(1, sysCompanyMapper.updateByPrimaryKey(company2));
        assertEquals(company2, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        assertEquals(1, sysCompanyMapper.deleteByPrimaryKey(company.id()));
    }

    @Test
    void insertUpdateSelective1Test() {
        SysCompany company = new SysCompany(
                uuid(), null, null, null
        );
        sysCompanyMapper.insertSelective(company);

        assertEquals(company, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        SysCompany company2 = new SysCompany(
                company.id(), null, null, null
        );
        // All set phrases were dropped when rendering the update statement
        assertThrows(InvalidSqlException.class, () -> sysCompanyMapper.updateByPrimaryKeySelective(company2));

        assertEquals(1, sysCompanyMapper.deleteByPrimaryKey(company.id()));
    }

    @Test
    void insertUpdateSelective2Test() {
        SysCompany company = new SysCompany(
                uuid(), "name", null, null
        );
        sysCompanyMapper.insertSelective(company);

        assertEquals(company, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        SysCompany company2 = new SysCompany(
                company.id(), uuid(), null, null
        );
        assertEquals(1, sysCompanyMapper.updateByPrimaryKeySelective(company2));
        assertEquals(company2, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        assertEquals(1, sysCompanyMapper.deleteByPrimaryKey(company.id()));
    }

    @Test
    void insertUpdateSelective3Test() {
        SysCompany company = new SysCompany(
                uuid(), "name", LocalDate.now(), null
        );
        sysCompanyMapper.insertSelective(company);

        assertEquals(company, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        SysCompany company2 = new SysCompany(
                company.id(), uuid(), LocalDate.of(2023, 1, 1), null
        );
        assertEquals(1, sysCompanyMapper.updateByPrimaryKeySelective(company2));
        assertEquals(company2, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        assertEquals(1, sysCompanyMapper.deleteByPrimaryKey(company.id()));
    }

    @Test
    void insertUpdateSelective4Test() {
        SysCompany company = new SysCompany(
                uuid(), "name", LocalDate.now(), uuid()
        );
        sysCompanyMapper.insertSelective(company);

        assertEquals(company, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        SysCompany company2 = new SysCompany(
                company.id(), uuid(), LocalDate.of(2023, 1, 1), uuid()
        );
        assertEquals(1, sysCompanyMapper.updateByPrimaryKeySelective(company2));
        assertEquals(company2, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        assertEquals(1, sysCompanyMapper.deleteByPrimaryKey(company.id()));
    }

}
