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

import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.mybatis.generator.api.IntrospectedTable;

public interface MetaInfoHandler {

    default void handle(IntrospectedTable table, TypeDeclaration<?> typeDeclaration) {
        if (table == null || typeDeclaration == null) {
            return;
        }
        if (typeDeclaration.isClassOrInterfaceDeclaration()) {
            handle(table, typeDeclaration.asClassOrInterfaceDeclaration());
        } else if (typeDeclaration.isRecordDeclaration()) {
            handle(table, typeDeclaration.asRecordDeclaration());
        } else if (typeDeclaration.isEnumDeclaration()) {
            handle(table, typeDeclaration.asEnumDeclaration());
        } else if (typeDeclaration.isAnnotationDeclaration()) {
            handle(table, typeDeclaration.asAnnotationDeclaration());
        } else {
            throw new GenerateMyBatisExampleException("unknown TypeDeclaration: " + typeDeclaration.getClass().getName());
        }
    }

    default void handle(IntrospectedTable table, ClassOrInterfaceDeclaration typeDeclaration) {
    }

    default void handle(IntrospectedTable table, RecordDeclaration typeDeclaration) {
    }

    default void handle(IntrospectedTable table, EnumDeclaration typeDeclaration) {
    }

    default void handle(IntrospectedTable table, AnnotationDeclaration typeDeclaration) {
    }

}
