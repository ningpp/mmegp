/*
 *    Copyright 2025 the original author or authors.
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
package me.ningpp.mmegp.demo.mapper;

import me.ningpp.mmegp.mybatis.dsql.EntityCriteriaDTO;
import me.ningpp.mmegp.query.CountDTO;
import me.ningpp.mmegp.query.SumDTO;
import org.mybatis.dynamic.sql.render.RenderingStrategies;

import java.time.LocalDate;
import java.util.List;

import static me.ningpp.mmegp.demo.mapper.SysCompanyDynamicSqlSupport.id;
import static me.ningpp.mmegp.demo.mapper.SysCompanyDynamicSqlSupport.marketCap;
import static me.ningpp.mmegp.demo.mapper.SysCompanyDynamicSqlSupport.startDate;
import static me.ningpp.mmegp.demo.mapper.SysCompanyDynamicSqlSupport.sysCompany;

public interface SysCompanyMapperExt extends SysCompanyMapper {

    default List<CountDTO<LocalDate>> countGroupByStartDate(EntityCriteriaDTO criteria) {
        return countGroupByDateColumn(criteria.toQuery(sysCompany)
                .columns(buildCountGroupByColumn(startDate))
                .toSelectModelOfGroupBy(startDate)
                .render(RenderingStrategies.MYBATIS3));
    }

    default List<CountDTO<String>> countGroupById(EntityCriteriaDTO criteria) {
        return countGroupByStringColumn(criteria.toQuery(sysCompany)
                .columns(buildCountGroupByColumn(id))
                .toSelectModelOfGroupBy(id)
                .render(RenderingStrategies.MYBATIS3));
    }

    default List<SumDTO<LocalDate>> sumMarketCapGroupByStartDate(EntityCriteriaDTO criteria) {
        return sumGroupByDateColumn(criteria.toQuery(sysCompany)
                .columns(buildSumGroupByColumn(marketCap, startDate))
                .toSelectModelOfGroupBy(startDate)
                .render(RenderingStrategies.MYBATIS3));
    }

}
