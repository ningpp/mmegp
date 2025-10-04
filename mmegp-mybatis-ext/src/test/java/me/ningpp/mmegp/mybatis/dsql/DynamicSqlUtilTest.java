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
package me.ningpp.mmegp.mybatis.dsql;

import me.ningpp.mmegp.mybatis.dsql.pagination.LimitOffset;
import me.ningpp.mmegp.mybatis.dsql.pagination.MySqlPaginationModelRenderer;
import me.ningpp.mmegp.mybatis.dsql.pagination.Page;
import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.ColumnAndConditionCriterion;
import org.mybatis.dynamic.sql.CriteriaGroup;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.aggregate.CountAll;
import org.mybatis.dynamic.sql.util.AbstractColumnMapping;
import org.mybatis.dynamic.sql.util.PropertyMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DynamicSqlUtilTest {
    SqlTable table = SqlTable.of("article");
    SqlColumn<String> id = SqlColumn.of("id", table);
    SqlColumn<LocalDateTime> createTime = SqlColumn.of("create_time", table);


    @Test
    void buildCriteriaGroupAnTest() {
        var a = SqlBuilder.select(new CountAll())
                .from(table)
                .where()

                .and(id, SqlBuilder.isEqualTo("a"))
                .and(List.of(
                        SqlBuilder.and(
                                createTime,
                                SqlBuilder.isBetween(LocalDateTime.now().minusWeeks(-1))
                                        .and(LocalDateTime.now().minusWeeks(-2))
                        ),
                        SqlBuilder.and(
                                List.of(SqlBuilder.and(
                                createTime,
                                SqlBuilder.isBetween(LocalDateTime.now().minusWeeks(2))
                                        .and(LocalDateTime.now().minusWeeks(1))
                                ),
                                        SqlBuilder.and(
                                                id,
                                                SqlBuilder.isLike("%x%")
                                        ))
                        )
                ))

                .build().render(RenderingStrategies.SPRING_NAMED_PARAMETER);
        System.out.println(a.getSelectStatement());
    }

    @Test
    void buildCriteriaGroupTest() {
        assertNull(DynamicSqlUtil.buildCriteriaGroup(null));
        assertNull(DynamicSqlUtil.buildCriteriaGroup(List.of()));
        List<SqlCriterion> criterions = new ArrayList<>();
        criterions.add(null);
        criterions.add(ColumnAndConditionCriterion.withColumn(id).withCondition(SqlBuilder.isEqualTo("a")).build());
        CriteriaGroup cg = DynamicSqlUtil.buildCriteriaGroup(criterions);
        var ssp = SqlBuilder.select(new CountAll()).from(table).where().and(cg).build().render(RenderingStrategies.MYBATIS3);
        assertEquals(1, ssp.getParameters().size());
        assertEquals("a", ssp.getParameters().get("p1"));
        assertEquals("select count(*) from article where id = #{parameters.p1}", ssp.getSelectStatement());

        criterions.add(ColumnAndConditionCriterion.withColumn(id).withCondition(SqlBuilder.isEqualTo("b")).build());
        cg = DynamicSqlUtil.buildCriteriaGroup(criterions);
        ssp = SqlBuilder.select(new CountAll()).from(table).where().and(cg).build().render(RenderingStrategies.MYBATIS3);
        assertEquals(2, ssp.getParameters().size());
        assertEquals("a", ssp.getParameters().get("p1"));
        assertEquals("b", ssp.getParameters().get("p2"));
        assertEquals("select count(*) from article where id = #{parameters.p1} and id = #{parameters.p2}", ssp.getSelectStatement());
    }

    @Test
    void selectPagetest() {
        Article article = new Article();
        article.setId(UUID.randomUUID().toString());
        long[] totals = {0L, 311L};
        for (long total : totals) {
            Page<Article> articlePage = DynamicSqlUtil.selectPage(
                    (ssp) -> total,
                    (ssp) -> List.of(article),
                    SqlBuilder.select(createTime).from(table)
                            .orderBy(SqlBuilder.sortColumn(createTime.name())),
                    LimitOffset.of(1L, 0L),
                    new MySqlPaginationModelRenderer()
            );
            assertEquals(total, articlePage.getTotalCount());
            if (total > 0) {
                assertEquals(1, articlePage.getItems().size());
                assertEquals(article.getId(), articlePage.getItems().get(0).getId());
            }
        }
    }

    @Test
    void insertTest() {
        List<AbstractColumnMapping> columnMappings = new ArrayList<>();
        columnMappings.add(PropertyMapping.of(id, "id"));
        columnMappings.add(PropertyMapping.of(createTime, "createTime"));

        Article article1 = new Article();
        article1.setId(UUID.randomUUID().toString());
        InsertStatementProvider<Article> isp = DynamicSqlUtil.renderInsert(article1, table, columnMappings);
        assertEquals("insert into article (id, create_time) values (#{row.id}, #{row.createTime})", isp.getInsertStatement());

        Article article2 = new Article();
        article2.setId(UUID.randomUUID().toString());
        MultiRowInsertStatementProvider<Article> misp = DynamicSqlUtil.renderMultiInsert(List.of(article1, article2), table, columnMappings);
        assertEquals("insert into article (id, create_time) " +
                        "values (#{records[0].id}, #{records[0].createTime}), " +
                        "(#{records[1].id}, #{records[1].createTime})",
                misp.getInsertStatement());
    }

}
