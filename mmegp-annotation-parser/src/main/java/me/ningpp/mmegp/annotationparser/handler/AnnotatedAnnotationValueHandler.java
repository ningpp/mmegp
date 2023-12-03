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
import me.ningpp.mmegp.annotationparser.AnnotationValueHandler;
import me.ningpp.mmegp.annotationparser.ModelBasedAnnotationParser;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;

import static me.ningpp.mmegp.annotationmodel.AnnotationModelGenerator.SUFFIX;
import static me.ningpp.mmegp.annotationparser.ModelBasedAnnotationParser.DOT;

public class AnnotatedAnnotationValueHandler implements AnnotationValueHandler {

    @Override
    public boolean support(Class<?> returnType) {
        return Annotation.class.isAssignableFrom(returnType);
    }

    @Override
    public Object handle(Class<?> returnType, Expression expr, Map<String, String> importMap, String modelPackage) {
        if (expr.isAnnotationExpr()) {
            try {
                Class<Annotation>  annotationType = (Class<Annotation>) returnType;
                Class<?> annotationModelType = Class.forName(
                        modelPackage + DOT + returnType.getSimpleName() + SUFFIX);
                return ModelBasedAnnotationParser.parse(annotationType,
                                annotationModelType,
                                Optional.ofNullable(expr.asAnnotationExpr()),
                                importMap,
                                modelPackage);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }
}
