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
package me.ningpp.mmegp.meta.handler;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import me.ningpp.mmegp.MetaInfoHandler;
import org.mybatis.generator.api.IntrospectedTable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractAnnotationMetaInfoHandler implements MetaInfoHandler {

    protected abstract Class<? extends Annotation> getAnnotationClass();

    protected abstract void handleTypeAnnotation(IntrospectedTable table,
                                                 Map<String, List<MemberValuePair>> memberMap);

    protected <N extends Node> void handleTypeAnnotation(IntrospectedTable table, NodeWithAnnotations<N> node) {
        Optional<AnnotationExpr> optionalColumnAnno = node.getAnnotationByClass(getAnnotationClass());
        if (optionalColumnAnno.isEmpty()
                || !optionalColumnAnno.get().isNormalAnnotationExpr()) {
            return;
        }

        handleTypeAnnotation(
            table,
            optionalColumnAnno.get()
                .asNormalAnnotationExpr()
                    .getPairs().stream().collect(
                            Collectors.groupingBy(MemberValuePair::getNameAsString))
        );
    }

    @Override
    public void handle(IntrospectedTable table, ClassOrInterfaceDeclaration coiDeclaration) {
        handleTypeAnnotation(table, coiDeclaration);
    }

    @Override
    public void handle(IntrospectedTable table, RecordDeclaration recordDeclaration) {
        handleTypeAnnotation(table, recordDeclaration);
    }

}
