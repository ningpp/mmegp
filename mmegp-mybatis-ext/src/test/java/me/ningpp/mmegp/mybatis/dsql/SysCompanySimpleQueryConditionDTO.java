package me.ningpp.mmegp.mybatis.dsql;

import me.ningpp.mmegp.query.PropertyConditionDTO;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.CriteriaGroup;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.dynamic.sql.delete.DeleteModel;
import org.mybatis.dynamic.sql.select.SelectModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SysCompanySimpleQueryConditionDTO implements EntityQueryConditionDTO {
    private PropertyConditionDTO<String> id;

    private PropertyConditionDTO<String> name;

    private PropertyConditionDTO<LocalDate> startDate;

    private PropertyConditionDTO<BigDecimal> marketCap;

    private PropertyConditionDTO<String> unifiedCode;

    public static SysCompanySimpleQueryConditionDTO null2Empty(SysCompanySimpleQueryConditionDTO dto) {
        return dto == null ? new SysCompanySimpleQueryConditionDTO() : dto;
    }

    @Override
    public CriteriaGroup buildCriteriaGroup() {
        List<SqlCriterion> criterions = new ArrayList<>();
        criterions.add(PropertyConditionDTO.toCriterion(SysCompanySimpleDynamicSqlSupport.id, id));
        criterions.add(PropertyConditionDTO.toCriterion(SysCompanySimpleDynamicSqlSupport.name, name));
        criterions.add(PropertyConditionDTO.toCriterion(SysCompanySimpleDynamicSqlSupport.startDate, startDate));
        criterions.add(PropertyConditionDTO.toCriterion(SysCompanySimpleDynamicSqlSupport.marketCap, marketCap));
        criterions.add(PropertyConditionDTO.toCriterion(SysCompanySimpleDynamicSqlSupport.unifiedCode, unifiedCode));
        return DynamicSqlUtil.buildCriteriaGroup(criterions);
    }

    @Override
    public SelectModel toSelect(SortSpecification ... sortSpecs) {
        return toSelect(SysCompanySimpleDynamicSqlSupport.ALL_COLUMNS, sortSpecs);
    }

    @Override
    public SelectModel toSelect(BasicColumn[] columns, SortSpecification ... sortSpecs) {
        return conditions(QueryDTO.of(SysCompanySimpleDynamicSqlSupport.sysCompanySimple).columns(columns).orderBy(sortSpecs)).toSelectModel();
    }

    @Override
    public DeleteModel toDelete() {
        return conditions(QueryDTO.of(SysCompanySimpleDynamicSqlSupport.sysCompanySimple)).toDeleteModel();
    }

    private QueryDTO<SysCompanySimpleDynamicSqlSupport.SysCompanySimple> conditions(QueryDTO<SysCompanySimpleDynamicSqlSupport.SysCompanySimple> dto) {
        stringCondition(dto, SysCompanySimpleDynamicSqlSupport.id, id);
        stringCondition(dto, SysCompanySimpleDynamicSqlSupport.name, name);
        commonCondition(dto, SysCompanySimpleDynamicSqlSupport.startDate, startDate);
        commonCondition(dto, SysCompanySimpleDynamicSqlSupport.marketCap, marketCap);
        stringCondition(dto, SysCompanySimpleDynamicSqlSupport.unifiedCode, unifiedCode);
        return dto;
    }

    public SysCompanySimpleQueryConditionDTO id(PropertyConditionDTO<String> id) {
        this.id = id;
        return this;
    }

    public SysCompanySimpleQueryConditionDTO name(PropertyConditionDTO<String> name) {
        this.name = name;
        return this;
    }

    public SysCompanySimpleQueryConditionDTO startDate(PropertyConditionDTO<LocalDate> startDate) {
        this.startDate = startDate;
        return this;
    }

    public SysCompanySimpleQueryConditionDTO marketCap(PropertyConditionDTO<BigDecimal> marketCap) {
        this.marketCap = marketCap;
        return this;
    }

    public SysCompanySimpleQueryConditionDTO unifiedCode(PropertyConditionDTO<String> unifiedCode) {
        this.unifiedCode = unifiedCode;
        return this;
    }

    public PropertyConditionDTO<String> getId() {
        return id;
    }

    public PropertyConditionDTO<String> getName() {
        return name;
    }

    public PropertyConditionDTO<LocalDate> getStartDate() {
        return startDate;
    }

    public PropertyConditionDTO<BigDecimal> getMarketCap() {
        return marketCap;
    }

    public PropertyConditionDTO<String> getUnifiedCode() {
        return unifiedCode;
    }
}