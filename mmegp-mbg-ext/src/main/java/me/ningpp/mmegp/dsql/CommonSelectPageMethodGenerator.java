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

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public class CommonSelectPageMethodGenerator extends AbstractSelectPageMethodGenerator {

    public static final Parameter RARAM_RENDERER = new Parameter(
            new FullyQualifiedJavaType("me.ningpp.mmegp.mybatis.dsql.pagination.PaginationModelRenderer"),
            "renderer");

    @Override
    protected Set<FullyQualifiedJavaType> getImportedTypes(IntrospectedTable introspectedTable,
            Properties pluginProperties) {
        return Set.of(RARAM_RENDERER.getType());
    }

    @Override
    protected List<Parameter> getOtherParameters(IntrospectedTable introspectedTable, Properties pluginProperties) {
        return List.of(RARAM_RENDERER);
    }

    @Override
    protected List<String> getBodyLines(IntrospectedTable introspectedTable, Properties pluginProperties) {
        return List.of(
                "return DynamicSqlUtil.selectPage(this::count, this::selectMany, listDsl, limitOffset, renderer);");
    }

}
