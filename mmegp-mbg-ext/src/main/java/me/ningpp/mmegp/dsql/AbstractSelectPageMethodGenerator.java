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
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public abstract class AbstractSelectPageMethodGenerator implements SelectPageMethodGenerator {

    protected Set<FullyQualifiedJavaType> getSuperInterfaces(IntrospectedTable introspectedTable, Properties pluginProperties) {
        return Set.of();
    }

    protected Set<FullyQualifiedJavaType> getImportedTypes(IntrospectedTable introspectedTable, Properties pluginProperties) {
        return Set.of();
    }

    protected List<Parameter> getOtherParameters(IntrospectedTable introspectedTable, Properties pluginProperties) {
        return List.of();
    }

    protected abstract List<String> getBodyLines(IntrospectedTable introspectedTable, Properties pluginProperties);

    @Override
    public final void generate(IntrospectedTable introspectedTable, Interface mapperInterface, Properties pluginProperties) {
        Set<FullyQualifiedJavaType> superInterfaces = getSuperInterfaces(introspectedTable, pluginProperties);
        if (superInterfaces != null) {
            superInterfaces.forEach(mapperInterface::addSuperInterface);
        }

        mapperInterface.addImportedType(new FullyQualifiedJavaType(
                "me.ningpp.mmegp.mybatis.dsql.pagination.Page"));
        mapperInterface.addImportedType(new FullyQualifiedJavaType(
                "me.ningpp.mmegp.mybatis.dsql.pagination.LimitOffset"));
        mapperInterface.addImportedType(new FullyQualifiedJavaType(
                "org.mybatis.dynamic.sql.select.SelectDSL"));
        mapperInterface.addImportedType(new FullyQualifiedJavaType(
                "org.mybatis.dynamic.sql.select.SelectModel"));

        Set<FullyQualifiedJavaType> importTypes = getImportedTypes(introspectedTable, pluginProperties);
        mapperInterface.addImportedTypes(Optional.ofNullable(importTypes).orElse(new HashSet<>(0)));

        Method method = new Method("selectPage");
        mapperInterface.addMethod(method);
        method.setDefault(true);
        method.setReturnType(new FullyQualifiedJavaType("Page<" + introspectedTable.getBaseRecordType() + ">"));
        method.addParameter(new Parameter(new
                FullyQualifiedJavaType("SelectDSL<SelectModel>"), "listDsl"));
        method.addParameter(new Parameter(new
                FullyQualifiedJavaType("LimitOffset"), "limitOffset"));

        List<Parameter> otherParms = getOtherParameters(introspectedTable, pluginProperties);
        method.getParameters().addAll(Optional.ofNullable(otherParms).orElse(new ArrayList<>(0)));

        List<String> bodyLines = getBodyLines(introspectedTable, pluginProperties);
        method.getBodyLines().addAll(Optional.ofNullable(bodyLines).orElse(new ArrayList<>(0)));
    }

}
