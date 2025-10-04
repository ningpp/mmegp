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
import me.ningpp.mmegp.query.RangeDTO;
import org.mybatis.dynamic.sql.AndOrCriteriaGroup.Builder;
import org.mybatis.dynamic.sql.BindableColumn;
import org.mybatis.dynamic.sql.ColumnAndConditionCriterion;
import org.mybatis.dynamic.sql.CriteriaGroup;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlCriterion;
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
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public final class DynamicSqlUtil {

    private DynamicSqlUtil() {
    }

    public static CriteriaGroup buildCriteriaGroup(List<SqlCriterion> criterions) {
        if (criterions == null) {
            return null;
        }
        List<SqlCriterion> notNullCriterions = criterions.stream().filter(Objects::nonNull).toList();
        if (notNullCriterions.isEmpty()) {
            return null;
        }

        CriteriaGroup.Builder builder = new CriteriaGroup.Builder();
        builder.withInitialCriterion(notNullCriterions.get(0));
        if (notNullCriterions.size() > 1) {
            builder.withSubCriteria(notNullCriterions.subList(1, notNullCriterions.size()).stream()
                .map(c -> new Builder().withConnector(SqlOperator.AND.getCode()).withInitialCriterion(c).build()).toList());
        }
        return builder.build();
    }

    public static <T> SqlCriterion toEqualToCriterion(BindableColumn<T> column, T tvalue) {
        if (tvalue != null) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isEqualTo(tvalue)).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toNotEqualToCriterion(BindableColumn<T> column, T tvalue) {
        if (tvalue != null) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isNotEqualTo(tvalue)).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toBetweenAndCriterion(BindableColumn<T> column, RangeDTO<T> tvalue) {
        if (tvalue != null && tvalue.getMin() != null && tvalue.getMax() != null) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(
                    SqlBuilder.isBetween(tvalue.getMin()).and(tvalue.getMax())).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toNotBetweenAndCriterion(BindableColumn<T> column, RangeDTO<T> tvalue) {
        if (tvalue != null && tvalue.getMin() != null && tvalue.getMax() != null) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(
                    SqlBuilder.isNotBetween(tvalue.getMin()).and(tvalue.getMax())).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toInCriterion(BindableColumn<T> column, Collection<T> tvalue) {
        if (tvalue != null) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isIn(tvalue)).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toNotInCriterion(BindableColumn<T> column, Collection<T> tvalue) {
        if (tvalue != null) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isNotIn(tvalue)).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toIsNullCriterion(BindableColumn<T> column, Boolean tvalue) {
        if (Boolean.TRUE.equals(tvalue)) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isNull()).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toIsNotNullCriterion(BindableColumn<T> column, Boolean tvalue) {
        if (Boolean.TRUE.equals(tvalue)) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isNotNull()).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toLikeCriterion(BindableColumn<T> column, T tvalue) {
        if (tvalue instanceof String str && !str.isEmpty()) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isLike(tvalue)).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toNotLikeCriterion(BindableColumn<T> column, T tvalue) {
        if (tvalue instanceof String str && !str.isEmpty()) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isNotLike(tvalue)).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toLessCriterion(BindableColumn<T> column, T tvalue) {
        if (tvalue != null) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isLessThan(tvalue)).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toLessEqualCriterion(BindableColumn<T> column, T tvalue) {
        if (tvalue != null) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isLessThanOrEqualTo(tvalue)).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toGreaterCriterion(BindableColumn<T> column, T tvalue) {
        if (tvalue != null) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isGreaterThan(tvalue)).build();
        } else {
            return null;
        }
    }

    public static <T> SqlCriterion toGreaterEqualCriterion(BindableColumn<T> column, T tvalue) {
        if (tvalue != null) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isGreaterThanOrEqualTo(tvalue)).build();
        } else {
            return null;
        }
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
