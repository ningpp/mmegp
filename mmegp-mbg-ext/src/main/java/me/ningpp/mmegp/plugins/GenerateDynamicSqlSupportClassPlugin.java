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

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.runtime.dynamic.sql.DynamicSqlSupportClassGenerator;

import java.util.ArrayList;
import java.util.List;

public class GenerateDynamicSqlSupportClassPlugin extends MmegpPluginAdapter {

    @Override
    public List<CompilationUnit> generateCompilationUnits(IntrospectedTable introspectedTable) {
        DynamicSqlSupportClassGenerator generator = DynamicSqlSupportClassGenerator
                .of(introspectedTable, context.getCommentGenerator(), new ArrayList<>(0));
        return List.of(generator.generate());
    }

}
