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
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.type.Type;

import me.ningpp.mmegp.annotations.GeneratedColumn;
import me.ningpp.mmegp.enums.AggregateFunction;

public final class JavaParserUtil {
    private JavaParserUtil() {
    }

    private static final Map<String, Class<?>> BOXED_TYPES = new HashMap<>();
    public static final String COUNT_GROUP_BY_COLUMNS_NAME = "countGroupByColumns";
    public static final String AGGREGATES_NAME = "aggregates";

    static {
        for (Primitive pt : PrimitiveType.Primitive.values()) {
            try {
                BOXED_TYPES.put(pt.toBoxedType().asString(), Class.forName("java.lang." + pt.toBoxedType().asString()));
            } catch (ClassNotFoundException e) {
                //ignore
            }
        }
    }

    public static JavaParser newParser() {
        ParserConfiguration jpc = new ParserConfiguration();
        jpc.setCharacterEncoding(StandardCharsets.UTF_8);
        return new JavaParser(jpc);
    }

    public static GeneratedTableInfo getTableValue(Optional<AnnotationExpr> generatedAnnotationExpr) {
        if (!generatedAnnotationExpr.isPresent()) {
            return null;
        }
        AnnotationExpr generatedAnnotation = generatedAnnotationExpr.get();
        if (!(generatedAnnotation instanceof NormalAnnotationExpr)) {
            return null;
        }
        NodeList<MemberValuePair> memberParis = ((NormalAnnotationExpr) generatedAnnotation).getPairs();
        if (memberParis == null || memberParis.isEmpty()) {
            return null;
        }

        String tableName = getStringValue("table", memberParis);
        List<String> countGroupByColumns = getArrayStringValue(COUNT_GROUP_BY_COLUMNS_NAME, memberParis);
        if (countGroupByColumns == null) {
            String value = getStringValue(COUNT_GROUP_BY_COLUMNS_NAME, memberParis);
            if (value != null) {
                countGroupByColumns = List.of(value);
            }
        }
        return new GeneratedTableInfo(tableName, countGroupByColumns);
    }

    private static String getStringValue(String name, NodeList<MemberValuePair> memberParis) {
        String value = null;
        for (MemberValuePair memberValuePair : memberParis) {
            value = getStringValue(name, memberValuePair);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    private static String getStringValue(String name, MemberValuePair memberValuePair) {
        String memberName = memberValuePair.getNameAsString();
        Expression memberValue = memberValuePair.getValue();
        if (name.equals(memberName) && memberValue instanceof StringLiteralExpr ) {
            return ((StringLiteralExpr) memberValue).asString();
        }
        return null;
    }

    private static List<String> getArrayStringValue(String name, NodeList<MemberValuePair> memberParis) {
        List<String> value = null;
        for (MemberValuePair memberValuePair : memberParis) {
            value = getArrayStringValue(name, memberValuePair);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    private static List<String> getArrayStringValue(String name, MemberValuePair memberValuePair) {
        String memberName = memberValuePair.getNameAsString();
        Expression memberValue = memberValuePair.getValue();
        if (name.equals(memberName) && memberValue instanceof ArrayInitializerExpr ) {
            List<Expression> expressions = ((ArrayInitializerExpr) memberValue).getValues();
            if (expressions != null) {
                List<String> values = new ArrayList<>();
                for (Expression expression : expressions) {
                    if (expression instanceof StringLiteralExpr) {
                        values.add(((StringLiteralExpr) expression).asString());
                    }
                }
                return values;
            }
        }
        return null;
    }

    public static Pair<IntrospectedColumn, Boolean> buildColumn(ClassOrInterfaceDeclaration modelDeclaration,
                                                                NodeList<ImportDeclaration> importDeclarations,
                                                                FieldDeclaration field,
                                                                Context context) throws ClassNotFoundException {
        if (field.getVariables().size() > 1) {
            throw new GenerateMyBatisExampleException("can't use multi variables declaration! Model="
                    + modelDeclaration.getFullyQualifiedName().get()
                    + ", field = " + field.getVariables().stream()
                                        .map(VariableDeclarator::getNameAsString)
                                        .collect(Collectors.joining(", ")));
        }
        Optional<AnnotationExpr> optionalColumnAnno = field.getAnnotationByClass(GeneratedColumn.class);
        if (!field.isPrivate() || !optionalColumnAnno.isPresent()) {
            return null;
        }
        AnnotationExpr columnAnnotation = optionalColumnAnno.get();
        if (!(columnAnnotation instanceof NormalAnnotationExpr)) {
            return null;
        }
        NodeList<MemberValuePair> memberParis = ((NormalAnnotationExpr) columnAnnotation).getPairs();
        if (memberParis == null || memberParis.isEmpty()) {
            return null;
        }
        IntrospectedColumnMmegpImpl column = new IntrospectedColumnMmegpImpl();
        column.setContext(context);
        
        String name = null;
        JdbcType jdbcType = null;
        boolean blob = false;
        boolean id = false;
        boolean generatedValue = false;
        String typeHandler = null;

        for (MemberValuePair memberValuePair : memberParis) {
            String memberName = memberValuePair.getNameAsString();
            Expression memberValue = memberValuePair.getValue();
            if ("name".equals(memberName) && memberValue instanceof StringLiteralExpr ) {
                name = ((StringLiteralExpr) memberValue).asString();
            } else if ("jdbcType".equals(memberName) && memberValue instanceof FieldAccessExpr ) {
                FieldAccessExpr expr = (FieldAccessExpr) memberValue;
                jdbcType = JdbcType.valueOf(expr.getName().asString());
            } else if ("blob".equals(memberName)) {
                blob = ((BooleanLiteralExpr) memberValue).getValue();
            } else if ("id".equals(memberName)) {
                id = ((BooleanLiteralExpr) memberValue).getValue();
            } else if ("generatedValue".equals(memberName)) {
                generatedValue = ((BooleanLiteralExpr) memberValue).getValue();
            } else if ("typeHandler".equals(memberName) && memberValue instanceof ClassExpr ) {
                ClassExpr expr = (ClassExpr) memberValue;
                typeHandler = getMatchedType(importDeclarations, expr.getType());
            }
        }

        if (StringUtils.isEmpty(name) || "null".equals(name) || jdbcType == null) {
            return null;
        }
        String className = getClassByType(importDeclarations, field.getVariable(0).getType());
        if (StringUtils.isEmpty(className)) {
            throw new GenerateMyBatisExampleException("不支持的Java类型！ " + 
                    "field = " + field.getVariable(0).getNameAsString() + 
                    ", type = " + field.getVariable(0).getType() + 
                    ", FullyQualifiedName = " + modelDeclaration.getFullyQualifiedName().orElse(null));
        }
        column.setBlobColumn(blob);
        column.setIdentity(id && generatedValue);
        column.setActualColumnName(name);
        column.setAutoIncrement(generatedValue);
        column.setJavaProperty(field.getVariable(0).getNameAsString());
        column.setJdbcType(jdbcType.TYPE_CODE);
        column.setJdbcTypeName(jdbcType.name());
        column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(className));
        column.setTypeHandler(typeHandler);

        column.getProperties().put(AGGREGATES_NAME,
                parseAggregates(memberParis).stream()
                .map(AggregateFunction::name)
                .collect(Collectors.joining(",")));

        return Pair.of(column, id);
    }

    private static List<AggregateFunction> parseAggregates(NodeList<MemberValuePair> memberParis) {
        List<AggregateFunction> aggregateFunctions = new ArrayList<>();
        for (MemberValuePair memberValuePair : memberParis) {
            String memberName = memberValuePair.getNameAsString();
            Expression memberValue = memberValuePair.getValue();
            if (!AGGREGATES_NAME.equals(memberName)) {
                continue;
            }
            if (memberValue instanceof ArrayInitializerExpr) {
                NodeList<Expression> expressions = ((ArrayInitializerExpr) memberValue).getValues();
                for (Expression expression : expressions) {
                    aggregateFunctions.add(parseAggregate(expression));
                }
            } else {
                aggregateFunctions.add(parseAggregate(memberValue));
            }
            
        }
        return aggregateFunctions.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static AggregateFunction parseAggregate(Expression exp) {
        String value = null;
        if (exp instanceof FieldAccessExpr) {
            FieldAccessExpr expr = (FieldAccessExpr) exp;
            value = expr.getName().asString();
        }
        return AggregateFunction.parse(value);
    }

    private static String getClassByType(NodeList<ImportDeclaration> importDeclarations, Type type) throws ClassNotFoundException {
        Class<?> clazz = null;
        if (type.isPrimitiveType()) {
            clazz = Class.forName("java.lang." + type.asPrimitiveType().toBoxedType().asString());
        } else if (type.isArrayType()) {
            if ("byte[]".equals(type.asString())) {
                clazz = byte[].class;
            } else if ("Byte[]".equals(type.asString())) {
                clazz = Byte[].class;
            }
        } else if ("String".equals(type.asString())) {
            clazz = String.class;
        } else if ("Date".equals(type.asString()) || "java.util.Date".equals(type.asString())) {
            clazz = Date.class;
        } else if ("java.sql.Date".equals(type.asString())) {
            clazz = java.sql.Date.class;
        } else if ("LocalTime".equals(type.asString()) || "java.time.LocalTime".equals(type.asString())) {
            clazz = LocalTime.class;
        } else if ("LocalDate".equals(type.asString()) || "java.time.LocalDate".equals(type.asString())) {
            clazz = LocalDate.class;
        } else if ("LocalDateTime".equals(type.asString()) || "java.time.LocalDateTime".equals(type.asString())) {
            clazz = LocalDateTime.class;
        } else if ("Year".equals(type.asString()) || "java.time.Year".equals(type.asString())) {
            clazz = Year.class;
        } else if ("YearMonth".equals(type.asString()) || "java.time.YearMonth".equals(type.asString())) {
            clazz = YearMonth.class;
        } else if ("BigDecimal".equals(type.asString()) || "java.math.BigDecimal".equals(type.asString())) {
            clazz = BigDecimal.class;
        } else if ("BigInteger".equals(type.asString()) || "java.math.BigInteger".equals(type.asString())) {
            clazz = BigInteger.class;
        } else if (type.isClassOrInterfaceType()) {
            clazz = BOXED_TYPES.get(type.asString());
        }

        if (clazz != null) {
            if (clazz.isArray()) {
                return clazz.getSimpleName();
            } else {
                return clazz.getName();
            }
        } else {
            return getMatchedType(importDeclarations, type);
        }
    }

    public static String getMatchedType(NodeList<ImportDeclaration> importDeclarations, Type type) {
        if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType ctype = type.asClassOrInterfaceType();
            Optional<NodeList<Type>> typeArgs = ctype.getTypeArguments();
            if (typeArgs.isPresent()) {
                int size = typeArgs.get().size();
                if (size == 1) {
                    return String.format(Locale.ROOT,
                            "%s<%s>",
                            getMatchedType(importDeclarations, ctype.getNameWithScope()),
                            getMatchedType(importDeclarations, typeArgs.get().get(0).asString()));
                } else {
                    return null;
                }
            }
        }
        return getMatchedType(importDeclarations, type.asString());
    }

    private static String getMatchedType(NodeList<ImportDeclaration> importDeclarations, String typeStr) {
        ImportDeclaration matched = importDeclarations == null ? null : importDeclarations.stream()
                .filter(importDeclar -> typeStr.equals(new FullyQualifiedJavaType(importDeclar.getNameAsString()).getShortName()))
                .findFirst().orElse(null);
        return matched == null ? typeStr : matched.getNameAsString();
    }

}
