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

import me.ningpp.mmegp.NullProgressCallback;
import me.ningpp.mmegp.dsql.DefaultSelectPageMethodGenerator;
import me.ningpp.mmegp.dsql.MmegpDynamicSqlMapperGenerator;
import me.ningpp.mmegp.dsql.SelectPageMethodGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.runtime.dynamic.sql.DynamicSqlMapperGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class MmegpGenerateMapperPlugin extends MmegpPluginAdapter {
    // don't gen selectDistinct method by default, because it is rarely used
    private boolean generateSelectDistinctMethod;
    private SelectPageMethodGenerator selectPageMethodGenerator;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String propertyKey = "generateSelectDistinctMethod";
        if (properties.containsKey(propertyKey)) {
            generateSelectDistinctMethod = Boolean.parseBoolean(properties.getProperty(propertyKey));
        }

        String selectPageMethodGeneratorType = properties.getProperty("selectPageMethodGeneratorType");
        if (properties.containsKey(SelectPageMethodGenerator.PROPERTY_KEY)) {
            if (!stringHasValue(selectPageMethodGeneratorType)) {
                selectPageMethodGeneratorType = DefaultSelectPageMethodGenerator.class.getName();
            }
        }
        if (stringHasValue(selectPageMethodGeneratorType)) {
            selectPageMethodGenerator = (SelectPageMethodGenerator) ObjectFactory
                    .createInternalObject(selectPageMethodGeneratorType);
        }
    }

    @Override
    public List<CompilationUnit> generateCompilationUnits(IntrospectedTable introspectedTable) {
        DynamicSqlMapperGenerator generator = new MmegpDynamicSqlMapperGenerator(
                getTargetProject(), generateSelectDistinctMethod,
                selectPageMethodGenerator, properties);
        generator.setContext(context);
        generator.setIntrospectedTable(introspectedTable);
        generator.setProgressCallback(new NullProgressCallback());
        generator.setWarnings(new ArrayList<>(0));
        return generator.getCompilationUnits();
    }

}
