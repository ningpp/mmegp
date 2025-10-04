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
import org.mybatis.dynamic.sql.SqlCriterion;

import java.util.ArrayList;
import java.util.List;

public class EntityCriteriaDTO {

    private List<EntityCriteriaNodeDTO> children;
    private SqlOperator operator;

    public EntityCriteriaDTO() {
    }

    public EntityCriteriaDTO(List<EntityCriteriaNodeDTO> children, SqlOperator operator) {
        this.children = children;
        this.operator = operator;
    }

    public List<EntityCriteriaNodeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<EntityCriteriaNodeDTO> children) {
        this.children = children;
    }

    public SqlOperator getOperator() {
        return operator;
    }

    public void setOperator(SqlOperator operator) {
        this.operator = operator;
    }

    public <R extends AliasableSqlTable<R>> QueryDTO<R> toQuery(R sqlTable) {
        QueryDTO<R> query = QueryDTO.of(sqlTable);
        if (children != null && !children.isEmpty()) {
            SqlCriterion initialCriterion = children.get(0).buildCriteriaGroup();

            List<AndOrCriteriaGroup> subCriterias = new ArrayList<>();
            for (int i = 1; i < children.size(); i++) {
                subCriterias.add(new AndOrCriteriaGroup.Builder()
                    .withInitialCriterion(children.get(i).buildCriteriaGroup())
                    .withConnector(getOperator().getCode())
                    .withSubCriteria(List.of())
                .build());
            }

            query.criteriaGroup(new AndOrCriteriaGroup.Builder()
                .withInitialCriterion(initialCriterion)
                .withConnector(getOperator().getCode())
                .withSubCriteria(subCriterias)
            .build());
        }
        return query;
    }

}
