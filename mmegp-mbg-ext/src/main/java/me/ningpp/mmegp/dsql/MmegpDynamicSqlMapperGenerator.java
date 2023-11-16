/*
 *    Copyright 2021-2023 the original author or authors.
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
package me.ningpp.mmegp.dsql;

import me.ningpp.mmegp.constants.Constants;
import me.ningpp.mmegp.enums.ModelType;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.runtime.dynamic.sql.DynamicSqlMapperGenerator;
import org.mybatis.generator.runtime.dynamic.sql.elements.AbstractMethodGenerator;
import org.mybatis.generator.runtime.dynamic.sql.elements.MethodAndImports;
import org.mybatis.generator.runtime.dynamic.sql.elements.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static me.ningpp.mmegp.DefaultIntrospectedTableBuilder.getMyBatis3JavaMapperType;

/**
 * copy code from
 * @see org.mybatis.generator.runtime.dynamic.sql.DynamicSqlMapperGenerator
 */
public class MmegpDynamicSqlMapperGenerator extends DynamicSqlMapperGenerator {

    private static final FullyQualifiedJavaType FQJT_UPDATE_DSL = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.update.UpdateDSL");
    private static final FullyQualifiedJavaType FQJT_UPDATE_MODEL = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.update.UpdateModel");
    private static final FullyQualifiedJavaType FQJT_UPDATE_DSL_MODEL = new FullyQualifiedJavaType("UpdateDSL<UpdateModel>");

    private final boolean generateSelectDistinctMethod;
    private final SelectPageMethodGenerator selectPageMethodGenerator;
    private final Properties properties;

    public MmegpDynamicSqlMapperGenerator(String project,
                                          boolean generateSelectDistinctMethod,
                                          SelectPageMethodGenerator selectPageMethodGenerator,
                                          Properties properties) {
        super(project);
        this.generateSelectDistinctMethod = generateSelectDistinctMethod;
        this.selectPageMethodGenerator = selectPageMethodGenerator;
        this.properties = properties == null ? new Properties() : properties;
    }

    @Override
    protected void preCalculate() {
        super.preCalculate();
        introspectedTable.setMyBatis3JavaMapperType(
            getMyBatis3JavaMapperType(introspectedTable));
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        preCalculate();

        Interface interfaze = createBasicInterface();

        FullyQualifiedJavaType supportClassType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatisDynamicSqlSupportType());
        interfaze.addStaticImport(supportClassType.getFullyQualifiedNameWithoutTypeParameters() + ".*");

        if (hasGeneratedKeys) {
            addBasicInsertMethod(interfaze);
            addBasicInsertMultipleMethod(interfaze);
        }

        boolean reuseResultMap = addBasicSelectManyMethod(interfaze);
        addBasicSelectOneMethod(interfaze, reuseResultMap);

        addGeneralCountMethod(interfaze);
        addGeneralDeleteMethod(interfaze);
        addDeleteByPrimaryKeyMethod(interfaze);
        addInsertOneMethod(interfaze);
        addInsertMultipleMethod(interfaze);
        addInsertSelectiveMethod(interfaze);
        addSelectListField(interfaze);
        addGeneralSelectMethod(interfaze);

        if (generateSelectDistinctMethod) {
            addSelectDistinctMethod(interfaze);
        }

        addSelectByPrimaryKeyMethod(interfaze);

        interfaze.addImportedType(FQJT_UPDATE_DSL);
        interfaze.addImportedType(FQJT_UPDATE_MODEL);
        addGeneralUpdateMethod(interfaze);

        addUpdateAllColumnsExceptPkMethod(interfaze);
        addUpdateSelectiveColumnsExceptPkMethod(interfaze);

        // don't gen updateAllColumns and updateSelectiveColumns method
        // because these two methods update pk value

        if (Utils.generateUpdateByPrimaryKey(introspectedTable)) {
            addUpdateByPrimaryKeyMethod(interfaze);
            addUpdateByPrimaryKeySelectiveMethod(interfaze);
        }

        if (selectPageMethodGenerator != null) {
            selectPageMethodGenerator.generate(introspectedTable, interfaze, properties);
        }

        return List.of(interfaze);
    }

    @Override
    protected Interface createBasicInterface() {
        Interface mapperInterface = super.createBasicInterface();

        MethodAndImports mi = genColumnMappingsMethod(introspectedTable);
        mapperInterface.addMethod(mi.getMethod());
        mapperInterface.addImportedTypes(mi.getImports());
        mapperInterface.addStaticImports(mi.getStaticImports());

        return mapperInterface;
    }

    public static MethodAndImports genColumnMappingsMethod(IntrospectedTable introspectedTable) {
        Method method = new Method("columnMappings4Insert");
        method.setDefault(true);
        method.setReturnType(new FullyQualifiedJavaType("List<AbstractColumnMapping>"));

        method.addBodyLine("List<AbstractColumnMapping> columnMappings = new ArrayList<>();");

        List<IntrospectedColumn> columns =
                ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        for (IntrospectedColumn column : columns) {
            method.addBodyLine(String.format(Locale.ROOT,
                    "columnMappings.add(PropertyMapping.of(%s, \"%s\"));",
                    column.getJavaProperty(), column.getJavaProperty()));
        }

        method.addBodyLine("return columnMappings;");

        return MethodAndImports.withMethod(method)
                .withImport(Constants.FQJT_ARRAYLIST)
                .withImport(Constants.FQJT_COLLECTION)
                .withImport(Constants.FQJT_ABS_COLUMN_MAPPING)
                .withImport(Constants.FQJT_PROPERTY_MAPPING)
                .withImport(Constants.FQJT_DYNAMICSQL_UTIL)
                .build();
    }

    @Override
    protected void addInsertOneMethod(Interface interfaze) {
        Method method = new Method("insert");
        method.setDefault(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(new Parameter(recordType, "row"));
        method.addBodyLine(String.format(Locale.ROOT,
            "return insert(DynamicSqlUtil.renderInsert(row, %s, columnMappings4Insert()));", tableFieldName));
        interfaze.addMethod(method);
    }

    @Override
    protected void addInsertMultipleMethod(Interface interfaze) {
        Method method = new Method("insertMultiple");
        method.setDefault(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(Constants.FQJT_COLLECTION.getShortName());
        parameterType.addTypeArgument(recordType);
        method.addParameter(new Parameter(parameterType, "records"));
        if (hasGeneratedKeys) {
            interfaze.addImportedType(Constants.FQJT_MULTI_INSERT_PROVIDER);
            method.addBodyLine(String.format(Locale.ROOT,
                    "MultiRowInsertStatementProvider<%s> provider = DynamicSqlUtil.renderMultiInsert(records, %s, columnMappings4Insert());",
                    recordType.getShortName(), tableFieldName));
            method.addBodyLine("return insertMultiple(provider.getInsertStatement(), provider.getRecords());");
        } else {
            method.addBodyLine(String.format(Locale.ROOT,
                    "return insertMultiple(DynamicSqlUtil.renderMultiInsert(records, %s, columnMappings4Insert()));", tableFieldName));
        }
        interfaze.addMethod(method);
    }

    protected void addUpdateAllColumnsExceptPkMethod(Interface interfaze) {
        Method method = createUpdateColumnsExceptPkMethod("updateAllColumnsExceptPk");
        method.addBodyLines(getSetEqualLines(
                introspectedTable.getNonPrimaryKeyColumns(), "return dsl", "        ", true));
        interfaze.addMethod(method);
    }

    protected void addUpdateSelectiveColumnsExceptPkMethod(Interface interfaze) {
        Method method = createUpdateColumnsExceptPkMethod("updateSelectiveColumnsExceptPk");
        method.addBodyLines(getSetEqualWhenPresentLines(
                introspectedTable.getNonPrimaryKeyColumns(), "return dsl", "        ", true));
        interfaze.addMethod(method);
    }

    private Method createUpdateColumnsExceptPkMethod(String methodName) {
        Method method = new Method(methodName);
        method.setStatic(true);

        method.setReturnType(FQJT_UPDATE_DSL_MODEL);
        method.addParameter(new Parameter(recordType, "row"));
        method.addParameter(new Parameter(FQJT_UPDATE_DSL_MODEL, "dsl"));
        return method;
    }

    @Override
    protected void addUpdateByPrimaryKeyMethod(Interface interfaze) {
        Method method = new Method("updateByPrimaryKey");
        method.setDefault(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(new Parameter(recordType, "row"));

        method.addBodyLine("return update(dsl -> updateAllColumnsExceptPk(row, dsl)");
        method.addBodyLines(getPrimaryKeyWhereClauseForUpdate("    "));
        method.addBodyLine(");");
        interfaze.addMethod(method);
    }

    @Override
    protected void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze) {
        Method method = new Method("updateByPrimaryKeySelective");
        method.setDefault(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(new Parameter(recordType, "row"));

        method.addBodyLine("return update(dsl -> updateSelectiveColumnsExceptPk(row, dsl)");
        method.addBodyLines(getPrimaryKeyWhereClauseForUpdate("    "));
        method.addBodyLine(");");
        interfaze.addMethod(method);
    }

    @Override
    protected void addInsertSelectiveMethod(Interface interfaze) {
        InsertSelectiveMethodMmegpGenerator generator = new InsertSelectiveMethodMmegpGenerator.Builder()
                .withContext(context)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .withRecordType(recordType)
                .build();
        if (generate(interfaze, generator) && !hasGeneratedKeys) {
            // add common interface
            addCommonInsertInterface(interfaze);
        }
    }

    private List<String> getSetEqualLines(List<IntrospectedColumn> columnList, String firstLinePrefix,
                                          String subsequentLinePrefix, boolean terminate) {
        return getSetLines(columnList, firstLinePrefix, subsequentLinePrefix, terminate, "equalTo");
    }

    private List<String> getSetEqualWhenPresentLines(List<IntrospectedColumn> columnList, String firstLinePrefix,
                                                    String subsequentLinePrefix, boolean terminate) {
        return getSetLines(columnList, firstLinePrefix, subsequentLinePrefix, terminate, "equalToWhenPresent");
    }

    private List<String> getSetLines(List<IntrospectedColumn> columnList, String firstLinePrefix,
                                     String subsequentLinePrefix, boolean terminate, String fragment) {
        List<String> lines = new ArrayList<>();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(columnList);
        Iterator<IntrospectedColumn> iter = columns.iterator();
        boolean modelIsRecord = isRecordModel(introspectedTable);
        boolean first = true;
        while (iter.hasNext()) {
            IntrospectedColumn column = iter.next();
            String fieldName = AbstractMethodGenerator.calculateFieldName(tableFieldName, column);
            String propertyGetter = getPropertyGetter(column, modelIsRecord);

            String start;
            if (first) {
                start = firstLinePrefix;
                first = false;
            } else {
                start = subsequentLinePrefix;
            }

            String line = start
                    + ".set(" //$NON-NLS-1$
                    + fieldName
                    + ")." //$NON-NLS-1$
                    + fragment
                    + "(row::" //$NON-NLS-1$
                    + propertyGetter
                    + ")"; //$NON-NLS-1$

            if (terminate && !iter.hasNext()) {
                line += ";"; //$NON-NLS-1$
            }

            lines.add(line);
        }

        return lines;
    }

    private List<String> getPrimaryKeyWhereClauseForUpdate(String prefix) {
        List<String> lines = new ArrayList<>();
        String format = "%s.%s(%s, isEqualTo(row::%s))";
        boolean modelIsRecord = isRecordModel(introspectedTable);
        boolean first = true;
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            String fieldName = AbstractMethodGenerator.calculateFieldName(tableFieldName, column);
            String propertyGetter = getPropertyGetter(column, modelIsRecord);
            if (first) {
                lines.add(String.format(Locale.ROOT,
                        format, prefix, "where", fieldName, propertyGetter));
                first = false;
            } else {
                lines.add(String.format(Locale.ROOT,
                        format, prefix, "and", fieldName, propertyGetter));
            }
        }
        return lines;
    }

    public static String getPropertyGetter(IntrospectedColumn column, boolean modelIsRecord) {
        return modelIsRecord ? column.getJavaProperty()
                : JavaBeansUtil.getGetterMethodName(
                    column.getJavaProperty(), column.getFullyQualifiedJavaType());
    }

    public static boolean isRecordModel(IntrospectedTable introspectedTable) {
        return ModelType.RECORD.equals(
                introspectedTable.getAttribute(ModelType.class.getName()));
    }

}
