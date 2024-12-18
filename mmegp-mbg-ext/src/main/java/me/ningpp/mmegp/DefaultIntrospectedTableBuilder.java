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
package me.ningpp.mmegp;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.type.Type;
import me.ningpp.mmegp.annotations.Table;
import me.ningpp.mmegp.annotations.Column;
import me.ningpp.mmegp.enums.AggregateFunction;
import me.ningpp.mmegp.enums.ModelType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.PropertyHolder;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.File;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.ningpp.mmegp.JavaParserUtil.AGGREGATES_NAME;
import static me.ningpp.mmegp.JavaParserUtil.COUNT_GROUP_BY_COLUMNS_NAME;
import static me.ningpp.mmegp.JavaParserUtil.getClassByType;
import static me.ningpp.mmegp.JavaParserUtil.parseArray;
import static me.ningpp.mmegp.JavaParserUtil.parseArrayString;
import static me.ningpp.mmegp.JavaParserUtil.parseBoolean;
import static me.ningpp.mmegp.JavaParserUtil.parseColumnName;
import static me.ningpp.mmegp.JavaParserUtil.parseJdbcType;
import static me.ningpp.mmegp.JavaParserUtil.parseTableName;
import static me.ningpp.mmegp.JavaParserUtil.parseTypeHandler;

public class DefaultIntrospectedTableBuilder implements IntrospectedTableBuilder {

    protected GeneratedTableInfo buildGeneratedTable(Context context,
            TypeDeclaration<?> modelDeclaration, NodeList<ImportDeclaration> importDeclarations) {
        if (modelDeclaration == null || modelDeclaration.getFullyQualifiedName().isEmpty()) {
            return null;
        }
        var annotationMembers = getNormalAnnotationMembers(modelDeclaration, Table.class);

        String tableName = parseTableName(annotationMembers, modelDeclaration.getNameAsString(), context);
        List<String> countGroupByColumns = parseArrayString(annotationMembers, COUNT_GROUP_BY_COLUMNS_NAME);
        return new GeneratedTableInfo(tableName, countGroupByColumns);
    }

    @Override
    public Pair<IntrospectedTable, File> buildFromSourceFile(Context context, File file,
            MetaInfoHandler metaInfoHandler) {
        try {
            String fileContent = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            ParseResult<CompilationUnit> parseResult = JavaParserUtil.newParser().parse(fileContent);
            Optional<CompilationUnit> cuOptional = parseResult.getResult();
            if (parseResult.isSuccessful() && cuOptional.isPresent()) {
                return Pair.of(
                        buildIntrospectedTable(
                                context,
                                cuOptional.get(),
                                metaInfoHandler
                        ),
                        file);
            }
        } catch (Exception e) {
            throw new GenerateMyBatisExampleException(e.getMessage(), e);
        }
        return Pair.of(null, file);
    }

    public IntrospectedTable buildIntrospectedTable(Context context,
               CompilationUnit compilationUnit, MetaInfoHandler metaInfoHandler) {
        TypeDeclaration<?> typeDeclaration = compilationUnit.getTypes().stream()
                .filter(typeDeclar -> typeDeclar.hasModifier(Modifier.Keyword.PUBLIC))
                .findFirst().orElse(null);
        if (typeDeclaration != null) {
            if (typeDeclaration.isRecordDeclaration()
                    || (typeDeclaration.isClassOrInterfaceDeclaration()
                    && !typeDeclaration.asClassOrInterfaceDeclaration().isInterface())) {
                return buildIntrospectedTable(
                        context,
                        typeDeclaration,
                        compilationUnit.getImports(),
                        metaInfoHandler);
            }
        }
        return null;
    }

    private IntrospectedTable buildIntrospectedTable(Context context,
                                                            TypeDeclaration<?> modelDeclaration,
                                                            NodeList<ImportDeclaration> importDeclarations,
                                                            MetaInfoHandler metaInfoHandler) {
        GeneratedTableInfo tableInfo = buildGeneratedTable(context, modelDeclaration, importDeclarations);
        if (tableInfo == null || StringUtils.isEmpty(tableInfo.getName())) {
            return null;
        }
        FullyQualifiedJavaType baseType = new FullyQualifiedJavaType(modelDeclaration.getFullyQualifiedName().get());
        IntrospectedTable introspectedTable = ObjectFactory.createIntrospectedTableForValidation(context);
        introspectedTable.setContext(context);
        introspectedTable.setBaseRecordType(baseType.getFullyQualifiedName());

        initTableAttributes(context, introspectedTable, baseType, tableInfo, modelDeclaration);

        addTableColumns(introspectedTable, modelDeclaration, importDeclarations, context);

        if (!introspectedTable.hasAnyColumns()) {
            return null;
        }

        if (metaInfoHandler != null) {
            metaInfoHandler.handle(introspectedTable, modelDeclaration);
        }

        return introspectedTable;
    }

    private static void initTableAttributes(Context context, IntrospectedTable introspectedTable,
                                            FullyQualifiedJavaType baseType, GeneratedTableInfo tableInfo,
                                            TypeDeclaration<?> modelDeclaration) {
        String domainObjectName = baseType.getShortName();
        FullyQualifiedTable table = new FullyQualifiedTable(null, null, tableInfo.getName(), domainObjectName,
                null, false, null, null, null, false, null, context);
        introspectedTable.setFullyQualifiedTable(table);

        ModelType modelType = modelDeclaration.isRecordDeclaration() ? ModelType.RECORD : ModelType.CLASS;
        introspectedTable.setAttribute(ModelType.class.getName(), modelType);
        introspectedTable.setTableConfiguration(
                buildTableConfiguration(context, modelType, domainObjectName, tableInfo));
    }

    private static String getPropertyValue(PropertyHolder propertyHolder, String key, String defaultValue) {
        String keyValue = propertyHolder.getProperty(key);
        if (StringUtility.stringHasValue(keyValue)) {
            return keyValue;
        }
        return defaultValue;
    }

    private static TableConfiguration buildTableConfiguration(Context context, ModelType modelType,
                                                              String domainObjectName, GeneratedTableInfo tableInfo) {
        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setDomainObjectName(domainObjectName);
        tableConfiguration.setTableName(tableInfo.getName());
        if (context.getJavaClientGeneratorConfiguration() != null) {
            String mapperSuffix = getPropertyValue(
                    context.getJavaClientGeneratorConfiguration(),
                    "mapperNameSuffix", "Mapper");
            tableConfiguration.setMapperName(domainObjectName + mapperSuffix);
        }
        tableConfiguration.addProperty(COUNT_GROUP_BY_COLUMNS_NAME,
                String.join(";", tableInfo.getCountGroupByColumns()));
        tableConfiguration.getProperties()
                .setProperty(
                        PropertyRegistry.ANY_IMMUTABLE,
                        String.valueOf(modelType == ModelType.RECORD));
        return tableConfiguration;
    }

    private List<Pair<IntrospectedColumn, Boolean>> buildColumns(TypeDeclaration<?> modelDeclaration,
                Map<String, ImportDeclaration> importMappings, Context context) {
        List<Pair<IntrospectedColumn, Boolean>> pairs = new ArrayList<>();
        if (modelDeclaration.isRecordDeclaration()) {
            NodeList<Parameter> parameters = modelDeclaration.asRecordDeclaration().getParameters();
            if (parameters != null) {
                for (Parameter param : parameters) {
                    pairs.add(buildColumn(modelDeclaration, importMappings, param, context));
                }
            }
        } else {
            List<FieldDeclaration> fields = modelDeclaration.getFields();
            if (fields != null) {
                for (FieldDeclaration field : fields) {
                    pairs.add(buildColumn(modelDeclaration, importMappings, field, context));
                }
            }
        }
        return pairs.stream().filter(Objects::nonNull).toList();
    }

    private Pair<IntrospectedColumn, Boolean> buildColumn(TypeDeclaration<?> modelDeclaration,
            Map<String, ImportDeclaration> declarMappings,
            Parameter param,
            Context context) {
        return buildColumn(modelDeclaration, declarMappings, context, param, param, param);
    }

    private Pair<IntrospectedColumn, Boolean> buildColumn(TypeDeclaration<?> modelDeclaration,
            Map<String, ImportDeclaration> declarMappings,
            FieldDeclaration field,
            Context context) {
        if (field.getVariables().size() > 1) {
            throw new GenerateMyBatisExampleException("can't use multi variables declaration! Model="
                    + modelDeclaration.getFullyQualifiedName().orElse(null)
                    + ", field = " + field.getVariables().stream()
                    .map(VariableDeclarator::getNameAsString)
                    .collect(Collectors.joining(", ")));
        }
        return buildColumn(modelDeclaration, declarMappings, context,
                field, field.getVariable(0), field.getVariable(0));
    }

    protected Map<String, List<MemberValuePair>> getNormalAnnotationMembers(
                Optional<AnnotationExpr> optionalColumnAnno) {
        if (optionalColumnAnno.isEmpty()
                || !optionalColumnAnno.get().isNormalAnnotationExpr()) {
            return Map.of();
        }
        return optionalColumnAnno.get().asNormalAnnotationExpr()
                .getPairs().stream().collect(
                        Collectors.groupingBy(MemberValuePair::getNameAsString));
    }

    private Map<String, List<MemberValuePair>> getNormalAnnotationMembers(
            NodeWithAnnotations<?> annotationNode, Class<? extends Annotation> annotationClass) {
        Optional<AnnotationExpr> optionalColumnAnno = annotationNode.getAnnotationByClass(annotationClass);
        return getNormalAnnotationMembers(optionalColumnAnno);
    }

    protected <N1 extends Node, N2 extends Node> Pair<IntrospectedColumn, Boolean> buildColumn(
            TypeDeclaration<?> modelDeclaration,
            Map<String, ImportDeclaration> declarMappings,
            Context context,
            NodeWithAnnotations<N1> annotationNode,
            NodeWithType<N2, Type> typeNode,
            NodeWithSimpleName<N2> nameNode) {
        Optional<AnnotationExpr> annoOptional = annotationNode.getAnnotationByClass(Column.class);
        if (annoOptional.isEmpty()) {
            return null;
        }
        var annotationMembers = getNormalAnnotationMembers(annoOptional);

        String typeClassName = getClassByType(declarMappings, typeNode.getType());
        if (StringUtils.isEmpty(typeClassName)) {
            throw new GenerateMyBatisExampleException(String.format(Locale.ROOT,
                    "not supported Java Type, field = %s, type = %s, FullyQualifiedName = %s",
                    nameNode.getNameAsString(), typeNode.getType().toString(),
                    modelDeclaration.getFullyQualifiedName().orElse(null)));
        }

        String javaProperty = nameNode.getNameAsString();
        String name = parseColumnName(annotationMembers, javaProperty, context);
        JdbcType jdbcType = parseJdbcType(typeClassName, annotationMembers);
        if (StringUtils.isEmpty(name) || jdbcType == null) {
            throw new GenerateMyBatisExampleException(String.format(Locale.ROOT,
                    "can't get column name or jdbcType, field = %s, type = %s, FullyQualifiedName = %s",
                    javaProperty, typeNode.getType().toString(),
                    modelDeclaration.getFullyQualifiedName().orElse(null)));
        }

        IntrospectedColumnMmegpImpl column = new IntrospectedColumnMmegpImpl();
        column.setContext(context);
        column.setActualColumnName(name);
        column.setJavaProperty(javaProperty);

        column.setBlobColumn(parseBoolean(annotationMembers, "blob", false));

        boolean id = parseBoolean(annotationMembers, "id", false);
        boolean generatedValue = parseBoolean(annotationMembers, "generatedValue", false);
        column.setIdentity(id && generatedValue);
        column.setAutoIncrement(generatedValue);

        column.setJdbcType(jdbcType.TYPE_CODE);
        column.setJdbcTypeName(jdbcType.name());
        column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(typeClassName));
        column.setTypeHandler(parseTypeHandler(annotationMembers, declarMappings));

        column.getProperties().put(AGGREGATES_NAME,
                parseAggregates(annotationMembers).stream()
                        .map(AggregateFunction::name)
                        .collect(Collectors.joining(",")));

        return Pair.of(column, id);
    }

    private List<AggregateFunction> parseAggregates(Map<String, List<MemberValuePair>> annotationMembers) {
        return parseArray(annotationMembers, AGGREGATES_NAME)
                .stream().map(this::parseAggregate)
                .filter(Objects::nonNull).toList();
    }

    private AggregateFunction parseAggregate(Expression exp) {
        String value = null;
        if (exp != null && exp.isFieldAccessExpr()) {
            value = exp.asFieldAccessExpr().getNameAsString();
        }
        return AggregateFunction.parse(value);
    }

    private void addTableColumns(IntrospectedTable introspectedTable,
                                        TypeDeclaration<?> modelDeclaration,
                                        NodeList<ImportDeclaration> importDeclarations,
                                        Context context) {
        Map<String, ImportDeclaration> importMappings = new HashMap<>();
        for (ImportDeclaration importDeclar : importDeclarations) {
            importMappings.put(new FullyQualifiedJavaType(importDeclar.getNameAsString()).getShortName(), importDeclar);
        }

        List<Pair<IntrospectedColumn, Boolean>> pairs = buildColumns(
                modelDeclaration, importMappings, context
        );

        for (Pair<IntrospectedColumn, Boolean> pair : pairs) {
            if (pair != null) {
                introspectedTable.addColumn(pair.getLeft());

                if (Boolean.TRUE.equals(pair.getRight())) {
                    introspectedTable.addPrimaryKeyColumn(pair.getLeft().getActualColumnName());
                }

                if (pair.getLeft().isIdentity()) {
                    introspectedTable.getTableConfiguration()
                            .setGeneratedKey(new GeneratedKey(
                                    pair.getLeft().getActualColumnName(),
                                    "JDBC", true, null));
                }
            }
        }
    }

}
