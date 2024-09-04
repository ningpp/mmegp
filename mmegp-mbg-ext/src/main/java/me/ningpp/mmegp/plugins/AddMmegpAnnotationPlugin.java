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
package me.ningpp.mmegp.plugins;

import me.ningpp.mmegp.enums.AggregateFunction;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class AddMmegpAnnotationPlugin extends PluginAdapter {

    private static final String DELIMITER = ", ";

    private static final FullyQualifiedJavaType JAVAX_GENERATED =
            new FullyQualifiedJavaType("javax.annotation.Generated");
    private static final FullyQualifiedJavaType JAKARTA_GENERATED =
            new FullyQualifiedJavaType("jakarta.annotation.Generated");

    private boolean removeGeneratedAnnotation = true;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        final String propertyKey = "removeGeneratedAnnotation";
        if (properties.containsKey(propertyKey)) {
            this.removeGeneratedAnnotation = Boolean.parseBoolean(properties.getProperty(propertyKey));
        }
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("me.ningpp.mmegp.annotations.Table");
        List<String> infos = new ArrayList<>();
        infos.add(String.format(Locale.ROOT,
                "table = \"%s\"",
                introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        String countGroupByColumnsValue = introspectedTable.getTableConfigurationProperty("countGroupByColumns");
        if (stringHasValue(countGroupByColumnsValue)) {
            List<String> multiGroup = Arrays.stream(countGroupByColumnsValue.split(";\\s*"))
                    .filter(StringUtility::stringHasValue)
                    .map(str -> String.format(Locale.ROOT, "\"%s\"", str))
                    .toList();
            infos.add(String.format(Locale.ROOT,
                    "countGroupByColumns = {%s}",
                    String.join(DELIMITER, multiGroup)));
        }

        topLevelClass.addAnnotation(String.format(Locale.ROOT,
                "@Table(%s)",
                String.join(DELIMITER, infos)));

        topLevelClass.addImportedType("me.ningpp.mmegp.annotations.Column");
        topLevelClass.addImportedType("org.apache.ibatis.type.JdbcType");

        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method,
                                               TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                               IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        deleteGeneratedAnnotation(topLevelClass, method.getAnnotations());
        return true;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method,
                                               TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                               IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        deleteGeneratedAnnotation(topLevelClass, method.getAnnotations());
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass,
                                       IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable, ModelClassType modelClassType) {

        deleteGeneratedAnnotation(topLevelClass, field.getAnnotations());

        List<String> otherInfos = new ArrayList<>();
        if (stringHasValue(introspectedColumn.getTypeHandler())) {
            otherInfos.add(String.format(Locale.ROOT,
                    "typeHandler = %s.class",
                    introspectedColumn.getTypeHandler()));
        }

        if (introspectedColumn.isBLOBColumn()) {
            otherInfos.add("blob = true");
        }

        if (introspectedTable.getPrimaryKeyColumns().contains(introspectedColumn)) {
            otherInfos.add("id = true");
        }

        if (introspectedColumn.isGeneratedColumn()) {
            otherInfos.add("generatedValue = true");
        }

        List<AggregateFunction> aggregateFunctions = AggregateFunction.parseArray(
                introspectedColumn.getProperties().getProperty("aggregates")
        );
        if (!aggregateFunctions.isEmpty()) {
            topLevelClass.addStaticImport("me.ningpp.mmegp.enums.AggregateFunction.*");
            otherInfos.add(String.format(Locale.ROOT, "aggregates = {%s}",
                    aggregateFunctions.stream()
                        .map(AggregateFunction::name)
                        .collect(Collectors.joining(DELIMITER))));
        }

        field.addAnnotation(String.format(Locale.ROOT,
                "@Column(name = \"%s\", jdbcType = JdbcType.%s%s)",
                introspectedColumn.getActualColumnName(),
                introspectedColumn.getJdbcTypeName(),
                otherInfos.isEmpty()
                        ? ""
                        : DELIMITER + String.join(DELIMITER, otherInfos)));
        return true;
    }

    private void deleteGeneratedAnnotation(TopLevelClass topLevelClass, List<String> annotations) {
        if (removeGeneratedAnnotation && annotations != null) {
            annotations.removeIf(anno -> anno.startsWith("@Generated(")
                    && anno.contains("org.mybatis.generator.api.MyBatisGenerator"));
            topLevelClass.getImportedTypes().remove(JAVAX_GENERATED);
            topLevelClass.getImportedTypes().remove(JAKARTA_GENERATED);
        }
    }

}
