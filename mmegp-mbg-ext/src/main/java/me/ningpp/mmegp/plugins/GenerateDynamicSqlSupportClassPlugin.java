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

import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.runtime.dynamic.sql.DynamicSqlSupportClassGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GenerateDynamicSqlSupportClassPlugin extends MmegpPluginAdapter {

    @Override
    public List<CompilationUnit> generateCompilationUnits(IntrospectedTable introspectedTable) {
        DynamicSqlSupportClassGenerator generator = DynamicSqlSupportClassGenerator
                .of(introspectedTable, context.getCommentGenerator(), new ArrayList<>(0));
        TopLevelClass tlc = generator.generate();

        addAllColumnsField(tlc, introspectedTable);

        return List.of(tlc);
    }

    public static final String ALL_COLUMNS_FIELD_NAME = "ALL_COLUMNS";

    private void addAllColumnsField(TopLevelClass tlc, IntrospectedTable table) {
        tlc.addImportedType(BasicColumn.class.getName());
        Field allColumnsField = new Field(ALL_COLUMNS_FIELD_NAME,
                new FullyQualifiedJavaType("BasicColumn[]"));
        allColumnsField.setStatic(true);
        allColumnsField.setFinal(true);
        allColumnsField.setVisibility(JavaVisibility.PUBLIC);
        allColumnsField.setInitializationString(
            String.format(Locale.ROOT, "BasicColumn.columnList(%s)",
                table.getAllColumns().stream().map(IntrospectedColumn::getJavaProperty)
                    .collect(Collectors.joining(", "))
            )
        );
        tlc.addField(allColumnsField);
    }

}
