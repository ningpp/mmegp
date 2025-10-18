package me.ningpp.mmegp.dao;

import me.ningpp.mmegp.dss.SysCompanySimpleDynamicSqlSupport;
import me.ningpp.mmegp.entity.SysCompanySimple;
import me.ningpp.mmegp.rowmapper.SysCompanySimpleRowMapper;
import me.ningpp.mmegp.springjdbc.MmegpSpringDao;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.AbstractColumnMapping;
import org.mybatis.dynamic.sql.util.PropertyMapping;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static me.ningpp.mmegp.dss.SysCompanySimpleDynamicSqlSupport.id;
import static me.ningpp.mmegp.dss.SysCompanySimpleDynamicSqlSupport.name;
import static me.ningpp.mmegp.dss.SysCompanySimpleDynamicSqlSupport.marketCap;
import static me.ningpp.mmegp.dss.SysCompanySimpleDynamicSqlSupport.startDate;
import static me.ningpp.mmegp.dss.SysCompanySimpleDynamicSqlSupport.unifiedCode;

public class SysCompanySimpleDao extends MmegpSpringDao<SysCompanySimple, String, SysCompanySimpleDynamicSqlSupport.SysCompanySimple> {
    public SysCompanySimpleDao(NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public SysCompanySimpleDynamicSqlSupport.SysCompanySimple getTable() {
        return SysCompanySimpleDynamicSqlSupport.sysCompanySimple;
    }

    @Override
    public Class<String> getIdClass() {
        return String.class;
    }

    @Override
    public SqlColumn<String> getIdColumn() {
        return SysCompanySimpleDynamicSqlSupport.id;
    }

    @Override
    public RowMapper<SysCompanySimple> getAllColumnRowMapper() {
        return new SysCompanySimpleRowMapper();
    }

    @Override
    public BasicColumn[] getAllColumns() {
        return SysCompanySimpleDynamicSqlSupport.ALL_COLUMNS;
    }

    @Override
    public UpdateDSL<UpdateModel> updateColumnsExceptPk(SysCompanySimple row, UpdateDSL<UpdateModel> dsl, boolean selectiveUpdate) {
        if (selectiveUpdate) {
            return dsl.set(name).equalToWhenPresent(row::getName)
                    .set(startDate).equalToWhenPresent(row::getStartDate)
                    .set(marketCap).equalToWhenPresent(row::getMarketCap)
                    .set(unifiedCode).equalToWhenPresent(row::getUnifiedCode);
        } else {
            return dsl.set(name).equalTo(row::getName)
                    .set(startDate).equalTo(row::getStartDate)
                    .set(marketCap).equalTo(row::getMarketCap)
                    .set(unifiedCode).equalTo(row::getUnifiedCode);
        }
    }

    @Override
    public String getId(SysCompanySimple entity) {
        return entity.getId();
    }

    @Override
    public List<AbstractColumnMapping> columnMappings4Insert() {
        List<AbstractColumnMapping> columnMappings = new ArrayList<>();
        columnMappings.add(PropertyMapping.of(id, "id"));
        columnMappings.add(PropertyMapping.of(name, "name"));
        columnMappings.add(PropertyMapping.of(startDate, "startDate"));
        columnMappings.add(PropertyMapping.of(marketCap, "marketCap"));
        columnMappings.add(PropertyMapping.of(unifiedCode, "unifiedCode"));
        return columnMappings;
    }
}