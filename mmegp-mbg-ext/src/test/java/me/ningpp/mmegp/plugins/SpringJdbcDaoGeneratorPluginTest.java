package me.ningpp.mmegp.plugins;

import org.junit.jupiter.api.Test;
import org.mybatis.generator.api.IntrospectedTable;

import java.util.Properties;

import static me.ningpp.mmegp.plugins.SpringJdbcDaoGeneratorPlugin.getDaoType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpringJdbcDaoGeneratorPluginTest extends BasePluginTest {

    @Test
    void getDaoTypeTest() {
        IntrospectedTable table = buildTable("entity.SysUser");
        assertEquals("dao.SysUserDao",
                getDaoType(table, new Properties()).getFullyQualifiedName());
    }

    @Test
    void getDaoType2Test() {
        IntrospectedTable table = buildTable("me.ningpp.mmegpexample.entity.SysUser");
        assertEquals("me.ningpp.mmegpexample.dao.SysUserDao",
                getDaoType(table, new Properties()).getFullyQualifiedName());
    }

}
