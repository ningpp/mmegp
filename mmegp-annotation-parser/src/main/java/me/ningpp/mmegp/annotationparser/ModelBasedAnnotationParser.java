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
package me.ningpp.mmegp.annotationparser;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import me.ningpp.mmegp.annotationparser.handler.AnnotatedAnnotationValueHandler;
import me.ningpp.mmegp.annotationparser.handler.BooleanAnnotationValueHandler;
import me.ningpp.mmegp.annotationparser.handler.ByteAnnotationValueHandler;
import me.ningpp.mmegp.annotationparser.handler.CharAnnotationValueHandler;
import me.ningpp.mmegp.annotationparser.handler.ClassAnnotationValueHandler;
import me.ningpp.mmegp.annotationparser.handler.DoubleAnnotationValueHandler;
import me.ningpp.mmegp.annotationparser.handler.EnumAnnotationValueHandler;
import me.ningpp.mmegp.annotationparser.handler.FloatAnnotationValueHandler;
import me.ningpp.mmegp.annotationparser.handler.IntAnnotationValueHandler;
import me.ningpp.mmegp.annotationparser.handler.LongAnnotationValueHandler;
import me.ningpp.mmegp.annotationparser.handler.ShortAnnotationValueHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.ningpp.mmegp.annotationmodel.AnnotationModelGenerator.SUFFIX;

public class ModelBasedAnnotationParser {

    public static final char DOT = '.';

    public static <M, N extends Node> M parse(Class<? extends Annotation> annotationClass,
                                              Class<M> clazz,
                                              NodeWithAnnotations<N> node,
                                              NodeList<ImportDeclaration> importDeclars,
                                              String modelPackage) {
        Optional<AnnotationExpr> optionalColumnAnno = node.getAnnotationByClass(annotationClass);
        Map<String, String> importMap = importDeclars.stream()
                .filter(importDeclar -> !importDeclar.isAsterisk() && !importDeclar.isStatic())
                .collect(Collectors.toMap(ModelBasedAnnotationParser::simpleName,
                        NodeWithName::getNameAsString));
        return parse(annotationClass, clazz, optionalColumnAnno, importMap, modelPackage);
    }

    private static String simpleName(ImportDeclaration importDeclaration) {
        String name = importDeclaration.getNameAsString();
        int last = name.lastIndexOf(DOT);
        if (last == -1) {
            return name;
        } else {
            return name.substring(last+1);
        }
    }

    public static <M> M parse(Class<? extends Annotation> annotationClass,
                              Class<M> modelClass,Optional<AnnotationExpr> optionalColumnAnno,
                              Map<String, String> importMap,
                              String modelPackage) {
        if (!modelClass.isRecord()) {
            throw new IllegalArgumentException("only support java record!");
        }

        Constructor<?> constructor = modelClass.getConstructors()[0];
        if (constructor.getParameters().length != annotationClass.getDeclaredMethods().length) {
            throw new IllegalArgumentException("Model don't match Annotation, they have different fields.");
        }

        Map<String, MemberValuePair> memberValuePairs = getMemberValuePairs(optionalColumnAnno);
        Object[] args = initargs(annotationClass, memberValuePairs, importMap, modelPackage);
        try {
            return (M) constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Map<String, MemberValuePair> getMemberValuePairs(Optional<AnnotationExpr> optionalExpr) {
        if (optionalExpr.isPresent()) {
            AnnotationExpr annotationExpr = optionalExpr.get();
            NodeList<MemberValuePair> memberValuePairs;
            if (annotationExpr.isMarkerAnnotationExpr()) {
                memberValuePairs = new NodeList<>();
            } else if (annotationExpr.isSingleMemberAnnotationExpr()) {
                memberValuePairs = new NodeList<>(
                        new MemberValuePair("value", annotationExpr.asSingleMemberAnnotationExpr().getMemberValue())
                );
            } else {
                memberValuePairs = annotationExpr.asNormalAnnotationExpr().getPairs();
            }
            return memberValuePairs.stream().collect(
                    Collectors.toMap(MemberValuePair::getNameAsString, v -> v));
        } else {
            return Map.of();
        }
    }

    private static Object[] initargs(Class<? extends Annotation> annotationClass,
                                     Map<String, MemberValuePair> memberValuePairs,
                                     Map<String, String> importMap,
                                     String modelPackage) {
        List<Method> methods = getAnnotationMethods(annotationClass);
        int size = methods.size();
        Object[] args = new Object[size];
        int index = 0;
        for (Method method : methods) {
            MemberValuePair valuePair = memberValuePairs.get(method.getName());
            Object val = parseMethodValue(method, valuePair, importMap, modelPackage);
            args[index] = val;
            index++;
        }
        return args;
    }

    private static Class<?> getAnnotationModelClass(Method method, String modelPackage) {
        return getAnnotationModelClass(method.getReturnType(), modelPackage);
    }

    private static Class<?> getAnnotationModelClass(Class<?> aclass, String modelPackage) {
        Class<?> clazz = null;
        if (aclass.isArray()) {
            if (aclass.componentType().isAnnotation()) {
                clazz = aclass.componentType();
            }
        } else if (aclass.isAnnotation()) {
            clazz = aclass;
        }
        try {
            return Class.forName(modelPackage + DOT
                    + clazz.getSimpleName() + SUFFIX);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Map<Method, Object> ANNOTATION_METHOD_DEFAULT_VALUE = new ConcurrentHashMap<>();

    private static Object getDefaultValue(Method method, String modelPackage) {
        if (ANNOTATION_METHOD_DEFAULT_VALUE.containsKey(method)) {
            return ANNOTATION_METHOD_DEFAULT_VALUE.get(method);
        }
        Object defaultValue = method.getDefaultValue();
        if (defaultValue != null) {
            defaultValue = handleDefaultValue(method, defaultValue, modelPackage);
        }
        ANNOTATION_METHOD_DEFAULT_VALUE.put(method, defaultValue);
        return defaultValue;
    }

    private static Object handleDefaultValue(Method method, Object defaultValue, String modelPackage) {
        Object returnValue;
        if (method.getReturnType().isArray()) {
            if (method.getReturnType().componentType().isAnnotation()) {
                int length = Array.getLength(defaultValue);
                Class<?> annotationModeClass = getAnnotationModelClass(method, modelPackage);
                Object array = Array.newInstance(annotationModeClass, length);;
                for (int i = 0; i < length; i++) {
                    Object annotationValue = Array.get(defaultValue, i);
                    Array.set(array, i,
                            convertAnnotationToModel(annotationValue,
                                    method.getReturnType().componentType(),
                                    annotationModeClass,
                                    modelPackage));
                }
                returnValue = array;
            } else {
                returnValue = defaultValue;
            }
        } else if (method.getReturnType().isAnnotation()) {
            Class<?> annotationModeClass = getAnnotationModelClass(method, modelPackage);
            returnValue = convertAnnotationToModel(defaultValue,
                    method.getReturnType(),
                    annotationModeClass,
                    modelPackage);
        } else {
            returnValue = defaultValue;
        }
        return returnValue;
    }

    private static final Map<Class<?>, List<Method>> ANNOTATION_SORTED_METHODS = new ConcurrentHashMap<>();

    private static List<Method> getAnnotationMethods(Class<?> annotationClass) {
        return ANNOTATION_SORTED_METHODS.computeIfAbsent(annotationClass,
                (k) -> Stream.of(annotationClass.getDeclaredMethods())
                        .sorted(Comparator.comparing(Method::getName)).toList());
    }

    private static Object convertAnnotationToModel(Object annotationValue,
                                                   Class<?> annotationClass,
                                                   Class<?> annotationModeClass,
                                                   String modelPackage) {
        List<Method> methods = getAnnotationMethods(annotationClass);
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotationValue);
        int size = methods.size();
        Object[] args = new Object[size];
        int index = 0;
        for (Method method : methods) {
            Object val = convertAnnotationToModel(invocationHandler,
                    annotationValue,
                    method,
                    modelPackage);
            args[index] = val;
            index++;
        }
        Constructor<?> constructor = annotationModeClass.getConstructors()[0];
        if (constructor.getParameters().length != annotationClass.getDeclaredMethods().length) {
            throw new IllegalArgumentException("Model don't match Annotation, they have different fields.");
        }
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object inokeValue(InvocationHandler invocationHandler, Object annotationValue, Method method) {
        try {
            return invocationHandler.invoke(annotationValue, method, null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static Object convertAnnotationToModel(InvocationHandler invocationHandler,
                                                   Object annotationValue,
                                                   Method method,
                                                   String modelPackage) {

        Object val = null;
        if (method.getReturnType().isAnnotation()) {
            val = convertAnnotationToModel(
                    inokeValue(invocationHandler, annotationValue, method),
                    method.getReturnType(),
                    getAnnotationModelClass(method, modelPackage),
                    modelPackage
            );
        } else if (method.getReturnType().isArray()) {
            if (method.getReturnType().componentType().isAnnotation()) {
                Object invoked = inokeValue(invocationHandler, annotationValue, method);
                int length = Array.getLength(invoked);
                Class<?> annotationModeClass = getAnnotationModelClass(method, modelPackage);
                Object array = Array.newInstance(annotationModeClass, length);;
                for (int i = 0; i < length; i++) {
                    Object invokedIndexValue = Array.get(invoked, i);
                    Array.set(array, i,
                            convertAnnotationToModel(invokedIndexValue,
                                    method.getReturnType().componentType(),
                                    annotationModeClass,
                                    modelPackage));
                }
                val = array;
            }
        }

        if (val == null) {
            val = inokeValue(invocationHandler, annotationValue, method);
        }
        return val;
    }

    private static Object parseMethodValue(Method method, MemberValuePair valuePair, Map<String, String> importMap, String modelPackage) {
        Object val;
        if (valuePair == null) {
            Object defaultValue = getDefaultValue(method, modelPackage);
            if (defaultValue != null) {
                val = defaultValue;
            } else {
                throw new IllegalArgumentException("missing '" + method.getName() + "' value");
            }
        } else {
            val = parseValue(method, valuePair, importMap, modelPackage);
        }
        return val;
    }

    private static Object parseValue(Method method, MemberValuePair valuePair, Map<String, String> importMap, String modelPackage) {
        if (method.getReturnType().isArray()) {
            return parseArrayValue(method, valuePair, importMap, modelPackage);
        } else {
            return parseSingleValue(method.getReturnType(), valuePair.getValue(), importMap, modelPackage);
        }
    }

    private static Object parseArrayValue(Method method, MemberValuePair valuePair, Map<String, String> importMap, String modelPackage) {
        NodeList<Expression> exprs;
        if (valuePair.getValue().isArrayInitializerExpr()) {
            exprs = valuePair.getValue().asArrayInitializerExpr().getValues();
        } else {
            exprs = new NodeList<>(valuePair.getValue());
        }
        Class<?> componentType = method.getReturnType().componentType();
        Class<?> arrayComponentType;
        if (componentType.isAnnotation()) {
            arrayComponentType = getAnnotationModelClass(componentType, modelPackage);
        } else {
            arrayComponentType = componentType;
        }
        int length = exprs.size();
        Object array = Array.newInstance(arrayComponentType, length);;
        for (int i = 0; i < length; i++) {
            Object value = parseSingleValue(componentType, exprs.get(i), importMap, modelPackage);
            Array.set(array, i, value);
        }
        return array;
    }

    private static Object parseSingleValue(Class<?> returnType, Expression expr, Map<String, String> importMap, String modelPackage) {
        Object val = null;
        for (AnnotationValueHandler handler : HANDLERS) {
            if (handler.support(returnType)) {
                val = handler.handle(returnType, expr, importMap, modelPackage);
                break;
            }
        }
        if (val == null) {
            throw new IllegalArgumentException("annotation value is null! Expression = " + expr);
        }
        return val;
    }

    private static final List<AnnotationValueHandler> HANDLERS;
    static {
        List<AnnotationValueHandler> handlers = new ArrayList<>();
        handlers.add(new BooleanAnnotationValueHandler());
        handlers.add(new ByteAnnotationValueHandler());
        handlers.add(new CharAnnotationValueHandler());
        handlers.add(new ShortAnnotationValueHandler());
        handlers.add(new IntAnnotationValueHandler());
        handlers.add(new LongAnnotationValueHandler());
        handlers.add(new FloatAnnotationValueHandler());
        handlers.add(new DoubleAnnotationValueHandler());
        handlers.add(new ClassAnnotationValueHandler());
        handlers.add(new EnumAnnotationValueHandler());
        handlers.add(new AnnotatedAnnotationValueHandler());
        HANDLERS = List.copyOf(handlers);
    }

    public static final Map<Primitive, Class<?>> PRIMITIVE_TYPES = Map.of(
            Primitive.BOOLEAN, boolean.class,
            Primitive.CHAR, char.class,
            Primitive.BYTE, byte.class,
            Primitive.SHORT, short.class,
            Primitive.INT, int.class,
            Primitive.LONG, long.class,
            Primitive.FLOAT, float.class,
            Primitive.DOUBLE, double.class
    );

}
