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
package me.ningpp.mmegp.dsql;

import me.ningpp.mmegp.plugins.EntityQueryConditionGeneratePlugin;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.Locale;
import java.util.Properties;

public class MmegpDynamicSqlMapperWithEntityQueryConditionGenerator
    extends MmegpDynamicSqlMapperGenerator {

    public MmegpDynamicSqlMapperWithEntityQueryConditionGenerator(String project,
            boolean generateSelectDistinctMethod, SelectPageMethodGenerator selectPageMethodGenerator,
            Properties properties) {
        super(project, generateSelectDistinctMethod, selectPageMethodGenerator, properties);
    }

    @Override
    protected void generateAddtionalCodes(IntrospectedTable introspectedTable, Interface interfaze) {
        var eqcType = EntityQueryConditionGeneratePlugin.getEntityQueryConditionType(introspectedTable, this.properties);
        interfaze.addImportedType(eqcType);
        interfaze.addImportedType(new FullyQualifiedJavaType(RenderingStrategies.class.getName()));
        interfaze.addImportedType(new FullyQualifiedJavaType(SortSpecification.class.getName()));

        interfaze.addMethod(createDeleteByQueryMethod(introspectedTable));
        interfaze.addMethod(createCountByQueryMethod(introspectedTable));
        interfaze.addMethod(createSelectByQueryMethod(introspectedTable));
        interfaze.addMethod(createSelectPageByQueryMethod(introspectedTable));

    }

    private Method createSelectPageByQueryMethod(IntrospectedTable introspectedTable) {
        Method method = new Method("selectPageByQuery");
        method.setDefault(true);
        method.setReturnType(new FullyQualifiedJavaType(String.format(
                Locale.ROOT, "Page<%s>",
                new FullyQualifiedJavaType((introspectedTable.getBaseRecordType())).getShortName()
        )));

        addSelectMethodParameters(method, introspectedTable);

        method.addBodyLine("return DynamicSqlUtil.selectPage(this::count, this::selectMany,");

        method.addBodyLine(String.format(Locale.ROOT, "    %s.null2Empty(dto).toSelectCount(),",
                createDtoParameter(introspectedTable).getType().getShortName()));

        method.addBodyLine(String.format(Locale.ROOT, "    %s.null2Empty(dto).toSelect(sortSpecs),",
                createDtoParameter(introspectedTable).getType().getShortName()));

        method.addBodyLine("    limitOffset, renderer);");
        return method;
    }

    private Method createSelectByQueryMethod(IntrospectedTable introspectedTable) {
        Method method = new Method("selectByQuery");
        method.setDefault(true);
        method.setReturnType(new FullyQualifiedJavaType(String.format(
                Locale.ROOT, "List<%s>",
                new FullyQualifiedJavaType((introspectedTable.getBaseRecordType())).getShortName()
        )));

        addSelectMethodParameters(method, introspectedTable);

        method.addBodyLine(String.format(Locale.ROOT,
                "return selectMany(DynamicSqlUtil.renderSelect(%s.null2Empty(dto).toSelect(sortSpecs), limitOffset, renderer));",
                createDtoParameter(introspectedTable).getType().getShortName()));
        return method;
    }

    private void addSelectMethodParameters(Method method, IntrospectedTable introspectedTable) {
        method.addParameter(createDtoParameter(introspectedTable));

        method.addParameter(new Parameter(new
                FullyQualifiedJavaType("LimitOffset"), "limitOffset"));

        method.addParameter(CommonSelectPageMethodGenerator.RARAM_RENDERER);

        method.addParameter(new Parameter(
                new FullyQualifiedJavaType(SortSpecification.class.getName()),
                "sortSpecs",
                true
        ));

    }

    private Method createCountByQueryMethod(IntrospectedTable introspectedTable) {
        Method method = new Method("countByQuery");
        method.setDefault(true);
        method.setReturnType(new FullyQualifiedJavaType(long.class.getSimpleName()));
        method.addParameter(createDtoParameter(introspectedTable));
        method.addBodyLine(String.format(Locale.ROOT,
                "return count(%s.null2Empty(dto).toSelectCount().render(RenderingStrategies.MYBATIS3));",
                createDtoParameter(introspectedTable).getType().getShortName()));
        return method;
    }

    private Method createDeleteByQueryMethod(IntrospectedTable introspectedTable) {
        Method method = new Method("deleteByQuery");
        method.setDefault(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(createDtoParameter(introspectedTable));
        method.addBodyLine(String.format(Locale.ROOT,
                "return delete(%s.null2Empty(dto).toDelete().render(RenderingStrategies.MYBATIS3));",
                createDtoParameter(introspectedTable).getType().getShortName()));
        return method;
    }

    private Parameter createDtoParameter(IntrospectedTable introspectedTable) {
        return new Parameter(
                EntityQueryConditionGeneratePlugin.getEntityQueryConditionType(introspectedTable, this.properties),
                "dto"
        );
    }

}
