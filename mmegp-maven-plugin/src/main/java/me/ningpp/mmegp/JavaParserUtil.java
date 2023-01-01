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
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.ObjectFactory;

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

    public static Pair<IntrospectedColumn, Boolean> buildColumn(ClassOrInterfaceDeclaration modelDeclaration, FieldDeclaration field, Context context) throws ClassNotFoundException {
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
        IntrospectedColumn column = ObjectFactory.createIntrospectedColumn(context);
        
        String name = null;
        JdbcType jdbcType = null;
        boolean blob = false;
        boolean id = false;
        boolean generatedValue = false;

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
            }
        }

        if (StringUtils.isEmpty(name) || "null".equals(name) || jdbcType == null) {
            return null;
        }
        Class<?> clazz = getClassByType(field.getVariable(0).getType());
        if (clazz == null) {
            throw new GenerateMyBatisExampleException("不支持的Java类型！ " + 
                    "field = " + field.getVariable(0).getNameAsString() + 
                    ", type = " + field.getVariable(0).getType() + 
                    ", FullyQualifiedName = " + modelDeclaration.getFullyQualifiedName().get());
        }
        column.setIdentity(id && generatedValue);
        column.setActualColumnName(name);
        column.setAutoIncrement(generatedValue);
        column.setJavaProperty(field.getVariable(0).getNameAsString());
        column.setJdbcType(jdbcType.TYPE_CODE);
        column.setJdbcTypeName(jdbcType.name());
        if (clazz == byte[].class) {
            column.setFullyQualifiedJavaType(new FullyQualifiedJavaType("byte[]"));
        } else if (clazz == Byte[].class) {
            column.setFullyQualifiedJavaType(new FullyQualifiedJavaType("Byte[]"));
        } else {
            column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(clazz.getName()));
        }

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

    private static Class<?> getClassByType(Type type) throws ClassNotFoundException {
        if (type.isPrimitiveType()) {
            return Class.forName("java.lang." + type.asPrimitiveType().toBoxedType().asString());
        } else if (type.isArrayType()) {
            if ("byte[]".equals(type.asString())) {
                return byte[].class;
            } else if ("Byte[]".equals(type.asString())) {
                return Byte[].class;
            } else {
                return null;
            }
        } else if ("String".equals(type.asString())) {
            return String.class;
        } else if ("Date".equals(type.asString()) || "java.util.Date".equals(type.asString())) {
            return Date.class;
        } else if ("java.sql.Date".equals(type.asString())) {
            return java.sql.Date.class;
        } else if ("LocalTime".equals(type.asString()) || "java.time.LocalTime".equals(type.asString())) {
            return LocalTime.class;
        } else if ("LocalDate".equals(type.asString()) || "java.time.LocalDate".equals(type.asString())) {
            return LocalDate.class;
        } else if ("LocalDateTime".equals(type.asString()) || "java.time.LocalDateTime".equals(type.asString())) {
            return LocalDateTime.class;
        } else if ("Year".equals(type.asString()) || "java.time.Year".equals(type.asString())) {
            return Year.class;
        } else if ("YearMonth".equals(type.asString()) || "java.time.YearMonth".equals(type.asString())) {
            return YearMonth.class;
        } else if ("BigDecimal".equals(type.asString()) || "java.math.BigDecimal".equals(type.asString())) {
            return BigDecimal.class;
        } else if ("BigInteger".equals(type.asString()) || "java.math.BigInteger".equals(type.asString())) {
            return BigInteger.class;
        } else if (type.isClassOrInterfaceType()) {
            return BOXED_TYPES.get(type.asString());
        }
        return null;
    }

}
