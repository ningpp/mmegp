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
package me.ningpp.mmegp.mybatis.dsql;

import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.AndOrCriteriaGroup;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.BindableColumn;
import org.mybatis.dynamic.sql.ColumnAndConditionCriterion;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.dynamic.sql.VisitableCondition;
import org.mybatis.dynamic.sql.delete.DeleteModel;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueryDTO<R extends AliasableSqlTable<R>> {

    private final AliasableSqlTable<R> root;

    private List<BasicColumn> selectList;

    private List<SortSpecification> orderBys;

    private final List<AndOrCriteriaGroup> subCriteria = new ArrayList<>();

    protected QueryDTO(AliasableSqlTable<R> root) {
        this.root = root;
    }

    public static <R extends AliasableSqlTable<R>> QueryDTO<R> of(AliasableSqlTable<R> root) {
        return new QueryDTO<>(root);
    }

    public QueryDTO<R> andLike(BindableColumn<String> column, String str) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isLike(str)));
        return this;
    }

    public QueryDTO<R> andNotLike(BindableColumn<String> column, String str) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isNotLike(str)));
        return this;
    }

    public <S> QueryDTO<R> andEqual(BindableColumn<S> column, S svalue) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isEqualTo(svalue)));
        return this;
    }

    public <S> QueryDTO<R> andNotEqual(BindableColumn<S> column, S svalue) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isNotEqualTo(svalue)));
        return this;
    }

    public <S> QueryDTO<R> andBetween(BindableColumn<S> column, S minValue, S maxValue) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isBetween(minValue).and(maxValue)));
        return this;
    }

    public <S> QueryDTO<R> andNotBetween(BindableColumn<S> column, S minValue, S maxValue) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isNotBetween(minValue).and(maxValue)));
        return this;
    }

    public <S> QueryDTO<R> andIsNull(BindableColumn<S> column) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isNull()));
        return this;
    }

    public <S> QueryDTO<R> andIsNotNull(BindableColumn<S> column) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isNotNull()));
        return this;
    }

    public <S> QueryDTO<R> andLess(BindableColumn<S> column, S svalue) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isLessThan(svalue)));
        return this;
    }

    public <S> QueryDTO<R> andLessEqual(BindableColumn<S> column, S svalue) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isLessThanOrEqualTo(svalue)));
        return this;
    }

    public <S> QueryDTO<R> andGreater(BindableColumn<S> column, S svalue) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isGreaterThan(svalue)));
        return this;
    }

    public <S> QueryDTO<R> andGreaterEqual(BindableColumn<S> column, S svalue) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isGreaterThanOrEqualTo(svalue)));
        return this;
    }

    public <S> QueryDTO<R> andIn(BindableColumn<S> column, S... values) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isIn(values)));
        return this;
    }

    public <S> QueryDTO<R> andIn(BindableColumn<S> column, Collection<S> values) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isIn(values)));
        return this;
    }

    public <S> QueryDTO<R> andNotIn(BindableColumn<S> column, S... values) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isNotIn(values)));
        return this;
    }

    public <S> QueryDTO<R> andNotIn(BindableColumn<S> column, Collection<S> values) {
        andSubCriteria(buildCriterion(column, SqlBuilder.isNotIn(values)));
        return this;
    }

    private <S> SqlCriterion buildCriterion(BindableColumn<S> column, VisitableCondition<S> condition) {
        return ColumnAndConditionCriterion.withColumn(column).withCondition(condition).build();
    }

    private void andSubCriteria(SqlCriterion initialCriterion) {
        addSubCriteria("and", initialCriterion);
    }

    private void orSubCriteria(SqlCriterion initialCriterion) {
        addSubCriteria("or", initialCriterion);
    }

    public void criteriaGroup(AndOrCriteriaGroup group) {
        this.subCriteria.add(group);
    }

    private void addSubCriteria(String connector, SqlCriterion initialCriterion) {
        this.subCriteria.add(new AndOrCriteriaGroup.Builder()
                .withInitialCriterion(initialCriterion)
                .withConnector(connector)
                .withSubCriteria(List.of())
                .build());
    }

    public QueryDTO<R> columns(BasicColumn... selectList) {
        this.selectList = List.of(selectList);
        return this;
    }

    public QueryDTO<R> orderBy(SortSpecification... columns) {
        orderBys = List.of(columns);
        return this;
    }

    public SelectModel toSelectModel() {
        QueryExpressionDSL<SelectModel> from = SqlBuilder.select(selectList)
                .from(root);

        SelectModel selectModel;
        boolean hasOrderBy = orderBys != null && !orderBys.isEmpty();
        if (subCriteria.isEmpty()) {
            if (hasOrderBy) {
                selectModel = from.orderBy(orderBys).build();
            } else {
                selectModel = from.build();
            }
        } else {
            if (hasOrderBy) {
                selectModel = from.where(subCriteria).orderBy(orderBys).build();
            } else {
                selectModel = from.where(subCriteria).build();
            }
        }
        return selectModel;
    }

    public DeleteStatementProvider toDelete() {
        return toDelete(RenderingStrategies.MYBATIS3);
    }

    public DeleteStatementProvider toDelete(RenderingStrategy strategy) {
        return toDeleteModel().render(strategy);
    }

    public SelectStatementProvider toSelect() {
        return toSelect(RenderingStrategies.MYBATIS3);
    }

    public SelectStatementProvider toSelect(RenderingStrategy strategy) {
        return toSelectModel().render(strategy);
    }

    public DeleteModel toDeleteModel() {
        if (subCriteria.isEmpty()) {
            return SqlBuilder.deleteFrom(root).build();
        } else {
            return SqlBuilder.deleteFrom(root).where(subCriteria).build();
        }
    }

}
