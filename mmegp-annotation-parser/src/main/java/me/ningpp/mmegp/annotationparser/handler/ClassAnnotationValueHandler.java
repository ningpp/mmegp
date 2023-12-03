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
package me.ningpp.mmegp.annotationparser.handler;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.Type;
import me.ningpp.mmegp.annotationparser.AnnotationValueHandler;

import java.util.Map;

import static me.ningpp.mmegp.annotationparser.ModelBasedAnnotationParser.DOT;
import static me.ningpp.mmegp.annotationparser.ModelBasedAnnotationParser.PRIMITIVE_TYPES;

public class ClassAnnotationValueHandler implements AnnotationValueHandler {

    @Override
    public boolean support(Class<?> returnType) {
        return Class.class.isAssignableFrom(returnType);
    }

    @Override
    public Object handle(Class<?> returnType, Expression expr, Map<String, String> importMap, String modelPackage) {
        Class<?> clazz = null;
        if (expr.isClassExpr()) {
            Type type = expr.asClassExpr().getType();
            try {
                if (type.isClassOrInterfaceType()) {
                    clazz = getClass(importMap, type);
                } else if (type.isPrimitiveType()) {
                    clazz = PRIMITIVE_TYPES.get(type.asPrimitiveType().getType());
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return clazz;
    }

    private static Class<?> getClass(Map<String, String> importMap, Type type) throws ClassNotFoundException {
        Class<?> clazz = null;
        if (JavaLangClasses.SIMPLE_NAMES.contains(type.asClassOrInterfaceType().getNameAsString())) {
            clazz = Class.forName("java.lang." + type.asClassOrInterfaceType().getNameAsString());
        } else if (JavaLangClasses.NAMES.contains(type.asClassOrInterfaceType().getNameWithScope())) {
            clazz = Class.forName(type.asClassOrInterfaceType().getNameWithScope());
        } else {
            String name = type.asClassOrInterfaceType().getNameWithScope();
            int first = name.indexOf(DOT);
            if (first == -1) {
                String className = importMap.get(name);
                if (className != null) {
                    clazz = Class.forName(className);
                }
            } else {
                String rootClassName = importMap.get(name.substring(0, first));
                if (rootClassName != null) {
                    clazz = Class.forName(rootClassName + name.substring(first));
                } else {
                    clazz = Class.forName(name);
                }
            }
        }
        return clazz;
    }

}
