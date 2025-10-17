package me.ningpp.mmegp.plugins;

import me.ningpp.mmegp.codegen.GenerateNothingMyBatis3IntrospectedTableImpl;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedTable;

class BasePluginTest {

    protected IntrospectedTable buildTable(String recordType) {
        IntrospectedTable table = new GenerateNothingMyBatis3IntrospectedTableImpl();
        table.setFullyQualifiedTable(new FullyQualifiedTable(
                null, null, "sys_user", "SysUser", null, true, null, null, "sys_user", false, null, null
        ));
        table.setBaseRecordType(recordType);
        return table;
    }

}
