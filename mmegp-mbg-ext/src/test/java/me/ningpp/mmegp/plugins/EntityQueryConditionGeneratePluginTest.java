package me.ningpp.mmegp.plugins;

import org.junit.jupiter.api.Test;
import org.mybatis.generator.api.IntrospectedTable;

import java.util.Properties;

import static me.ningpp.mmegp.plugins.EntityQueryConditionGeneratePlugin.getEntityQueryConditionType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityQueryConditionGeneratePluginTest extends BasePluginTest {

    @Test
    void getEntityQueryConditionTypeTest() {
        IntrospectedTable table = buildTable("entity.SysUser");
        assertEquals("query.SysUserQueryConditionDTO",
                getEntityQueryConditionType(table, new Properties()).getFullyQualifiedName());
    }

    @Test
    void getEntityQueryConditionType2Test() {
        IntrospectedTable table = buildTable("me.ningpp.mmegpexample.entity.SysUser");
        assertEquals("me.ningpp.mmegpexample.query.SysUserQueryConditionDTO",
                getEntityQueryConditionType(table, new Properties()).getFullyQualifiedName());
    }

}
