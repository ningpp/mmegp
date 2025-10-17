package me.ningpp.mmegp.plugins;

import org.junit.jupiter.api.Test;
import org.mybatis.generator.api.IntrospectedTable;

import java.util.Properties;

import static me.ningpp.mmegp.plugins.SpringJdbcRowMapperGeneratorPlugin.getRowMapperType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpringJdbcRowMapperGeneratorPluginTest extends BasePluginTest {

    @Test
    void getDaoTypeTest() {
        IntrospectedTable table = buildTable("entity.SysUser");
        assertEquals("rowmapper.SysUserRowMapper",
                getRowMapperType(table, new Properties()).getFullyQualifiedName());
    }

    @Test
    void getDaoType2Test() {
        IntrospectedTable table = buildTable("me.ningpp.mmegpexample.entity.SysUser");
        assertEquals("me.ningpp.mmegpexample.rowmapper.SysUserRowMapper",
                getRowMapperType(table, new Properties()).getFullyQualifiedName());
    }
}
