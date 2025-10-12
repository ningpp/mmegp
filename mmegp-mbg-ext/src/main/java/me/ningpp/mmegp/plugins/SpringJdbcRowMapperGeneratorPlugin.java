package me.ningpp.mmegp.plugins;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class SpringJdbcRowMapperGeneratorPlugin extends MmegpPluginAdapter {

    @Override
    public List<CompilationUnit> generateCompilationUnits(IntrospectedTable introspectedTable) {
        return List.of(generateRowMapper(introspectedTable));
    }

    public static FullyQualifiedJavaType getRowMapperType(IntrospectedTable introspectedTable, Properties properties) {
        String domainPackage = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getPackageName();
        String defaultPackage = domainPackage.lastIndexOf(".") == -1
                ? "rowmapper" : domainPackage.substring(0, domainPackage.lastIndexOf(".")) + ".rowmapper";
        return new FullyQualifiedJavaType(String.format(Locale.ROOT, "%s.%s%s",
                properties.getProperty("package", defaultPackage),
                introspectedTable.getFullyQualifiedTable().getDomainObjectName(),
                properties.getProperty("rowMapperNameSuffix", "RowMapper")));
    }

    private TopLevelClass generateRowMapper(IntrospectedTable introspectedTable) {
        TopLevelClass tlc = new TopLevelClass(getRowMapperType(introspectedTable, this.properties));
        tlc.setVisibility(JavaVisibility.PUBLIC);
        tlc.addImportedType("org.springframework.jdbc.core.RowMapper");
        tlc.addImportedType(introspectedTable.getBaseRecordType());
        tlc.addImportedType(new FullyQualifiedJavaType(SQLException.class.getName()));
        var dssType = new FullyQualifiedJavaType(introspectedTable.getMyBatisDynamicSqlSupportType());
        tlc.addImportedType(dssType);
        var entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        tlc.addImportedType(entityType);
        tlc.addSuperInterface(new FullyQualifiedJavaType(
                String.format(Locale.ROOT, "RowMapper<%s>", entityType.getShortName())));
        Method method = new Method("mapRow");
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(entityType);

        String rsParamName = "rs";
        method.addParameter(new Parameter(new FullyQualifiedJavaType(ResultSet.class.getName()), rsParamName));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "rowNum"));

        method.addException(new FullyQualifiedJavaType(SQLException.class.getSimpleName()));

        String varName = "entity";
        method.addBodyLine(String.format(Locale.ROOT, "var %s = new %s();",
                varName, entityType.getShortName()));
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        for (IntrospectedColumn column : columns) {
            tlc.addImportedType(column.getFullyQualifiedJavaType());
            if (StringUtils.isBlank(column.getTypeHandler())) {
                method.addBodyLine(setEntityValueBodyLine(rsParamName, varName, column, dssType.getShortName()));
            } else {
                tlc.addImportedType(column.getTypeHandler());
                method.addBodyLine(String.format(Locale.ROOT, "%s.%s(new %s().getNullableResult(%s, %s.%s.name()));",
                        varName, JavaBeansUtil.getSetterMethodName(column.getJavaProperty()),
                        new FullyQualifiedJavaType(column.getTypeHandler()).getShortName(),
                        rsParamName, dssType.getShortName(), column.getJavaProperty(),
                        column.getFullyQualifiedJavaType().getShortName()));
            }
        }
        method.addBodyLine(String.format(Locale.ROOT, "return %s;", varName));
        tlc.addMethod(method);
        return tlc;
    }

    private String setEntityValueBodyLine(String rsParamName, String varName,
            IntrospectedColumn column, String dssName) {
        return String.format(Locale.ROOT, "%s.%s(%s.getObject(%s.%s.name(), %s.class));",
                varName, JavaBeansUtil.getSetterMethodName(column.getJavaProperty()),
                rsParamName, dssName, column.getJavaProperty(),
                column.getFullyQualifiedJavaType().getShortName());
    }

}
