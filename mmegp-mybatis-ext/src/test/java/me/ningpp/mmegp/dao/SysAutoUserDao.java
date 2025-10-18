package me.ningpp.mmegp.dao;

import me.ningpp.mmegp.entity.SysAutoUser;
import me.ningpp.mmegp.rowmapper.SysAutoUserRowMapper;
import me.ningpp.mmegp.dss.SysAutoUserDynamicSqlSupport;
import me.ningpp.mmegp.springjdbc.MmegpSpringDao;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.util.AbstractColumnMapping;
import org.mybatis.dynamic.sql.util.PropertyMapping;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static me.ningpp.mmegp.dss.SysAutoUserDynamicSqlSupport.deleted;
import static me.ningpp.mmegp.dss.SysAutoUserDynamicSqlSupport.firstName;
import static me.ningpp.mmegp.dss.SysAutoUserDynamicSqlSupport.lastName;

public class SysAutoUserDao extends MmegpSpringDao<SysAutoUser, Integer, SysAutoUserDynamicSqlSupport.SysAutoUser> {
    public SysAutoUserDao(NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public SysAutoUserDynamicSqlSupport.SysAutoUser getTable() {
        return SysAutoUserDynamicSqlSupport.sysAutoUser;
    }

    @Override
    public Class<Integer> getIdClass() {
        return Integer.class;
    }

    @Override
    public SqlColumn<Integer> getIdColumn() {
        return SysAutoUserDynamicSqlSupport.id;
    }

    @Override
    public BiConsumer<SysAutoUser, Integer> getAutoIncrementConsumer() {
        return SysAutoUser::setId;
    }

    @Override
    public RowMapper<SysAutoUser> getAllColumnRowMapper() {
        return new SysAutoUserRowMapper();
    }

    @Override
    public BasicColumn[] getAllColumns() {
        return SysAutoUserDynamicSqlSupport.ALL_COLUMNS;
    }

    @Override
    public List<AbstractColumnMapping> columnMappings4Insert() {
        List<AbstractColumnMapping> columnMappings = new ArrayList<>();
        columnMappings.add(PropertyMapping.of(firstName, "firstName"));
        columnMappings.add(PropertyMapping.of(lastName, "lastName"));
        columnMappings.add(PropertyMapping.of(deleted, "deleted"));
        return columnMappings;
    }
}