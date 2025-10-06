/*
 *    Copyright 2025 the original author or authors.
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
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import me.ningpp.mmegp.MetaInfoHandler;
import me.ningpp.mmegp.annotations.TenantId;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;

import java.util.function.Function;
import java.util.stream.Stream;

public class TenantIdMetaInfoHandler implements MetaInfoHandler {

    @Override
    public void handle(IntrospectedTable table, ClassOrInterfaceDeclaration typeDeclaration) {
        for (var f : typeDeclaration.getFields()) {
            handleTenantIdAnnotation(table, f, f.getVariables().get(0).getNameAsString());
        }
    }

    @Override
    public void handle(IntrospectedTable table, RecordDeclaration typeDeclaration) {
        for (var p : typeDeclaration.getParameters()) {
            handleTenantIdAnnotation(table, p, p.getNameAsString());
        }
    }

    private <N extends Node> void handleTenantIdAnnotation(IntrospectedTable table,
            NodeWithAnnotations<N> node,
            String name) {
        var hasTenantId = node.getAnnotationByClass(TenantId.class);
        if (hasTenantId.isPresent()) {
            IntrospectedColumn tenantColumn = Stream.of(
                table.getPrimaryKeyColumns().stream(),
                table.getBaseColumns().stream()
            )
                .flatMap(Function.identity())
                .filter(c -> name.equals(c.getJavaProperty()))
            .findFirst().orElse(null);
            if (tenantColumn == null) {
                throw new IllegalArgumentException("this is not a column, but with TenantId Annotation!");
            }
            tenantColumn.getProperties().setProperty(
                    TenantId.class.getName(),
                    "true"
            );
        }
    }

}
