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

import org.mybatis.dynamic.sql.AndOrCriteriaGroup;
import org.mybatis.dynamic.sql.CriteriaGroup;

import java.util.List;

public class EntityCriteriaNodeDTO {

    private EntityQueryConditionDTO condition;
    private List<EntityCriteriaNodeDTO> subGroups;
    private SqlOperator operator;

    public EntityCriteriaNodeDTO() {
    }

    public EntityCriteriaNodeDTO(EntityQueryConditionDTO condition,
            List<EntityCriteriaNodeDTO> subGroups, SqlOperator operator) {
        this.condition = condition;
        this.subGroups = subGroups;
        this.operator = operator;
    }

    public EntityQueryConditionDTO getCondition() {
        return condition;
    }

    public void setCondition(EntityQueryConditionDTO condition) {
        this.condition = condition;
    }

    public List<EntityCriteriaNodeDTO> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<EntityCriteriaNodeDTO> subGroups) {
        this.subGroups = subGroups;
    }

    public SqlOperator getOperator() {
        return operator;
    }

    public void setOperator(SqlOperator operator) {
        this.operator = operator;
    }

    public CriteriaGroup buildCriteriaGroup() {
        if (condition == null) {
            throw new IllegalArgumentException("condition can't be null!");
        }
        CriteriaGroup cg = condition.buildCriteriaGroup();
        if (subGroups == null || subGroups.isEmpty()) {
            return cg;
        } else {
            CriteriaGroup.Builder builder = new CriteriaGroup.Builder();
            builder.withInitialCriterion(cg);
            builder.withSubCriteria(subGroups.stream()
                    .map(node -> new AndOrCriteriaGroup.Builder()
                            .withConnector(operator.getCode())
                            .withInitialCriterion(node.buildCriteriaGroup()).build()
                    ).toList());
            return builder.build();
        }
    }

    public static EntityCriteriaNodeDTO of(EntityQueryConditionDTO dto) {
        return new EntityCriteriaNodeDTO(dto, List.of(), null);
    }

}
