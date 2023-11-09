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

import me.ningpp.mmegp.mybatis.dsql.pagination.LimitOffsetPaginationModelRendererProvider;
import me.ningpp.mmegp.mybatis.dsql.pagination.MySqlPaginationModelRendererProvider;
import me.ningpp.mmegp.mybatis.dsql.pagination.OffsetFetchPaginationModelRendererProvider;
import me.ningpp.mmegp.mybatis.dsql.pagination.SqlServerPaginationModelRendererProvider;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public class StaticSelectPageMethodGenerator extends AbstractSelectPageMethodGenerator {

    public static final String PROPERTY_KEY = "paginationModelRendererProviderType";

    private String getPaginationModelRendererProviderType(Properties pluginProperties) {
        String type = pluginProperties.getProperty(PROPERTY_KEY);
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException(PROPERTY_KEY + " should set in plugin properties");
        }

        if ("OffsetFetch".equalsIgnoreCase(type)) {
            type = OffsetFetchPaginationModelRendererProvider.class.getName();
        } else if ("LimitOffset".equalsIgnoreCase(type)) {
            type = LimitOffsetPaginationModelRendererProvider.class.getName();
        } else if ("MySql".equalsIgnoreCase(type)) {
            type = MySqlPaginationModelRendererProvider.class.getName();
        } else if ("SqlServer".equalsIgnoreCase(type)) {
            type = SqlServerPaginationModelRendererProvider.class.getName();
        }
        return type;
    }

    @Override
    protected Set<FullyQualifiedJavaType> getSuperInterfaces(IntrospectedTable introspectedTable, Properties pluginProperties) {
        return Set.of(new FullyQualifiedJavaType(getPaginationModelRendererProviderType(pluginProperties)));
    }

    @Override
    protected List<String> getBodyLines(IntrospectedTable introspectedTable, Properties pluginProperties) {
        return List.of("return DynamicSqlUtil.selectPage(this::count, this::selectMany, listDsl, limitOffset, RENDERER);");
    }

}
