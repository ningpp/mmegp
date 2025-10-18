package me.ningpp.mmegp.query;

import me.ningpp.mmegp.dss.SysAutoUserDynamicSqlSupport;
import me.ningpp.mmegp.mybatis.dsql.DynamicSqlUtil;
import me.ningpp.mmegp.mybatis.dsql.EntityQueryConditionDTO;
import me.ningpp.mmegp.mybatis.dsql.QueryDTO;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.CriteriaGroup;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.dynamic.sql.delete.DeleteModel;
import org.mybatis.dynamic.sql.select.SelectModel;

import java.util.ArrayList;
import java.util.List;

public class SysAutoUserQueryConditionDTO implements EntityQueryConditionDTO {
    private PropertyConditionDTO<Integer> id;

    private PropertyConditionDTO<String> firstName;

    private PropertyConditionDTO<String> lastName;

    private PropertyConditionDTO<Byte> deleted;

    public static SysAutoUserQueryConditionDTO null2Empty(SysAutoUserQueryConditionDTO dto) {
        return dto == null ? new SysAutoUserQueryConditionDTO() : dto;
    }

    @Override
    public CriteriaGroup buildCriteriaGroup() {
        List<SqlCriterion> criterions = new ArrayList<>();
        criterions.add(PropertyConditionDTO.toCriterion(SysAutoUserDynamicSqlSupport.id, id));
        criterions.add(PropertyConditionDTO.toCriterion(SysAutoUserDynamicSqlSupport.firstName, firstName));
        criterions.add(PropertyConditionDTO.toCriterion(SysAutoUserDynamicSqlSupport.lastName, lastName));
        criterions.add(PropertyConditionDTO.toCriterion(SysAutoUserDynamicSqlSupport.deleted, deleted));
        return DynamicSqlUtil.buildCriteriaGroup(criterions);
    }

    @Override
    public SelectModel toSelect(SortSpecification ... sortSpecs) {
        return toSelect(SysAutoUserDynamicSqlSupport.ALL_COLUMNS, sortSpecs);
    }

    @Override
    public SelectModel toSelect(BasicColumn[] columns, SortSpecification ... sortSpecs) {
        return conditions(QueryDTO.of(SysAutoUserDynamicSqlSupport.sysAutoUser).columns(columns).orderBy(sortSpecs)).toSelectModel();
    }

    @Override
    public DeleteModel toDelete() {
        return conditions(QueryDTO.of(SysAutoUserDynamicSqlSupport.sysAutoUser)).toDeleteModel();
    }

    private QueryDTO<SysAutoUserDynamicSqlSupport.SysAutoUser> conditions(QueryDTO<SysAutoUserDynamicSqlSupport.SysAutoUser> dto) {
        commonCondition(dto, SysAutoUserDynamicSqlSupport.id, id);
        stringCondition(dto, SysAutoUserDynamicSqlSupport.firstName, firstName);
        stringCondition(dto, SysAutoUserDynamicSqlSupport.lastName, lastName);
        commonCondition(dto, SysAutoUserDynamicSqlSupport.deleted, deleted);
        return dto;
    }

    public SysAutoUserQueryConditionDTO id(PropertyConditionDTO<Integer> id) {
        this.id = id;
        return this;
    }

    public SysAutoUserQueryConditionDTO firstName(PropertyConditionDTO<String> firstName) {
        this.firstName = firstName;
        return this;
    }

    public SysAutoUserQueryConditionDTO lastName(PropertyConditionDTO<String> lastName) {
        this.lastName = lastName;
        return this;
    }

    public SysAutoUserQueryConditionDTO deleted(PropertyConditionDTO<Byte> deleted) {
        this.deleted = deleted;
        return this;
    }

    public PropertyConditionDTO<Integer> getId() {
        return id;
    }

    public PropertyConditionDTO<String> getFirstName() {
        return firstName;
    }

    public PropertyConditionDTO<String> getLastName() {
        return lastName;
    }

    public PropertyConditionDTO<Byte> getDeleted() {
        return deleted;
    }
}