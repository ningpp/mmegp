package me.ningpp.mmegp.plugins;

import me.ningpp.mmegp.dsql.MmegpDynamicSqlMapperGenerator;
import me.ningpp.mmegp.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.runtime.dynamic.sql.elements.FragmentGenerator;
import org.mybatis.generator.runtime.dynamic.sql.elements.MethodAndImports;

import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.function.BiConsumer;

import static me.ningpp.mmegp.MyBatisGeneratorUtil.getOnlyOnePkColumn;
import static me.ningpp.mmegp.plugins.SpringJdbcRowMapperGeneratorPlugin.getRowMapperType;

public class SpringJdbcDaoGeneratorPlugin extends MmegpPluginAdapter {

    @Override
    public List<CompilationUnit> generateCompilationUnits(IntrospectedTable introspectedTable) {
        return List.of(generateDao(introspectedTable));
    }

    private TopLevelClass generateDao(IntrospectedTable introspectedTable) {
        TopLevelClass tlc = new TopLevelClass(getDaoType(introspectedTable));
        tlc.setVisibility(JavaVisibility.PUBLIC);
        tlc.addImportedType(introspectedTable.getBaseRecordType());
        addCommonImports(tlc);
        addExtends(tlc, introspectedTable);

        tlc.addMethod(buildConstructor(introspectedTable));

        tlc.addMethod(buildGetTableMethod(introspectedTable));

        tlc.addMethod(buildGetIdClassMethod(introspectedTable));

        tlc.addMethod(buildGetIdColumnMethod(introspectedTable));

        tlc.addMethod(buildGetIdMethod(introspectedTable));

        addConsumerMethod(tlc, introspectedTable);

        tlc.addMethod(buildGetAllColumnRowMapperMethod(introspectedTable));
        tlc.addImportedType(getRowMapperType(introspectedTable, this.properties));

        tlc.addMethod(buildAllColumnsMethod());

        addColumnMappings4InsertMethod(tlc, introspectedTable);

        addUpdateColumnsExceptPkMethod(tlc, introspectedTable);

        return tlc;
    }

    private void addConsumerMethod(TopLevelClass tlc, IntrospectedTable table) {
        var pkColumn = getOnlyOnePkColumn(table);
        if (!pkColumn.isAutoIncrement()) {
            return;
        }

        tlc.addImportedType(BiConsumer.class.getName());

        var entityTypeName = new FullyQualifiedJavaType(table.getBaseRecordType()).getShortName();
        tlc.addMethod(buildOverridePublicMethod(
                "getAutoIncrementConsumer",
                new FullyQualifiedJavaType(String.format(Locale.ROOT,
                        "BiConsumer<%s, %s>",
                        entityTypeName,
                        pkColumn.getFullyQualifiedJavaType().getShortName()
                )),
                String.format(Locale.ROOT, "%s::%s",
                    entityTypeName,
                    JavaBeansUtil.getSetterMethodName(pkColumn.getJavaProperty())
                )
        ));
    }

    private void addColumnMappings4InsertMethod(TopLevelClass tlc, IntrospectedTable table) {
        MethodAndImports mi = MmegpDynamicSqlMapperGenerator.genColumnMappingsMethod(table);
        mi.getMethod().setDefault(false);
        mi.getMethod().setVisibility(JavaVisibility.PUBLIC);
        mi.getMethod().addAnnotation("@Override");

        tlc.addMethod(mi.getMethod());
        tlc.addImportedTypes(mi.getImports());
        tlc.addStaticImports(mi.getStaticImports());
        tlc.addStaticImport(table.getMyBatisDynamicSqlSupportType() + ".*");
    }

    private Method buildAllColumnsMethod() {
        return buildOverridePublicMethod(
                "getAllColumns",
                new FullyQualifiedJavaType("BasicColumn[]"),
                GenerateDynamicSqlSupportClassPlugin.ALL_COLUMNS_FIELD_NAME
        );
    }

    private Method buildGetAllColumnRowMapperMethod(IntrospectedTable table) {
        return buildOverridePublicMethod(
                "getAllColumnRowMapper",
                new FullyQualifiedJavaType(
                        String.format(Locale.ROOT, "RowMapper<%s>",
                                new FullyQualifiedJavaType(table.getBaseRecordType()).getShortName())
                ),
                String.format(Locale.ROOT, "new %s()",
                        getRowMapperType(table, this.properties).getShortName())
        );
    }

    private void addUpdateColumnsExceptPkMethod(TopLevelClass tlc, IntrospectedTable table) {
        tlc.addImportedType(UpdateDSL.class.getName());
        tlc.addImportedType(UpdateModel.class.getName());
        var dslType = new FullyQualifiedJavaType("UpdateDSL<UpdateModel>");
        Method method = buildOverridePublicMethod("updateColumnsExceptPk", dslType, null);
        var recordType = new FullyQualifiedJavaType(table.getBaseRecordType());
        method.addParameter(new Parameter(recordType, "row"));
        method.addParameter(new Parameter(dslType, "dsl"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), "selectiveUpdate"));

        FragmentGenerator fragement = new FragmentGenerator.Builder()
                .withIntrospectedTable(table).withResultMapId(recordType.getShortNameWithoutTypeArguments() + "Result")
                .withTableFieldName(table.getMyBatisDynamicSQLTableObjectName())
        .build();

        var withoutPks = table.getNonPrimaryKeyColumns();
        method.addBodyLine("if (selectiveUpdate) {");
        method.addBodyLines(fragement.getSetEqualWhenPresentLines(withoutPks, "return dsl", "        ", true));
        method.addBodyLine("} else {");
        method.addBodyLines(fragement.getSetEqualLines(withoutPks, "return dsl", "        ", true));
        method.addBodyLine("}");
        tlc.addMethod(method);
    }

    private Method buildGetIdMethod(IntrospectedTable table) {
        IntrospectedColumn pkColumn = getOnlyOnePkColumn(table);
        Method method = buildOverridePublicMethod(
                "getId",
                pkColumn.getFullyQualifiedJavaType(),
                String.format(Locale.ROOT, "entity.%s()", JavaBeansUtil.getGetterMethodName(
                        pkColumn.getJavaProperty(),
                        pkColumn.getFullyQualifiedJavaType()
                ))
        );
        method.addParameter(new Parameter(new FullyQualifiedJavaType(table.getBaseRecordType()), "entity"));
        return method;
    }

    private Method buildGetIdColumnMethod(IntrospectedTable table) {
        var dssTypeName = new FullyQualifiedJavaType(table.getMyBatisDynamicSqlSupportType()).getShortName();
        IntrospectedColumn pkColumn = getOnlyOnePkColumn(table);
        String pkTypeName = pkColumn.getFullyQualifiedJavaType().getShortName();
        return buildOverridePublicMethod(
                "getIdColumn",
                new FullyQualifiedJavaType(
                        String.format(Locale.ROOT, "SqlColumn<%s>", pkTypeName)
                ),
                String.format(Locale.ROOT, "%s.%s", dssTypeName, pkColumn.getJavaProperty())
        );
    }

    private Method buildGetIdClassMethod(IntrospectedTable table) {
        String pkTypeName = getOnlyOnePkColumn(table).getFullyQualifiedJavaType().getShortName();
        return buildOverridePublicMethod(
                "getIdClass",
                new FullyQualifiedJavaType(
                        String.format(Locale.ROOT, "Class<%s>", pkTypeName)
                ),
                String.format(Locale.ROOT, "%s.class", pkTypeName)
        );
    }

    private Method buildGetTableMethod(IntrospectedTable table) {
        var dssTypeName = new FullyQualifiedJavaType(table.getMyBatisDynamicSqlSupportType()).getShortName();
        var dssTableTypeName = new FullyQualifiedJavaType(table.getMyBatisDynamicSQLTableObjectName()).getShortName();
        return buildOverridePublicMethod(
                "getTable",
                new FullyQualifiedJavaType(
                        String.format(Locale.ROOT, "%s.%s",
                        dssTypeName, dssTableTypeName)
                ),
                String.format(Locale.ROOT, "%s.%s", dssTypeName, StringUtil.firstLowerCase(dssTableTypeName))
        );
    }

    private Method buildOverridePublicMethod(String name,
                                             FullyQualifiedJavaType returnType,
                                             String returnValue) {
        Method method = new Method(name);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addAnnotation("@Override");
        method.setReturnType(returnType);
        if (StringUtils.isNotEmpty(returnValue)) {
            method.addBodyLine(String.format(Locale.ROOT, "return %s;", returnValue));
        }
        return method;
    }

    private Method buildConstructor(IntrospectedTable table) {
        Method method = new Method(getDaoType(table).getShortName());
        method.setConstructor(true);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(new FullyQualifiedJavaType("NamedParameterJdbcTemplate"), "jdbcTemplate"));
        method.addBodyLine("super(jdbcTemplate);");
        return method;
    }

    private void addExtends(TopLevelClass tlc, IntrospectedTable table) {
        tlc.addImportedType(getOnlyOnePkColumn(table).getFullyQualifiedJavaType());
        tlc.addImportedType(new FullyQualifiedJavaType(table.getMyBatisDynamicSqlSupportType()));
        tlc.setSuperClass(String.format(Locale.ROOT, "MmegpSpringDao<%s, %s, %s.%s>",
                new FullyQualifiedJavaType(table.getBaseRecordType()),
                getOnlyOnePkColumn(table).getFullyQualifiedJavaType().getShortName(),
                new FullyQualifiedJavaType(table.getMyBatisDynamicSqlSupportType()).getShortName(),
                new FullyQualifiedJavaType(table.getMyBatisDynamicSQLTableObjectName()).getShortName()));
    }

    public FullyQualifiedJavaType getDaoType(IntrospectedTable table) {
        return getDaoType(table, this.properties);
    }

    public static FullyQualifiedJavaType getDaoType(IntrospectedTable introspectedTable, Properties properties) {
        String domainPackage = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getPackageName();
        String defaultPackage = domainPackage.lastIndexOf(".") == -1
                ? "dao" : domainPackage.substring(0, domainPackage.lastIndexOf(".")) + ".dao";
        return new FullyQualifiedJavaType(String.format(Locale.ROOT, "%s.%s%s",
                properties.getProperty("package", defaultPackage),
                introspectedTable.getFullyQualifiedTable().getDomainObjectName(),
                properties.getProperty("daoNameSuffix", "Dao")));
    }

    private void addCommonImports(TopLevelClass tlc) {
        tlc.addImportedType("me.ningpp.mmegp.springjdbc.MmegpSpringDao");
        tlc.addImportedType("org.mybatis.dynamic.sql.BasicColumn");
        tlc.addImportedType("org.mybatis.dynamic.sql.SqlColumn");
        tlc.addImportedType("org.mybatis.dynamic.sql.util.AbstractColumnMapping");
        tlc.addImportedType("org.mybatis.dynamic.sql.util.PropertyMapping");
        tlc.addImportedType("org.springframework.jdbc.core.RowMapper");
        tlc.addImportedType("org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate");
        tlc.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        tlc.addImportedType(FullyQualifiedJavaType.getNewArrayListInstance());
    }

}
