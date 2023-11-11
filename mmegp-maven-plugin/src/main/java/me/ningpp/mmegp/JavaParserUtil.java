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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import me.ningpp.mmegp.annotations.Generated;
import me.ningpp.mmegp.annotations.GeneratedColumn;
import me.ningpp.mmegp.enums.AggregateFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class JavaParserUtil {
    private JavaParserUtil() {
    }

    private static final Map<String, JdbcType> JDBC_TYPES = new HashMap<>();
    public static final String COUNT_GROUP_BY_COLUMNS_NAME = "countGroupByColumns";
    public static final String AGGREGATES_NAME = "aggregates";

    static {
        for (JdbcType jdbcType : JdbcType.values()) {
            JDBC_TYPES.put(jdbcType.name(), jdbcType);
        }
    }

    public static JavaParser newParser() {
        ParserConfiguration jpc = new ParserConfiguration();
        jpc.setLanguageLevel(LanguageLevel.JAVA_17);
        jpc.setCharacterEncoding(StandardCharsets.UTF_8);
        return new JavaParser(jpc);
    }

    public static GeneratedTableInfo getTableValue(NodeWithAnnotations<?> annotationNode) {
        Map<String, List<MemberValuePair>> annotationMembers = getNormalAnnotationMembers(annotationNode, Generated.class);
        if (annotationMembers.isEmpty()) {
            return null;
        }

        String tableName = parseString(annotationMembers, "table", null);
        List<String> countGroupByColumns = parseArrayString(annotationMembers, COUNT_GROUP_BY_COLUMNS_NAME);
        return new GeneratedTableInfo(tableName, countGroupByColumns);
    }

    public static Pair<IntrospectedColumn, Boolean> buildColumn(TypeDeclaration<?> modelDeclaration,
                                                                Map<String, ImportDeclaration> declarMappings,
                                                                Parameter param,
                                                                Context context) throws ClassNotFoundException {
        return buildColumn(modelDeclaration, declarMappings, context, param, param, param);
    }

    public static Pair<IntrospectedColumn, Boolean> buildColumn(TypeDeclaration<?> modelDeclaration,
                                                                Map<String, ImportDeclaration> declarMappings,
                                                                FieldDeclaration field,
                                                                Context context) throws ClassNotFoundException {
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

    private static Map<String, List<MemberValuePair>> getNormalAnnotationMembers(NodeWithAnnotations<?> annotationNode,
                                                                                 Class<? extends Annotation> annotationClass) {
        Optional<AnnotationExpr> optionalColumnAnno = annotationNode.getAnnotationByClass(annotationClass);
        if (optionalColumnAnno.isEmpty()
                || !optionalColumnAnno.get().isNormalAnnotationExpr()) {
            return Map.of();
        }
        return optionalColumnAnno.get().asNormalAnnotationExpr()
                .getPairs().stream().collect(
                        Collectors.groupingBy(MemberValuePair::getNameAsString));
    }

    private static <N1 extends Node, N2 extends Node> Pair<IntrospectedColumn, Boolean> buildColumn(
            TypeDeclaration<?> modelDeclaration,
            Map<String, ImportDeclaration> declarMappings,
            Context context,
            NodeWithAnnotations<N1> annotationNode,
            NodeWithType<N2, Type> typeNode,
            NodeWithSimpleName<N2> nameNode) throws ClassNotFoundException {
        Map<String, List<MemberValuePair>> annotationMembers = getNormalAnnotationMembers(annotationNode, GeneratedColumn.class);
        if (annotationMembers.isEmpty()) {
            return null;
        }

        String name = parseString(annotationMembers, "name", null);
        JdbcType jdbcType = parseJdbcType(annotationMembers);
        if (StringUtils.isEmpty(name) || jdbcType == null) {
            throw new GenerateMyBatisExampleException(String.format(Locale.ROOT,
                    "can't get column name or jdbcType, field = %s, type = %s, FullyQualifiedName = %s",
                    nameNode.getNameAsString(), typeNode.getType().toString(),
                    modelDeclaration.getFullyQualifiedName().orElse(null)));
        }

        String className = getClassByType(declarMappings, typeNode.getType());
        if (StringUtils.isEmpty(className)) {
            throw new GenerateMyBatisExampleException(String.format(Locale.ROOT,
                    "not supported Java Type, field = %s, type = %s, FullyQualifiedName = %s",
                    nameNode.getNameAsString(), typeNode.getType().toString(),
                    modelDeclaration.getFullyQualifiedName().orElse(null)));
        }

        IntrospectedColumnMmegpImpl column = new IntrospectedColumnMmegpImpl();
        column.setContext(context);
        column.setActualColumnName(name);
        column.setJavaProperty(nameNode.getNameAsString());

        column.setBlobColumn(parseBoolean(annotationMembers, "blob", false));

        boolean id = parseBoolean(annotationMembers, "id", false);
        boolean generatedValue = parseBoolean(annotationMembers, "generatedValue", false);
        column.setIdentity(id && generatedValue);
        column.setAutoIncrement(generatedValue);

        column.setJdbcType(jdbcType.TYPE_CODE);
        column.setJdbcTypeName(jdbcType.name());
        column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(className));
        column.setTypeHandler(parseTypeHandler(annotationMembers, declarMappings));

        column.getProperties().put(AGGREGATES_NAME,
                parseAggregates(annotationMembers).stream()
                        .map(AggregateFunction::name)
                        .collect(Collectors.joining(",")));

        return Pair.of(column, id);
    }

    private static String parseTypeHandler(Map<String, List<MemberValuePair>> annotationMembers,
                                           Map<String, ImportDeclaration> declarMappings) {
        return parse(annotationMembers, "typeHandler")
                .filter(Expression::isClassExpr)
                .map(mv -> getMatchedType(declarMappings, mv.asClassExpr().getType()))
                .orElse(null);
    }

    private static JdbcType parseJdbcType(Map<String, List<MemberValuePair>> annotationMembers) {
        return parse(annotationMembers, "jdbcType")
                .flatMap(memberValue -> {
                    String jdbcTypeName = null;
                    if (memberValue.isFieldAccessExpr()) {
                        jdbcTypeName = memberValue.asFieldAccessExpr().getNameAsString();
                    } else if (memberValue.isNameExpr()) {
                        jdbcTypeName = memberValue.asNameExpr().getNameAsString();
                    }
                    return Optional.ofNullable(JDBC_TYPES.get(jdbcTypeName));
                }).orElse(null);
    }

    private static List<Expression> parseArray(Map<String, List<MemberValuePair>> annotationMembers, String name) {
        Optional<ArrayInitializerExpr> arrayInitExpr = parse(annotationMembers, name)
                .filter(Expression::isArrayInitializerExpr)
                .map(Expression::asArrayInitializerExpr);
        if (arrayInitExpr.isPresent()) {
            return arrayInitExpr.get().getValues();
        } else {
            Optional<Expression> expr = parse(annotationMembers, name);
            return expr.map(List::of).orElseGet(List::of);
        }
    }

    private static List<String> parseArrayString(Map<String, List<MemberValuePair>> annotationMembers, String name) {
        return parseArray(annotationMembers, name)
                .stream().filter(Expression::isStringLiteralExpr)
                .map(expr -> expr.asStringLiteralExpr().asString())
                .collect(Collectors.toList());
    }

    private static String parseString(Map<String, List<MemberValuePair>> annotationMembers, String name, String defaultValue) {
        return parse(annotationMembers, name)
                .filter(Expression::isStringLiteralExpr)
                .map(mv -> mv.asStringLiteralExpr().asString())
                .orElse(defaultValue);
    }

    private static boolean parseBoolean(Map<String, List<MemberValuePair>> annotationMembers, String name, boolean defaultValue) {
        return parse(annotationMembers, name)
                .filter(Expression::isBooleanLiteralExpr)
                .map(mv -> mv.asBooleanLiteralExpr().getValue())
                .orElse(defaultValue);
    }

    private static Optional<Expression> parse(Map<String, List<MemberValuePair>> annotationMembers, String name) {
        List<MemberValuePair> pairs = annotationMembers.get(name);
        if (pairs != null && pairs.size() == 1) {
            return Optional.ofNullable(pairs.get(0).getValue());
        }
        return Optional.empty();
    }

    private static <T> List<T> null2Empty(List<T> list) {
        return list == null ? List.of() : list;
    }

    private static List<AggregateFunction> parseAggregates(Map<String, List<MemberValuePair>> annotationMembers) {
        return parseArray(annotationMembers, AGGREGATES_NAME)
                .stream().map(JavaParserUtil::parseAggregate)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static AggregateFunction parseAggregate(Expression exp) {
        String value = null;
        if (exp != null && exp.isFieldAccessExpr()) {
            value = exp.asFieldAccessExpr().getNameAsString();
        }
        return AggregateFunction.parse(value);
    }

    private static final Set<Class<?>> PRIMITIVES_AND_BOXED_TYPES;
    static {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(Boolean.class);
        classes.add(Byte.class);
        classes.add(Character.class);
        classes.add(Short.class);
        classes.add(Integer.class);
        classes.add(Long.class);
        classes.add(Float.class);
        classes.add(Double.class);
        classes.add(Boolean.TYPE);
        classes.add(Byte.TYPE);
        classes.add(Character.TYPE);
        classes.add(Short.TYPE);
        classes.add(Integer.TYPE);
        classes.add(Long.TYPE);
        classes.add(Float.TYPE);
        classes.add(Double.TYPE);
        PRIMITIVES_AND_BOXED_TYPES = Set.copyOf(classes);
    }

    private static final Map<String, Class<?>> MAPPING_TYPES;
    static {
        List<Class<?>> classes = new ArrayList<>(PRIMITIVES_AND_BOXED_TYPES);
        classes.add(String.class);

        classes.add(BigInteger.class);
        classes.add(BigDecimal.class);

        classes.add(LocalTime.class);
        classes.add(LocalDate.class);
        classes.add(LocalDateTime.class);
        classes.add(Year.class);
        classes.add(YearMonth.class);
        classes.add(java.util.Date.class);
        classes.add(java.sql.Time.class);
        classes.add(java.sql.Timestamp.class);

        classes.add(byte[].class);
        classes.add(Byte[].class);

        Map<String, Class<?>> mappings = new HashMap<>();
        mappings.put(java.sql.Date.class.getName(), java.sql.Date.class);
        for (Class<?> clazz : classes) {
            if (!clazz.isArray()) {
                mappings.put(clazz.getName(), clazz);
            }
            mappings.put(clazz.getSimpleName(), clazz);
        }
        MAPPING_TYPES = Map.copyOf(mappings);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println(Integer.TYPE);
        System.out.println(Integer.TYPE.getName());
        System.out.println(Integer.TYPE.getSimpleName());
        System.out.println(Integer.TYPE.getModule());
        System.out.println(Class.forName(
                Integer.TYPE.getName(),
                true,
                Integer.TYPE.getClassLoader()));
    }

    private static String getClassByType(Map<String, ImportDeclaration> declarMappings, Type type) throws ClassNotFoundException {
        Class<?> clazz = MAPPING_TYPES.get(type.asString());
        if (clazz != null) {
            if (clazz.isArray()) {
                return clazz.getSimpleName();
            } else {
                return clazz.getName();
            }
        } else {
            return getMatchedType(declarMappings, type);
        }
    }

    public static String getMatchedType(Map<String, ImportDeclaration> declarMappings, Type type) {
        if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType ctype = type.asClassOrInterfaceType();
            Optional<NodeList<Type>> typeArgs = ctype.getTypeArguments();
            if (typeArgs.isPresent()) {
                int size = typeArgs.get().size();
                if (size == 1) {
                    return String.format(Locale.ROOT,
                            "%s<%s>",
                            getMatchedType(declarMappings, ctype.getNameWithScope()),
                            getMatchedType(declarMappings, typeArgs.get().get(0).asString()));
                } else {
                    return null;
                }
            }
        }
        return getMatchedType(declarMappings, type.asString());
    }

    private static String getMatchedType(Map<String, ImportDeclaration> declarMappings, String typeStr) {
        ImportDeclaration matched = declarMappings.get(typeStr);
        return matched == null ? typeStr : matched.getNameAsString();
    }

}
