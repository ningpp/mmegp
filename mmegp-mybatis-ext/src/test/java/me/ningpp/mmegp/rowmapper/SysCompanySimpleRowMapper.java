package me.ningpp.mmegp.rowmapper;

import me.ningpp.mmegp.dss.SysCompanySimpleDynamicSqlSupport;
import me.ningpp.mmegp.entity.SysCompanySimple;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class SysCompanySimpleRowMapper implements RowMapper<SysCompanySimple> {
    @Override
    public SysCompanySimple mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
        var entity = new SysCompanySimple();
        entity.setId(rs.getObject(SysCompanySimpleDynamicSqlSupport.id.name(), String.class));
        entity.setName(rs.getObject(SysCompanySimpleDynamicSqlSupport.name.name(), String.class));
        entity.setStartDate(rs.getObject(SysCompanySimpleDynamicSqlSupport.startDate.name(), LocalDate.class));
        entity.setMarketCap(rs.getObject(SysCompanySimpleDynamicSqlSupport.marketCap.name(), BigDecimal.class));
        entity.setUnifiedCode(rs.getObject(SysCompanySimpleDynamicSqlSupport.unifiedCode.name(), String.class));
        return entity;
    }
}