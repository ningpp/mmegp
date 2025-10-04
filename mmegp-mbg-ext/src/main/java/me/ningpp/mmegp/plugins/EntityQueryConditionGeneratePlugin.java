/*
 *    Copyright 2025 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package me.ningpp.mmegp.plugins;

import me.ningpp.mmegp.mybatis.dsql.DynamicSqlUtil;
import me.ningpp.mmegp.mybatis.dsql.EntityQueryConditionDTO;
import me.ningpp.mmegp.util.StringUtil;
import org.mybatis.dynamic.sql.CriteriaGroup;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

public class EntityQueryConditionGeneratePlugin extends MmegpPluginAdapter {

    @Override
    public List<CompilationUnit> generateCompilationUnits(IntrospectedTable introspectedTable) {
        return List.of(generateCompilationUnit(introspectedTable));
    }

    private static final FullyQualifiedJavaType EQCDTO_FQJT = new FullyQualifiedJavaType(EntityQueryConditionDTO.class.getName());

    public static FullyQualifiedJavaType getEntityQueryConditionType(IntrospectedTable introspectedTable,
            Properties properties) {
        String domainPackage = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getPackageName();
        String defaultPackage = domainPackage.lastIndexOf(".") == -1
                ? "query" : domainPackage.substring(0, domainPackage.lastIndexOf(".")) + ".query";
        return new FullyQualifiedJavaType(String.format(Locale.ROOT, "%s.%s%s",
                properties.getProperty("package", defaultPackage),
                introspectedTable.getFullyQualifiedTable().getDomainObjectName(),
                properties.getProperty("queryNameSuffix", "QueryConditionDTO")));
    }

    private TopLevelClass generateCompilationUnit(IntrospectedTable introspectedTable) {
        TopLevelClass tlc = new TopLevelClass(
                getEntityQueryConditionType(introspectedTable, this.properties)
        );
        tlc.setVisibility(JavaVisibility.PUBLIC);
        tlc.addImportedType(EQCDTO_FQJT);
        tlc.addSuperInterface(EQCDTO_FQJT);
        tlc.addImportedType("me.ningpp.mmegp.mybatis.dsql.QueryDTO");
        tlc.addImportedType("me.ningpp.mmegp.query.PropertyConditionDTO");
        tlc.addImportedType("org.mybatis.dynamic.sql.BasicColumn");
        tlc.addImportedType("org.mybatis.dynamic.sql.SortSpecification");
        tlc.addImportedType("org.mybatis.dynamic.sql.delete.DeleteModel");
        tlc.addImportedType("org.mybatis.dynamic.sql.select.SelectModel");
        tlc.addImportedType(introspectedTable.getMyBatis3JavaMapperType());
        tlc.addImportedType(getDynamicSqlSupportType(introspectedTable));

        createNull2EmptyMethod(tlc);

        List<Field> fields = createFields(tlc, introspectedTable);

        createBuildCriteriaGroup(tlc, introspectedTable, fields);

        createToSelectMethod(tlc, introspectedTable);

        createToSelectWithColumnMethod(tlc, introspectedTable);

        createToDeleteMethod(tlc, introspectedTable);

        createConditionsMethod(introspectedTable, tlc, fields);

        createBuilderStyleSetterMethonds(tlc, fields);

        createGetterMethonds(tlc, fields);

        return tlc;
    }

    private void createBuildCriteriaGroup(TopLevelClass tlc, IntrospectedTable introspectedTable, List<Field> fields) {
        Method method = new Method("buildCriteriaGroup");
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType(CriteriaGroup.class.getName()));
        method.addBodyLine("List<SqlCriterion> criterions = new ArrayList<>();");
        for (Field field : fields) {
            method.addBodyLine(String.format(Locale.ROOT,
                    "criterions.add(PropertyConditionDTO.toCriterion(%s.%s, %s));",
                    getDynamicSqlSupportType(introspectedTable).getShortName(),
                    field.getName(), field.getName()));
        }
        method.addBodyLine("return DynamicSqlUtil.buildCriteriaGroup(criterions);");
        tlc.addImportedType(CriteriaGroup.class.getName());
        tlc.addImportedType(SqlCriterion.class.getName());
        tlc.addImportedType(ArrayList.class.getName());
        tlc.addImportedType(List.class.getName());
        tlc.addImportedType(DynamicSqlUtil.class.getName());
        tlc.addMethod(method);
    }

    private void createToSelectMethod(TopLevelClass tlc, IntrospectedTable introspectedTable) {
        Method method = buildToSelectMethod(tlc);
        method.addBodyLine(String.format(Locale.ROOT,
                "return toSelect(%s.selectList, sortSpecs);",
                new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()).getShortName()));
    }

    private void createToSelectWithColumnMethod(TopLevelClass tlc, IntrospectedTable introspectedTable) {
        Method method = buildToSelectMethod(tlc);
        method.addParameter(0, new Parameter(new FullyQualifiedJavaType("BasicColumn[]"), "columns"));
        method.addBodyLine(String.format(Locale.ROOT,
                "return conditions(QueryDTO.of(%s.%s).columns(columns).orderBy(sortSpecs)).toSelectModel();",
                getDynamicSqlSupportType(introspectedTable).getShortName(),
                StringUtil.firstLowerCase(introspectedTable.getMyBatisDynamicSQLTableObjectName())
        ));
    }

    private Method buildToSelectMethod(TopLevelClass tlc) {
        Method method = new Method("toSelect");
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("SelectModel"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("SortSpecification"), "sortSpecs", true));
        tlc.addMethod(method);
        return method;
    }

    private void createToDeleteMethod(TopLevelClass tlc, IntrospectedTable introspectedTable) {
        Method method = new Method("toDelete");
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("DeleteModel"));
        method.addBodyLine(String.format(Locale.ROOT,
                "return conditions(QueryDTO.of(%s.%s)).toDeleteModel();",
                getDynamicSqlSupportType(introspectedTable).getShortName(),
                StringUtil.firstLowerCase(introspectedTable.getMyBatisDynamicSQLTableObjectName())));
        tlc.addMethod(method);
    }

    private FullyQualifiedJavaType getDynamicSqlSupportType(IntrospectedTable introspectedTable) {
        return new FullyQualifiedJavaType(introspectedTable.getMyBatisDynamicSqlSupportType());
    }

    private void createConditionsMethod(IntrospectedTable introspectedTable, TopLevelClass tlc, List<Field> fields) {
        String entityName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        String dssShortName = getDynamicSqlSupportType(introspectedTable).getShortName();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                String.format(Locale.ROOT, "QueryDTO<%s.%s>",
                        dssShortName, entityName));
        Method method = new Method("conditions");
        method.setVisibility(JavaVisibility.PRIVATE);
        method.setReturnType(type);
        method.addParameter(new Parameter(type, "dto"));
        var columns = introspectedTable.getAllColumns().stream()
                .collect(Collectors.toMap(IntrospectedColumn::getJavaProperty, v->v, (v1,v2)->v2));
        for (Field field : fields) {
            boolean isStringField = String.class.getSimpleName()
                    .equals(columns.get(field.getName()).getFullyQualifiedJavaType().getShortName());
            String callMethodName = isStringField ? "stringCondition" : "commonCondition";
            method.addBodyLine(String.format(Locale.ROOT,
                    "%s(dto, %s.%s, %s);",
                    callMethodName, dssShortName, field.getName(), field.getName()));
        }
        method.addBodyLine("return dto;");
        tlc.addMethod(method);
    }

    private void createNull2EmptyMethod(TopLevelClass tlc) {
        Method method = new Method("null2Empty");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setStatic(true);
        method.setReturnType(tlc.getType());
        method.addParameter(new Parameter(
                tlc.getType(),
                "dto"
        ));
        method.addBodyLine(String.format(Locale.ROOT,
                "return dto == null ? new %s() : dto;", tlc.getType().getShortName()));
        tlc.addMethod(method);
    }

    private void createGetterMethonds(TopLevelClass tlc, List<Field> fields) {
        for (Field field : fields) {
            Method method = new Method(JavaBeansUtil
                    .getGetterMethodName(field.getName(), field.getType()));
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setReturnType(field.getType());
            method.addBodyLine("return " + field.getName() + ";");
            tlc.addMethod(method);
        }
    }

    private void createBuilderStyleSetterMethonds(TopLevelClass tlc, List<Field> fields) {
        for (Field field : fields) {
            Method method = new Method(field.getName());
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setReturnType(tlc.getType());
            method.addParameter(new Parameter(
                    field.getType(),
                    field.getName()
            ));
            method.addBodyLine(String.format(Locale.ROOT,
                    "this.%s = %s;",
                    field.getName(), field.getName()));
            method.addBodyLine("return this;");
            tlc.addMethod(method);
        }
    }

    private List<Field> createFields(TopLevelClass tlc, IntrospectedTable introspectedTable) {
        List<Field> fields = new ArrayList<>();
        List<IntrospectedColumn> introspectedColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn column : introspectedColumns) {
            tlc.addImportedType(column.getFullyQualifiedJavaType());
            Field field = new Field(column.getJavaProperty(),
                    new FullyQualifiedJavaType(String.format(Locale.ROOT,
                            "PropertyConditionDTO<%s>",
                            column.getFullyQualifiedJavaType().getShortName())));
            field.setStatic(false);
            field.setVisibility(JavaVisibility.PRIVATE);
            tlc.addField(field);
            fields.add(field);
        }
        return fields;
    }

}
