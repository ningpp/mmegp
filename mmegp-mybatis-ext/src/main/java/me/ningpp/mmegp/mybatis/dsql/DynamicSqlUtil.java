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
import me.ningpp.mmegp.mybatis.dsql.pagination.Page;
import me.ningpp.mmegp.mybatis.dsql.pagination.PaginationModelRenderer;
import me.ningpp.mmegp.mybatis.dsql.pagination.PaginationSelectRenderer;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.insert.InsertModel;
import org.mybatis.dynamic.sql.insert.MultiRowInsertModel;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.aggregate.CountAll;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.AbstractColumnMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public final class DynamicSqlUtil {

    private DynamicSqlUtil() {
    }

    public static SelectStatementProvider renderSelect(SelectDSL<SelectModel> listDsl,
                                                       LimitOffset limitOffset,
                                                       PaginationModelRenderer paginationModelRender) {
        return new PaginationSelectRenderer(listDsl, limitOffset, paginationModelRender).render();
    }

    public static SelectStatementProvider renderSelect(SelectModel selectModel,
            LimitOffset limitOffset,
            PaginationModelRenderer paginationModelRender) {
        return new PaginationSelectRenderer(selectModel, limitOffset, paginationModelRender,
                RenderingStrategies.MYBATIS3, null, null).render();
    }

    public static <R> Page<R> selectPage(
            ToLongFunction<SelectStatementProvider> countMapper,
            Function<SelectStatementProvider, List<R>> listMapper,
            SelectDSL<SelectModel> listDsl,
            LimitOffset limitOffset,
            PaginationModelRenderer renderer) {
        return selectPage(countMapper, listMapper, toSelectCountModel(listDsl), listDsl.build(),
                limitOffset, renderer);
    }

    public static <R> Page<R> selectPage(
            ToLongFunction<SelectStatementProvider> countMapper,
            Function<SelectStatementProvider, List<R>> listMapper,
            SelectModel selectCountModel,
            SelectModel selectColumnsModel,
            LimitOffset limitOffset,
            PaginationModelRenderer renderer) {
        return selectPage(countMapper, listMapper,
                selectCountModel, selectColumnsModel,
                limitOffset, Page::of, renderer);
    }

    public static <R> Page<R> selectPage(
            ToLongFunction<SelectStatementProvider> countMapper,
            Function<SelectStatementProvider, List<R>> listMapper,
            SelectModel selectCountModel,
            SelectModel selectColumnsModel,
            LimitOffset limitOffset,
            BiFunction<List<R>, Long, Page<R>> pageFun,
            PaginationModelRenderer renderer) {
        long totalCount = countFrom(countMapper, selectCountModel);
        List<R> items;
        if (totalCount > 0L) {
            SelectStatementProvider selectStmtProvider = renderSelect(
                    selectColumnsModel,
                    limitOffset,
                    renderer
            );
            items  = listMapper.apply(selectStmtProvider);
        } else {
            items = new ArrayList<>(0);
        }
        return pageFun.apply(items, totalCount);
    }

    public static SelectModel toSelectCountModel(SelectDSL<SelectModel> dsl) {
        return SqlBuilder.select(new CountAll()).from(dsl, "_mmegp_").build();
    }

    public static long countFrom(ToLongFunction<SelectStatementProvider> mapper, SelectDSL<SelectModel> dsl) {
        return countFrom(mapper, toSelectCountModel(dsl));
    }

    public static long countFrom(ToLongFunction<SelectStatementProvider> mapper, SelectModel selectModel) {
        return mapper.applyAsLong(selectModel.render(RenderingStrategies.MYBATIS3));
    }

    public static <T> InsertStatementProvider<T> renderInsert(T row,SqlTable table,
            List<AbstractColumnMapping> columnMappings) {
        return renderInsert(InsertModel.withRow(row).withTable(table).withColumnMappings(columnMappings).build());
    }

    public static <T> InsertStatementProvider<T> renderInsert(InsertModel<T> insertModel) {
        return insertModel.render(RenderingStrategies.MYBATIS3);
    }

    public static <T> MultiRowInsertStatementProvider<T> renderMultiInsert(Collection<T> records,
            SqlTable table, List<AbstractColumnMapping> columnMappings) {
        return renderMultiInsert(
                    MultiRowInsertModel
                        .withRecords(records)
                        .withTable(table)
                        .withColumnMappings(columnMappings)
                    .build());
    }

    public static <T> MultiRowInsertStatementProvider<T> renderMultiInsert(
            MultiRowInsertModel<T> multiRowInsertModel) {
        return multiRowInsertModel.render(RenderingStrategies.MYBATIS3);
    }

}
