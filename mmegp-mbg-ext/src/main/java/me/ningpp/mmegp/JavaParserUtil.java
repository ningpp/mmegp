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
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.ObjectFactory;

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
import java.util.Optional;
import java.util.Set;

public final class JavaParserUtil {
    private JavaParserUtil() {
    }

    private static final String TABLE_NAMING_STRATEGY_INSTANCE_KEY = "tableNamingStrategyInstance";
    private static final String TABLE_NAMING_STRATEGY_KEY = "tableNamingStrategy";

    private static final String COLUMN_NAMING_STRATEGY_INSTANCE_KEY = "columnNamingStrategyInstance";
    private static final String COLUMN_NAMING_STRATEGY_KEY = "columnNamingStrategy";

    private static final Map<String, JdbcType> PREDEFINED_TYPES;
    private static final Map<String, JdbcType> JDBC_TYPES = new HashMap<>();
    public static final String COUNT_GROUP_BY_COLUMNS_NAME = "countGroupByColumns";
    public static final String AGGREGATES_NAME = "aggregates";

    static {
        for (JdbcType jdbcType : JdbcType.values()) {
            JDBC_TYPES.put(jdbcType.name(), jdbcType);
        }
    }

    static {
        Map<String, JdbcType> predefinedTypes = new HashMap<>();
        predefinedTypes.put(boolean.class.getName(), JdbcType.BOOLEAN);
        predefinedTypes.put(Boolean.class.getName(), JdbcType.BOOLEAN);
        predefinedTypes.put(char.class.getName(), JdbcType.CHAR);
        predefinedTypes.put(Character.class.getName(), JdbcType.CHAR);
        predefinedTypes.put(byte.class.getName(), JdbcType.TINYINT);
        predefinedTypes.put(Byte.class.getName(), JdbcType.TINYINT);
        predefinedTypes.put(short.class.getName(), JdbcType.SMALLINT);
        predefinedTypes.put(Short.class.getName(), JdbcType.SMALLINT);
        predefinedTypes.put(int.class.getName(), JdbcType.INTEGER);
        predefinedTypes.put(Integer.class.getName(), JdbcType.INTEGER);
        predefinedTypes.put(long.class.getName(), JdbcType.BIGINT);
        predefinedTypes.put(Long.class.getName(), JdbcType.BIGINT);
        predefinedTypes.put(float.class.getName(), JdbcType.FLOAT);
        predefinedTypes.put(Float.class.getName(), JdbcType.FLOAT);
        predefinedTypes.put(double.class.getName(), JdbcType.DOUBLE);
        predefinedTypes.put(Double.class.getName(), JdbcType.DOUBLE);

        predefinedTypes.put(String.class.getName(), JdbcType.VARCHAR);

        predefinedTypes.put(java.math.BigDecimal.class.getName(), JdbcType.DECIMAL);
        predefinedTypes.put(java.math.BigInteger.class.getName(), JdbcType.BIGINT);

        predefinedTypes.put(java.sql.Date.class.getName(), JdbcType.DATE);
        predefinedTypes.put(java.util.Date.class.getName(), JdbcType.TIMESTAMP);
        predefinedTypes.put(java.sql.Timestamp.class.getName(), JdbcType.TIMESTAMP);

        predefinedTypes.put(java.time.LocalDate.class.getName(), JdbcType.DATE);
        predefinedTypes.put(java.time.LocalDateTime.class.getName(), JdbcType.TIMESTAMP);
        predefinedTypes.put(java.time.LocalTime.class.getName(), JdbcType.TIME);
        predefinedTypes.put(java.time.Year.class.getName(), JdbcType.INTEGER);

        predefinedTypes.put(byte[].class.getSimpleName(), JdbcType.LONGVARBINARY);
        predefinedTypes.put(Byte[].class.getSimpleName(), JdbcType.LONGVARBINARY);

        PREDEFINED_TYPES = Map.copyOf(predefinedTypes);
    }

    public static JavaParser newParser() {
        ParserConfiguration jpc = new ParserConfiguration();
        jpc.setLanguageLevel(LanguageLevel.JAVA_17);
        jpc.setCharacterEncoding(StandardCharsets.UTF_8);
        return new JavaParser(jpc);
    }

    public static String parseTableName(String name, String entityName, Context context) {
        return parseNamingByStrategy(name, entityName,
                TABLE_NAMING_STRATEGY_INSTANCE_KEY, TABLE_NAMING_STRATEGY_KEY, context);
    }

    public static String parseTableName(Map<String, List<MemberValuePair>> annotationMembers,
            String entityName, Context context) {
        return parseNamingByStrategy(annotationMembers, "table", entityName,
                TABLE_NAMING_STRATEGY_INSTANCE_KEY, TABLE_NAMING_STRATEGY_KEY, context);
    }

    public static String parseColumnName(String name, String javaProperty, Context context) {
        return parseNamingByStrategy(name, javaProperty,
                COLUMN_NAMING_STRATEGY_INSTANCE_KEY, COLUMN_NAMING_STRATEGY_KEY, context);
    }

    public static String parseColumnName(Map<String, List<MemberValuePair>> annotationMembers,
            String javaProperty, Context context) {
        return parseNamingByStrategy(annotationMembers, "name", javaProperty,
                COLUMN_NAMING_STRATEGY_INSTANCE_KEY, COLUMN_NAMING_STRATEGY_KEY, context);
    }

    private static String parseNamingByStrategy(Map<String, List<MemberValuePair>> annotationMembers,
            String annotationMethod, String srcName,
            String instanceKey, String propertyKey,
            Context context) {
        return parseNamingByStrategy(parseString(annotationMembers, annotationMethod, ""),
                srcName, instanceKey, propertyKey, context);
    }

    private static String parseNamingByStrategy(String name, String srcName,
            String instanceKey, String propertyKey, Context context) {
        if (StringUtils.isEmpty(name)) {
            NamingStrategy strategy = (NamingStrategy) context.getProperties().get(instanceKey);
            if (strategy == null) {
                String strategyClassName = (String) context.getProperties()
                        .getOrDefault(propertyKey, SnakeCaseStrategy.class.getName());
                strategy = (NamingStrategy) ObjectFactory.createExternalObject(strategyClassName);
                context.getProperties().put(instanceKey, strategy);
            }
            name = strategy.translate(srcName);
        }
        return name;
    }

    public static String parseTypeHandler(Map<String, List<MemberValuePair>> annotationMembers,
                                          Map<String, String> declarMappings) {
        return parse(annotationMembers, "typeHandler")
                .filter(Expression::isClassExpr)
                .map(mv -> getMatchedType(declarMappings, mv.asClassExpr().getType()))
                .orElse(null);
    }

    public static JdbcType parseJdbcType(String typeClassName,
                                         Map<String, List<MemberValuePair>> annotationMembers) {
        return parseJdbcType(
                typeClassName,
                parseEnumValue("jdbcType", JDBC_TYPES, annotationMembers, JdbcType.UNDEFINED)
        );
    }

    public static JdbcType parseJdbcType(String typeClassName, JdbcType declarJdbcType) {
        if (StringUtils.isEmpty(typeClassName)) {
            return null;
        }
        if (declarJdbcType == null || declarJdbcType == JdbcType.UNDEFINED) {
            return PREDEFINED_TYPES.get(typeClassName);
        } else {
            return declarJdbcType;
        }
    }

    public static <T> T parseEnumValue(String name,
                                       Map<String, T> valueMap,
                                       Map<String, List<MemberValuePair>> annotationMembers,
                                       T defaultValue) {
        return parse(annotationMembers, name)
                .flatMap(memberValue -> Optional.ofNullable(valueMap.get(getEnumTypeName(memberValue))))
                .orElse(defaultValue);
    }

    private static String getEnumTypeName(Expression memberValue) {
        String typeName = null;
        if (memberValue.isFieldAccessExpr()) {
            typeName = memberValue.asFieldAccessExpr().getNameAsString();
        } else if (memberValue.isNameExpr()) {
            typeName = memberValue.asNameExpr().getNameAsString();
        }
        return typeName;
    }

    public static List<Expression> parseArray(Map<String, List<MemberValuePair>> annotationMembers, String name) {
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

    public static List<String> parseArrayString(Map<String, List<MemberValuePair>> annotationMembers, String name) {
        return parseArray(annotationMembers, name)
                .stream().filter(Expression::isStringLiteralExpr)
                .map(expr -> expr.asStringLiteralExpr().asString())
                .toList();
    }

    public static String parseString(Map<String, List<MemberValuePair>> annotationMembers, String name,
            String defaultValue) {
        return parse(annotationMembers, name)
                .filter(Expression::isStringLiteralExpr)
                .map(mv -> mv.asStringLiteralExpr().asString())
                .orElse(defaultValue);
    }

    public static boolean parseBoolean(Map<String, List<MemberValuePair>> annotationMembers, String name,
            boolean defaultValue) {
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

    public static String getClassByType(Map<String, String> declarMappings, Type type) {
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

    public static String getMatchedType(Map<String, String> declarMappings, Type type) {
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

    private static String getMatchedType(Map<String, String> declarMappings, String typeStr) {
        return declarMappings.getOrDefault(typeStr, typeStr);
    }

}
