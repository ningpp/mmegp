package me.ningpp.mmegp.rowmapper;

import me.ningpp.mmegp.entity.SysAutoUser;
import me.ningpp.mmegp.dss.SysAutoUserDynamicSqlSupport;
import org.springframework.jdbc.core.RowMapper;

import java.sql.SQLException;

public class SysAutoUserRowMapper implements RowMapper<SysAutoUser> {
    @Override
    public SysAutoUser mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
        var entity = new SysAutoUser();
        entity.setId(rs.getObject(SysAutoUserDynamicSqlSupport.id.name(), Integer.class));
        entity.setFirstName(rs.getObject(SysAutoUserDynamicSqlSupport.firstName.name(), String.class));
        entity.setLastName(rs.getObject(SysAutoUserDynamicSqlSupport.lastName.name(), String.class));
        entity.setDeleted(rs.getObject(SysAutoUserDynamicSqlSupport.deleted.name(), Byte.class));
        return entity;
    }
}