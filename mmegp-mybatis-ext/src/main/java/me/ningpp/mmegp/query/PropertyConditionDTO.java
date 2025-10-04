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
package me.ningpp.mmegp.query;

import me.ningpp.mmegp.mybatis.dsql.DynamicSqlUtil;
import org.mybatis.dynamic.sql.BindableColumn;
import org.mybatis.dynamic.sql.ColumnAndConditionCriterion;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlCriterion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class PropertyConditionDTO<T> {
    private T equalTo;
    private T notEqualTo;
    private RangeDTO<T> betweenAnd;
    private RangeDTO<T> notBetweenAnd;
    private Collection<T> in;
    private Collection<T> notIn;
    private Boolean isNull;
    private Boolean isNotNull;
    private T like;
    private T notLike;
    private T less;
    private T lessEqual;
    private T greater;
    private T greaterEqual;

    public static <T> SqlCriterion toCriterion(BindableColumn<T> column, PropertyConditionDTO<T> dto) {
        if (dto == null) {
            return null;
        }
        return dto.toCriterion(column);
    }

    public SqlCriterion toCriterion(BindableColumn<T> column) {
        List<SqlCriterion> criterions = new ArrayList<>();
        criterions.add(DynamicSqlUtil.toEqualToCriterion(column, equalTo));
        criterions.add(DynamicSqlUtil.toNotEqualToCriterion(column, notEqualTo));
        criterions.add(DynamicSqlUtil.toBetweenAndCriterion(column, betweenAnd));
        criterions.add(DynamicSqlUtil.toNotBetweenAndCriterion(column, notBetweenAnd));
        criterions.add(DynamicSqlUtil.toInCriterion(column, in));
        criterions.add(DynamicSqlUtil.toNotInCriterion(column, notIn));
        criterions.add(DynamicSqlUtil.toIsNullCriterion(column, isNull));
        criterions.add(DynamicSqlUtil.toIsNotNullCriterion(column, isNotNull));
        criterions.add(DynamicSqlUtil.toLikeCriterion(column, like));
        criterions.add(DynamicSqlUtil.toNotLikeCriterion(column, notLike));
        criterions.add(DynamicSqlUtil.toLessCriterion(column, less));
        criterions.add(DynamicSqlUtil.toLessEqualCriterion(column, lessEqual));
        criterions.add(DynamicSqlUtil.toGreaterCriterion(column, greater));
        criterions.add(DynamicSqlUtil.toGreaterEqualCriterion(column, greaterEqual));
        return DynamicSqlUtil.buildCriteriaGroup(criterions);
    }

    public static void main(String[] args) {
        var fields = PropertyConditionDTO.class.getDeclaredFields();
        for (var field : fields) {
            String x = field.getName().substring(0, 1).toUpperCase(Locale.ROOT)
                    + field.getName().substring(1);
            System.out.println("criterions.add(DynamicSqlUtil.to"+x+"Criterion(column, "+field.getName()+"));");

        }
    }

    public static <T> SqlCriterion toEqualToCriterion(BindableColumn<T> column, T tvalue) {
        if (tvalue != null) {
            return ColumnAndConditionCriterion.withColumn(column).withCondition(SqlBuilder.isEqualTo(tvalue)).build();
        } else {
            return null;
        }
    }

    public static <T> PropertyConditionDTO<T> equalTo(T tvalue) {
        return new PropertyConditionDTO<T>().setEqualTo(tvalue);
    }

    public static <T> PropertyConditionDTO<T> notEqualTo(T tvalue) {
        return new PropertyConditionDTO<T>().setNotEqualTo(tvalue);
    }

    public static <T> PropertyConditionDTO<T> betweenAnd(RangeDTO<T> tvalue) {
        return new PropertyConditionDTO<T>().setBetweenAnd(tvalue);
    }

    public static <T> PropertyConditionDTO<T> notBetweenAnd(RangeDTO<T> tvalue) {
        return new PropertyConditionDTO<T>().setNotBetweenAnd(tvalue);
    }

    public static <T> PropertyConditionDTO<T> in(Collection<T> tvalue) {
        return new PropertyConditionDTO<T>().setIn(tvalue);
    }

    public static <T> PropertyConditionDTO<T> notIn(Collection<T> tvalue) {
        return new PropertyConditionDTO<T>().setNotIn(tvalue);
    }

    public static <T> PropertyConditionDTO<T> isNull(Boolean tvalue) {
        return new PropertyConditionDTO<T>().setNull(tvalue);
    }

    public static <T> PropertyConditionDTO<T> isNotNull(Boolean tvalue) {
        return new PropertyConditionDTO<T>().setNotNull(tvalue);
    }

    public static <T> PropertyConditionDTO<String> like(String tvalue) {
        return new PropertyConditionDTO<String>().setLike(tvalue);
    }

    public static <T> PropertyConditionDTO<String> notLike(String tvalue) {
        return new PropertyConditionDTO<String>().setNotLike(tvalue);
    }

    public static <T> PropertyConditionDTO<T> less(T tvalue) {
        return new PropertyConditionDTO<T>().setLess(tvalue);
    }

    public static <T> PropertyConditionDTO<T> lessEqual(T tvalue) {
        return new PropertyConditionDTO<T>().setLessEqual(tvalue);
    }

    public static <T> PropertyConditionDTO<T> greater(T tvalue) {
        return new PropertyConditionDTO<T>().setGreater(tvalue);
    }

    public static <T> PropertyConditionDTO<T> greaterEqual(T tvalue) {
        return new PropertyConditionDTO<T>().setGreaterEqual(tvalue);
    }


    public T getEqualTo() {
        return equalTo;
    }

    public PropertyConditionDTO<T> setEqualTo(T equalTo) {
        this.equalTo = equalTo;
        return this;
    }

    public T getNotEqualTo() {
        return notEqualTo;
    }

    public PropertyConditionDTO<T> setNotEqualTo(T notEqualTo) {
        this.notEqualTo = notEqualTo;
        return this;
    }

    public RangeDTO<T> getBetweenAnd() {
        return betweenAnd;
    }

    public PropertyConditionDTO<T> setBetweenAnd(RangeDTO<T> betweenAnd) {
        this.betweenAnd = betweenAnd;
        return this;
    }

    public RangeDTO<T> getNotBetweenAnd() {
        return notBetweenAnd;
    }

    public PropertyConditionDTO<T> setNotBetweenAnd(RangeDTO<T> notBetweenAnd) {
        this.notBetweenAnd = notBetweenAnd;
        return this;
    }

    public Collection<T> getIn() {
        return in;
    }

    public PropertyConditionDTO<T> setIn(Collection<T> in) {
        this.in = in;
        return this;
    }

    public Collection<T> getNotIn() {
        return notIn;
    }

    public PropertyConditionDTO<T> setNotIn(Collection<T> notIn) {
        this.notIn = notIn;
        return this;
    }

    public Boolean getNull() {
        return isNull;
    }

    public PropertyConditionDTO<T> setNull(Boolean aNull) {
        isNull = aNull;
        return this;
    }

    public Boolean getNotNull() {
        return isNotNull;
    }

    public PropertyConditionDTO<T> setNotNull(Boolean notNull) {
        isNotNull = notNull;
        return this;
    }

    public T getLike() {
        return like;
    }

    public PropertyConditionDTO<T> setLike(T like) {
        this.like = like;
        return this;
    }

    public T getNotLike() {
        return notLike;
    }

    public PropertyConditionDTO<T> setNotLike(T notLike) {
        this.notLike = notLike;
        return this;
    }

    public T getLess() {
        return less;
    }

    public PropertyConditionDTO<T> setLess(T less) {
        this.less = less;
        return this;
    }

    public T getLessEqual() {
        return lessEqual;
    }

    public PropertyConditionDTO<T> setLessEqual(T lessEqual) {
        this.lessEqual = lessEqual;
        return this;
    }

    public T getGreater() {
        return greater;
    }

    public PropertyConditionDTO<T> setGreater(T greater) {
        this.greater = greater;
        return this;
    }

    public T getGreaterEqual() {
        return greaterEqual;
    }

    public PropertyConditionDTO<T> setGreaterEqual(T greaterEqual) {
        this.greaterEqual = greaterEqual;
        return this;
    }
}
