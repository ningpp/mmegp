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
import me.ningpp.mmegp.demo.mapper.SysCompanyDynamicSqlSupport;
import me.ningpp.mmegp.demo.mapper.SysCompanyMapper;
import me.ningpp.mmegp.demo.mapper.SysCompanyMapperExt;
import me.ningpp.mmegp.demo.query.SysCompanyQueryConditionDTO;
import me.ningpp.mmegp.mybatis.dsql.EntityCriteriaDTO;
import me.ningpp.mmegp.mybatis.dsql.EntityCriteriaNodeDTO;
import me.ningpp.mmegp.mybatis.dsql.SqlOperator;
import me.ningpp.mmegp.mybatis.dsql.pagination.LimitOffset;
import me.ningpp.mmegp.mybatis.dsql.pagination.Page;
import me.ningpp.mmegp.query.CountDTO;
import me.ningpp.mmegp.query.SumDTO;
import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.exception.InvalidSqlException;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.ningpp.mmegp.demo.mapper.SysCompanyDynamicSqlSupport.id;
import static me.ningpp.mmegp.demo.mapper.SysCompanyDynamicSqlSupport.sysCompany;
import static me.ningpp.mmegp.query.PropertyConditionDTO.equalTo;
import static me.ningpp.mmegp.query.PropertyConditionDTO.greaterEqual;
import static me.ningpp.mmegp.query.PropertyConditionDTO.in;
import static me.ningpp.mmegp.query.PropertyConditionDTO.like;
import static me.ningpp.mmegp.query.PropertyConditionDTO.notEqualTo;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SysCompanyTest extends DemoApplicationStarterHsqldb {

    @Autowired
    SysCompanyMapper sysCompanyMapper;

    @Autowired
    SysCompanyMapperExt sysCompanyMapperExt;

    @Test
    void handwritingByIdsTest() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            SysCompany company = new SysCompany(uuid(), uuid(), null, null, uuid());
            sysCompanyMapper.insert(company);
            ids.add(company.id());
        }

        assertArrayEquals(ids.stream().sorted().toArray(),
                sysCompanyMapperExt.handwritingSelectByIds(ids).stream()
                        .map(SysCompany::id).sorted().toArray());

        int deletedCount = sysCompanyMapperExt.handwritingDeleteByIds(ids);
        assertEquals(2, deletedCount);

        assertTrue(sysCompanyMapperExt.handwritingSelectByIds(ids).isEmpty());
    }

    @Test
    void countSumGroupByTest() {
        LocalDate today = LocalDate.now();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            SysCompany company = new SysCompany(
                    uuid(),
                    "有限责任公司",
                    i == 3 ? today.minusDays(1) : today,
                    i == 3 ? null : BigDecimal.valueOf(i),
                    uuid()
            );
            sysCompanyMapper.insert(company);
            ids.add(company.id());
        }

        List<CountDTO<String>> cnts = sysCompanyMapperExt.countGroupById(new EntityCriteriaDTO());
        assertEquals(4, cnts.size());
        assertEquals(1, cnts.stream().map(CountDTO::getCount).distinct().count());
        assertArrayEquals(ids.stream().sorted().toArray(),
                cnts.stream().map(CountDTO::getValue).sorted().toArray());

        List<CountDTO<LocalDate>> dates = sysCompanyMapperExt.countGroupByStartDate(new EntityCriteriaDTO());
        assertEquals(2, dates.size());
        assertEquals(1, dates.stream().filter(s -> today.equals(s.getValue())).count());
        assertEquals(1, dates.stream().filter(s -> !today.equals(s.getValue())).count());
        assertEquals(3, dates.stream().filter(s -> today.equals(s.getValue())).findFirst().get().getCount());
        assertEquals(1, dates.stream().filter(s -> today.minusDays(1).equals(s.getValue())).findFirst().get().getCount());

        List<SumDTO<LocalDate>> sums = sysCompanyMapperExt.sumMarketCapGroupByStartDate(new EntityCriteriaDTO());
        assertEquals(2, sums.size());
        assertEquals(1, sums.stream().filter(s -> today.equals(s.getValue())).count());
        assertEquals(1, sums.stream().filter(s -> !today.equals(s.getValue())).count());
        assertNull(sums.stream().filter(s -> today.minusDays(1).equals(s.getValue())).findFirst().get().getSum());
        assertEquals(new BigDecimal("3"), sums.stream().filter(s -> today.equals(s.getValue())).findFirst().get().getSum().setScale(0, RoundingMode.HALF_UP));

        sysCompanyMapper.deleteByQuery(new SysCompanyQueryConditionDTO().id(in(ids)));
    }

    @Test
    void companyTest() {
        SysCompany company = new SysCompany(
                uuid(),
                "有限责任公司",
                LocalDate.now(),
                null,
                uuid()
        );
        sysCompanyMapper.insert(company);

        SysCompanyQueryConditionDTO queryDto = new SysCompanyQueryConditionDTO()
                .id(equalTo(company.id()))
                .startDate(greaterEqual(LocalDate.now().minusDays(1)))
                .unifiedCode(notEqualTo(uuid()))
                .name(like("%责任%"));
        assertEquals(1, sysCompanyMapper.countByQuery(queryDto));

        assertEquals(company, sysCompanyMapper.selectByQuery(queryDto,
                LimitOffset.ofLimit(1L), renderer,
                SqlBuilder.sortColumn("id").descending()).get(0));

        assertEquals(1, sysCompanyMapper.deleteByQuery(queryDto));
    }

    @Test
    void companyCriteriaTest() {
        SysCompany company = new SysCompany(
                uuid(),
                "有限责任公司",
                LocalDate.now(),
                null,
                uuid()
        );
        sysCompanyMapper.insert(company);

        EntityCriteriaNodeDTO first = EntityCriteriaNodeDTO.of(
                new SysCompanyQueryConditionDTO().id(equalTo(UUID.randomUUID().toString()))
        );
        EntityCriteriaNodeDTO second = EntityCriteriaNodeDTO.of(
                new SysCompanyQueryConditionDTO().id(equalTo(company.id()))
        );
        EntityCriteriaDTO criteria = new EntityCriteriaDTO(List.of(first, second), SqlOperator.OR);

        List<SysCompany> companies = sysCompanyMapper.selectByCriteria(criteria,
                LimitOffset.of(11L, 0L), renderer, SqlBuilder.sortColumn("id").descending());
        assertEquals(1, companies.size());
        assertEquals(company, companies.get(0));

        assertEquals(1, sysCompanyMapper.countByCriteria(criteria));
        assertEquals(1, sysCompanyMapper.countDistinctByCriteria(id, criteria));

        Page<SysCompany> companyPage = sysCompanyMapper.selectPageByCriteria(criteria,
                LimitOffset.of(1L, 0L), renderer,
                SqlBuilder.sortColumn("start_date").descending(),
                SqlBuilder.sortColumn("id").descending());
        assertEquals(1, companyPage.getTotalCount());
        assertEquals(1, companyPage.getItems().size());
        assertEquals(company, companyPage.getItems().get(0));

        assertEquals(1, sysCompanyMapper.deleteByCriteria(criteria));

        companies = sysCompanyMapper.selectByCriteria(criteria, LimitOffset.of(11L, 0L), renderer,
                SqlBuilder.sortColumn("id").descending());
        assertEquals(0, companies.size());
    }

    @Test
    void selectPageTest() {
        List<SysCompany> companys = List.of(
                new SysCompany(uuid(), uuid(), LocalDate.now(), null, uuid()),
                new SysCompany(uuid(), uuid(), LocalDate.now(), null, uuid()),
                new SysCompany(uuid(), uuid(), LocalDate.now(), null, uuid())
        ).stream().sorted((c1, c2) -> c2.id().compareTo(c1.id())).toList();
        sysCompanyMapper.insertMultiple(companys);

        for (int i = 0; i < companys.size(); i++) {
            SelectDSL<SelectModel> selectDsl = SelectDSL
                    .select(SysCompanyDynamicSqlSupport.ALL_COLUMNS)
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
                null,
                "1"
        );
        sysCompanyMapper.insert(company);

        assertEquals(company, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        SysCompany company2 = new SysCompany(
                company.id(), uuid(), LocalDate.of(2023, 1, 1), null, uuid()
        );
        assertEquals(1, sysCompanyMapper.updateByPrimaryKey(company2));
        assertEquals(company2, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        assertEquals(1, sysCompanyMapper.deleteByPrimaryKey(company.id()));
    }

    @Test
    void insertUpdateSelective1Test() {
        SysCompany company = new SysCompany(
                uuid(), null, null, null, null
        );
        sysCompanyMapper.insertSelective(company);

        assertEquals(company, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        SysCompany company2 = new SysCompany(
                company.id(), null, null, null, null
        );
        // All set phrases were dropped when rendering the update statement
        assertThrows(InvalidSqlException.class, () -> sysCompanyMapper.updateByPrimaryKeySelective(company2));

        assertEquals(1, sysCompanyMapper.deleteByPrimaryKey(company.id()));
    }

    @Test
    void insertUpdateSelective2Test() {
        SysCompany company = new SysCompany(
                uuid(), "name", null, null, null
        );
        sysCompanyMapper.insertSelective(company);

        assertEquals(company, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        SysCompany company2 = new SysCompany(
                company.id(), uuid(), null, null, null
        );
        assertEquals(1, sysCompanyMapper.updateByPrimaryKeySelective(company2));
        assertEquals(company2, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        assertEquals(1, sysCompanyMapper.deleteByPrimaryKey(company.id()));
    }

    @Test
    void insertUpdateSelective3Test() {
        SysCompany company = new SysCompany(
                uuid(), "name", LocalDate.now(), null, null
        );
        sysCompanyMapper.insertSelective(company);

        assertEquals(company, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        SysCompany company2 = new SysCompany(
                company.id(), uuid(), LocalDate.of(2023, 1, 1), null, null
        );
        assertEquals(1, sysCompanyMapper.updateByPrimaryKeySelective(company2));
        assertEquals(company2, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        assertEquals(1, sysCompanyMapper.deleteByPrimaryKey(company.id()));
    }

    @Test
    void insertUpdateSelective4Test() {
        SysCompany company = new SysCompany(
                uuid(), "name", LocalDate.now(), null, uuid()
        );
        sysCompanyMapper.insertSelective(company);

        assertEquals(company, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        SysCompany company2 = new SysCompany(
                company.id(), uuid(), LocalDate.of(2023, 1, 1), null, uuid()
        );
        assertEquals(1, sysCompanyMapper.updateByPrimaryKeySelective(company2));
        assertEquals(company2, sysCompanyMapper.selectByPrimaryKey(company.id()).orElse(null));

        assertEquals(1, sysCompanyMapper.deleteByPrimaryKey(company.id()));
    }

}
