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

import me.ningpp.mmegp.query.PropertyConditionDTO;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.delete.DeleteModel;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.aggregate.CountAll;
import org.mybatis.dynamic.sql.select.aggregate.CountDistinct;

public interface EntityQueryConditionDTO {

    default SelectModel toSelectCount() {
        return toSelect(new BasicColumn[]{new CountAll()});
    }

    default SelectModel toSelectCountDistinct(BasicColumn column) {
        return toSelect(new BasicColumn[]{CountDistinct.of(column)});
    }

    SelectModel toSelect(SortSpecification... sortSpecs);

    SelectModel toSelect(BasicColumn[] columns, SortSpecification... sortSpecs);

    DeleteModel toDelete();

    default <R extends AliasableSqlTable<R>> void stringCondition(QueryDTO<R> queryDto,
            SqlColumn<String> column, PropertyConditionDTO<String> condition) {
        if (condition == null) {
            return;
        }
        commonCondition(queryDto, column, condition);

        conditionLike(queryDto, column, condition);
        conditionNotLike(queryDto, column, condition);
    }

    default <T, R extends AliasableSqlTable<R>> void commonCondition(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }
        conditionEqual(queryDto, column, condition);
        conditionNotEqual(queryDto, column, condition);

        conditionIn(queryDto, column, condition);
        conditionNotIn(queryDto, column, condition);

        conditionBetweenAnd(queryDto, column, condition);
        conditionNotBetweenAnd(queryDto, column, condition);

        conditionLess(queryDto, column, condition);
        conditionLessEqual(queryDto, column, condition);
        conditionGreater(queryDto, column, condition);
        conditionGreaterEqual(queryDto, column, condition);

        conditionIsNull(queryDto, column, condition);
        conditionIsNotNull(queryDto, column, condition);
    }

    default <T, R extends AliasableSqlTable<R>> void conditionEqual(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getEqualTo() != null) {
            queryDto.andEqual(column, condition.getEqualTo());
        }
    }

    default <T, R extends AliasableSqlTable<R>> void conditionNotEqual(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getNotEqualTo() != null) {
            queryDto.andNotEqual(column, condition.getNotEqualTo());
        }
    }

    default <T, R extends AliasableSqlTable<R>> void conditionBetweenAnd(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getBetweenAnd() != null
                && condition.getBetweenAnd().getMin() != null
                && condition.getBetweenAnd().getMax() != null) {
            queryDto.andBetween(column, condition.getBetweenAnd().getMin(), condition.getBetweenAnd().getMax());
        }
    }

    default <T, R extends AliasableSqlTable<R>> void conditionNotBetweenAnd(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getNotBetweenAnd() != null
                && condition.getNotBetweenAnd().getMin() != null
                && condition.getNotBetweenAnd().getMax() != null) {
            queryDto.andNotBetween(column, condition.getNotBetweenAnd().getMin(), condition.getNotBetweenAnd().getMax());
        }
    }

    default <T, R extends AliasableSqlTable<R>> void conditionIsNotNull(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (Boolean.TRUE.equals(condition.getNotNull())) {
            queryDto.andIsNotNull(column);
        }
    }

    default <T, R extends AliasableSqlTable<R>> void conditionIsNull(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (Boolean.TRUE.equals(condition.getNull())) {
            queryDto.andIsNull(column);
        }
    }

    default <T, R extends AliasableSqlTable<R>> void conditionIn(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getIn() != null && !condition.getIn().isEmpty()) {
            queryDto.andIn(column, condition.getIn());
        }
    }

    default <T, R extends AliasableSqlTable<R>> void conditionNotIn(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getNotIn() != null && !condition.getNotIn().isEmpty()) {
            queryDto.andNotIn(column, condition.getNotIn());
        }
    }

    default <R extends AliasableSqlTable<R>> void conditionLike(QueryDTO<R> queryDto,
            SqlColumn<String> column, PropertyConditionDTO<String> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getLike() != null) {
            queryDto.andLike(column, condition.getLike());
        }
    }

    default <R extends AliasableSqlTable<R>> void conditionNotLike(QueryDTO<R> queryDto,
            SqlColumn<String> column, PropertyConditionDTO<String> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getNotLike() != null) {
            queryDto.andNotLike(column, condition.getNotLike());
        }
    }

    default <T, R extends AliasableSqlTable<R>> void conditionLess(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getLess() != null) {
            queryDto.andLess(column, condition.getLess());
        }
    }

    default <T, R extends AliasableSqlTable<R>> void conditionLessEqual(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getLessEqual() != null) {
            queryDto.andLessEqual(column, condition.getLessEqual());
        }
    }

    default <T, R extends AliasableSqlTable<R>> void conditionGreater(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getGreater() != null) {
            queryDto.andGreater(column, condition.getGreater());
        }
    }

    default <T, R extends AliasableSqlTable<R>> void conditionGreaterEqual(QueryDTO<R> queryDto,
            SqlColumn<T> column, PropertyConditionDTO<T> condition) {
        if (condition == null) {
            return;
        }

        if (condition.getGreaterEqual() != null) {
            queryDto.andGreaterEqual(column, condition.getGreaterEqual());
        }
    }

}
