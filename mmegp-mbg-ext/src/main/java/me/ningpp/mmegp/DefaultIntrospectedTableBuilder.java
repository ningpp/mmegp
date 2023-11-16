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
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import me.ningpp.mmegp.enums.ModelType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.PropertyHolder;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultIntrospectedTableBuilder implements IntrospectedTableBuilder {

    @Override
    public Pair<IntrospectedTable, File> buildFromSourceFile(Context context, File file, MetaInfoHandler metaInfoHandler) {
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

    public static IntrospectedTable buildIntrospectedTable(Context context,
                                                           CompilationUnit compilationUnit,
                                                           MetaInfoHandler metaInfoHandler) {
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

    private static IntrospectedTable buildIntrospectedTable(Context context,
                                                            TypeDeclaration<?> modelDeclaration,
                                                            NodeList<ImportDeclaration> importDeclarations,
                                                            MetaInfoHandler metaInfoHandler) {
        if (modelDeclaration == null || modelDeclaration.getFullyQualifiedName().isEmpty()) {
            return null;
        }
        GeneratedTableInfo tableInfo = JavaParserUtil.getTableValue(modelDeclaration);
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
        introspectedTable.setTableConfiguration(buildTableConfiguration(context, modelType, domainObjectName, tableInfo));

        String exampleTargetPackage = getPropertyValue(
                context.getJavaModelGeneratorConfiguration(),
                PropertyRegistry.MODEL_GENERATOR_EXAMPLE_PACKAGE,
                baseType.getPackageName());
        introspectedTable.setExampleType(exampleTargetPackage + "." + domainObjectName + "Example");

        if (context.getJavaClientGeneratorConfiguration() != null) {
            introspectedTable.setMyBatis3JavaMapperType(
                    getMyBatis3JavaMapperType(introspectedTable));
        }
    }

    public static String getMyBatis3JavaMapperType(IntrospectedTable introspectedTable) {
        JavaClientGeneratorConfiguration cfg = introspectedTable.getContext()
                .getJavaClientGeneratorConfiguration();
        return cfg.getTargetPackage() + "."
                + introspectedTable.getTableConfiguration().getDomainObjectName()
                + getPropertyValue(cfg, "mapperNameSuffix", "Mapper");
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
        tableConfiguration.addProperty(JavaParserUtil.COUNT_GROUP_BY_COLUMNS_NAME,
                String.join(";", tableInfo.getCountGroupByColumns()));
        tableConfiguration.getProperties()
                .setProperty(
                        PropertyRegistry.ANY_IMMUTABLE,
                        String.valueOf(modelType == ModelType.RECORD));
        return tableConfiguration;
    }

    private static List<Pair<IntrospectedColumn, Boolean>> buildColumns(TypeDeclaration<?> modelDeclaration,
                                                                        Map<String, ImportDeclaration> importMappings,
                                                                        Context context) {
        List<Pair<IntrospectedColumn, Boolean>> pairs = new ArrayList<>();
        if (modelDeclaration.isRecordDeclaration()) {
            NodeList<Parameter> parameters = modelDeclaration.asRecordDeclaration().getParameters();
            if (parameters != null) {
                for (Parameter param : parameters) {
                    pairs.add(JavaParserUtil.buildColumn(modelDeclaration, importMappings, param, context));
                }
            }
        } else {
            List<FieldDeclaration> fields = modelDeclaration.getFields();
            if (fields != null) {
                for (FieldDeclaration field : fields) {
                    pairs.add(JavaParserUtil.buildColumn(modelDeclaration, importMappings, field, context));
                }
            }
        }
        return pairs;
    }

    private static void addTableColumns(IntrospectedTable introspectedTable,
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
