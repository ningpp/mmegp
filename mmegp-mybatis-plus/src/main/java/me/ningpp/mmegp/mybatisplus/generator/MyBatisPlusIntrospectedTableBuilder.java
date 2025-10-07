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
package me.ningpp.mmegp.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.type.Type;
import me.ningpp.mmegp.DefaultIntrospectedTableBuilder;
import me.ningpp.mmegp.GeneratedTableInfo;
import me.ningpp.mmegp.IntrospectedColumnMmegpImpl;
import me.ningpp.mmegp.annotationparser.ModelBasedAnnotationParser;
import me.ningpp.mmegp.enums.SoftDeleteStrategy;
import me.ningpp.mmegp.meta.model.SoftDeleteModel;
import me.ningpp.mmegp.mybatisplus.annotationmodel.TableFieldModel;
import me.ningpp.mmegp.mybatisplus.annotationmodel.TableIdModel;
import me.ningpp.mmegp.mybatisplus.annotationmodel.TableLogicModel;
import me.ningpp.mmegp.mybatisplus.annotationmodel.TableNameModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.ningpp.mmegp.JavaParserUtil.getClassByType;
import static me.ningpp.mmegp.JavaParserUtil.parseColumnName;
import static me.ningpp.mmegp.JavaParserUtil.parseJdbcType;
import static me.ningpp.mmegp.JavaParserUtil.parseTableName;
import static me.ningpp.mmegp.JavaParserUtil.parseTypeHandler;

public class MyBatisPlusIntrospectedTableBuilder extends DefaultIntrospectedTableBuilder {

    @Override
    protected GeneratedTableInfo buildGeneratedTable(Context context,
            TypeDeclaration<?> modelDeclaration, NodeList<ImportDeclaration> importDeclarations) {

        TableNameModel tableNameModel = ModelBasedAnnotationParser.parse(TableName.class, TableNameModel.class,
                modelDeclaration, importDeclarations);
        if (tableNameModel == null) {
            return null;
        } else {
            return new GeneratedTableInfo(
                    parseTableName(
                            tableNameModel.value(),
                            modelDeclaration.getNameAsString(),
                            context
                    ), List.of());
        }
    }

    @Override
    protected <N1 extends Node, N2 extends Node> Pair<IntrospectedColumn, Boolean> buildColumn(
            IntrospectedTable introspectedTable,
            TypeDeclaration<?> modelDeclaration,
            Map<String, String> declarMappings,
            Context context,
            NodeWithAnnotations<N1> annotationNode,
            NodeWithType<N2, Type> typeNode,
            NodeWithSimpleName<N2> nameNode) {

        var tableFieldModel = ModelBasedAnnotationParser.parse(TableField.class, TableFieldModel.class,
                annotationNode, declarMappings);
        Map<String, List<MemberValuePair>> annotationMembers;
        if (tableFieldModel != null) {
            if (! tableFieldModel.exist()) {
                return null;
            }

            annotationMembers = getNormalAnnotationMembers(
                    annotationNode.getAnnotationByClass(TableField.class));
        } else {
            annotationMembers = new HashMap<>(0);
        }

        String typeClassName = getClassByType(declarMappings, typeNode.getType());
        String javaProperty = nameNode.getNameAsString();
        String name = tableFieldModel == null ? null : tableFieldModel.value();
        if (StringUtils.isEmpty(name)) {
            name = parseColumnName(name, javaProperty, context);
        }
        JdbcType jdbcType = parseJdbcType(typeClassName, annotationMembers);
        if (jdbcType == null) {
            throw new IllegalStateException("not support type for : "
                    + introspectedTable.getBaseRecordType() + "#" + javaProperty);
        }

        IntrospectedColumnMmegpImpl column = new IntrospectedColumnMmegpImpl();
        column.setIntrospectedTable(introspectedTable);
        column.setContext(context);
        column.setActualColumnName(name);
        column.setJavaProperty(javaProperty);

        column.setBlobColumn(false);

        TableIdModel tableIdModel = ModelBasedAnnotationParser.parse(
                TableId.class, TableIdModel.class,
                annotationNode, declarMappings);
        boolean id = tableIdModel != null;
        boolean generatedValue = tableIdModel != null
                && IdType.AUTO == tableIdModel.type();
        column.setIdentity(id && generatedValue);
        column.setAutoIncrement(generatedValue);

        column.setJdbcType(jdbcType.TYPE_CODE);
        column.setJdbcTypeName(jdbcType.name());
        column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(typeClassName));
        column.setTypeHandler(parseTypeHandler(annotationMembers, declarMappings));

        handleSoftDelete(column, declarMappings, annotationNode);

        return Pair.of(column, id);
    }

    private <N1 extends Node> void handleSoftDelete(IntrospectedColumn column,
            Map<String, String> declarMappings,
            NodeWithAnnotations<N1> annotationNode) {
        TableLogicModel tableLogicModel = ModelBasedAnnotationParser.parse(
                TableLogic.class, TableLogicModel.class,
                annotationNode, declarMappings);
        if (tableLogicModel == null) {
            return;
        }

        String notDeletedValue = tableLogicModel.value();
        if (StringUtils.isEmpty(notDeletedValue)) {
            notDeletedValue = (String) column.getContext().getProperties()
                    .getOrDefault("mybatisPlusNotDeletedValue", "0");
        }

        String deletedValue = tableLogicModel.delval();
        if (StringUtils.isEmpty(deletedValue)) {
            deletedValue = (String) column.getContext().getProperties()
                    .getOrDefault("mybatisPlusDeletedValue", "1");
        }

        column.getIntrospectedTable().setAttribute(SoftDeleteModel.class.getName(),
                new SoftDeleteModel(column, SoftDeleteStrategy.FIXED_VALUE,
                        notDeletedValue, deletedValue));
    }

    protected List<Pair<IntrospectedColumn, Boolean>> afterBuildColumns(List<Pair<IntrospectedColumn, Boolean>> oriPairs) {
        List<Pair<IntrospectedColumn, Boolean>> results = new ArrayList<>();
        IntrospectedColumn idColumn = null;
        boolean hasPk = false;
        for (Pair<IntrospectedColumn, Boolean> pair : oriPairs) {
            if (pair == null) {
                continue;
            }
            if (Boolean.TRUE.equals(pair.getRight())) {
                hasPk = true;
            }
            if ("id".equals(pair.getLeft().getJavaProperty())) {
                idColumn = pair.getLeft();
            }
        }

        for (Pair<IntrospectedColumn, Boolean> pair : oriPairs) {
            if (pair == null) {
                continue;
            }
            if (!hasPk && pair.getLeft().getActualColumnName().equals(idColumn.getActualColumnName())) {
                results.add(Pair.of(pair.getLeft(), Boolean.TRUE));
            } else {
                results.add(pair);
            }
        }
        return results;
    }

}
