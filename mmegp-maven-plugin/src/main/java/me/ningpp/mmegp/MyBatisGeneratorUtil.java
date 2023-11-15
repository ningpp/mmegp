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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
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
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.ObjectFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

public final class MyBatisGeneratorUtil {

    private static final Map<Class<?>, JdbcType> JDBC_TYPE_MAPPING = new HashMap<>();

    private static final Set<String> EXCLUDE_IMPORTS = new HashSet<>();

    static {
        JDBC_TYPE_MAPPING.put(Boolean.class, JdbcType.BOOLEAN);
        JDBC_TYPE_MAPPING.put(boolean.class, JdbcType.BOOLEAN);

        JDBC_TYPE_MAPPING.put(Byte.class, JdbcType.TINYINT);
        JDBC_TYPE_MAPPING.put(byte.class, JdbcType.TINYINT);

        JDBC_TYPE_MAPPING.put(Short.class, JdbcType.SMALLINT);
        JDBC_TYPE_MAPPING.put(short.class, JdbcType.SMALLINT);

        JDBC_TYPE_MAPPING.put(Integer.class, JdbcType.INTEGER);
        JDBC_TYPE_MAPPING.put(int.class, JdbcType.INTEGER);

        JDBC_TYPE_MAPPING.put(Long.class, JdbcType.BIGINT);
        JDBC_TYPE_MAPPING.put(long.class, JdbcType.BIGINT);

        JDBC_TYPE_MAPPING.put(Float.class, JdbcType.FLOAT);
        JDBC_TYPE_MAPPING.put(float.class, JdbcType.FLOAT);

        JDBC_TYPE_MAPPING.put(Double.class, JdbcType.DOUBLE);
        JDBC_TYPE_MAPPING.put(double.class, JdbcType.DOUBLE);

        JDBC_TYPE_MAPPING.put(String.class, JdbcType.VARCHAR);
        JDBC_TYPE_MAPPING.put(Character.class, JdbcType.VARCHAR);
        JDBC_TYPE_MAPPING.put(char.class, JdbcType.VARCHAR);

        JDBC_TYPE_MAPPING.put(BigInteger.class, JdbcType.BIGINT);

        JDBC_TYPE_MAPPING.put(BigDecimal.class, JdbcType.DECIMAL);

        JDBC_TYPE_MAPPING.put(byte[].class, JdbcType.BLOB);
        JDBC_TYPE_MAPPING.put(Byte[].class, JdbcType.BLOB);

        JDBC_TYPE_MAPPING.put(Date.class, JdbcType.TIMESTAMP);
        JDBC_TYPE_MAPPING.put(java.sql.Date.class, JdbcType.DATE);
        JDBC_TYPE_MAPPING.put(LocalTime.class, JdbcType.TIME);
        JDBC_TYPE_MAPPING.put(LocalDate.class, JdbcType.TIMESTAMP);
        JDBC_TYPE_MAPPING.put(LocalDateTime.class, JdbcType.TIMESTAMP);

        EXCLUDE_IMPORTS.add("me.ningpp.mmegp.annotations.Generated");
        EXCLUDE_IMPORTS.add("me.ningpp.mmegp.annotations.GeneratedColumn");
        EXCLUDE_IMPORTS.add("org.apache.ibatis.type.JdbcType");
    }

    private MyBatisGeneratorUtil() {
    }

    public static JdbcType getJdbcTypeByClass(Class<?> clazz) {
        return JDBC_TYPE_MAPPING.get(clazz);
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
        String domainObjectName = new FullyQualifiedJavaType(modelDeclaration.getFullyQualifiedName().get()).getShortName();
        IntrospectedTable introspectedTable = ObjectFactory.createIntrospectedTableForValidation(context);
        FullyQualifiedTable table = new FullyQualifiedTable(null, null, tableInfo.getName(), domainObjectName, null, false, null, null, null, false, null, context);
        introspectedTable.setFullyQualifiedTable(table);
        
        introspectedTable.setContext(context);
        introspectedTable.setBaseRecordType(modelDeclaration.getFullyQualifiedName().get());

        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setDomainObjectName(domainObjectName);
        tableConfiguration.setTableName(tableInfo.getName());
        tableConfiguration.addProperty(JavaParserUtil.COUNT_GROUP_BY_COLUMNS_NAME,
                String.join(";", tableInfo.getCountGroupByColumns()));
        introspectedTable.setTableConfiguration(tableConfiguration);

        introspectedTable.setExampleType(modelDeclaration.getFullyQualifiedName().get() + "Example");
        introspectedTable.setMyBatis3JavaMapperType(context.getJavaClientGeneratorConfiguration().getTargetPackage() + "." + domainObjectName + "Mapper");

        Map<String, ImportDeclaration> importMappings = new HashMap<>();
        for (ImportDeclaration importDeclar : importDeclarations) {
            importMappings.put(new FullyQualifiedJavaType(importDeclar.getNameAsString()).getShortName(), importDeclar);
        }

        List<Pair<IntrospectedColumn, Boolean>> pairs = buildColumns(
                modelDeclaration, importMappings, context
        );

        addTableColumns(introspectedTable, pairs);

        if (introspectedTable.getAllColumns().isEmpty()) {
            return null;
        }

        if (metaInfoHandler != null) {
            metaInfoHandler.handle(introspectedTable, modelDeclaration);
        }

        introspectedTable.setAttribute(
            ModelType.class.getName(),
            modelDeclaration.isRecordDeclaration() ? ModelType.RECORD : ModelType.CLASS
        );
        tableConfiguration.getProperties()
            .setProperty(
                PropertyRegistry.ANY_IMMUTABLE,
                String.valueOf(modelDeclaration.isRecordDeclaration()));
        return introspectedTable;
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
                                        List<Pair<IntrospectedColumn, Boolean>> pairs) {
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

}
