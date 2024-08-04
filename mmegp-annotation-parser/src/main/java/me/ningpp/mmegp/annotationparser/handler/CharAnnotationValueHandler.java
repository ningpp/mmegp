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

import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import me.ningpp.mmegp.annotationparser.AnnotationValueHandler;

import java.util.Map;

public class CharAnnotationValueHandler implements AnnotationValueHandler {

    @Override
    public boolean support(Class<?> returnType) {
        return returnType == char.class;
    }

    @Override
    public Object handle(Class<?> returnType, Expression expr, Map<String, String> importMap, String modelPackage) {
        Character charValue = null;
        if (expr.isCastExpr()) {
            CastExpr castExpr = expr.asCastExpr();
            if (castExpr.getType().isPrimitiveType()
                    && castExpr.getType().asPrimitiveType().getType() == Primitive.CHAR
                    && castExpr.getExpression().isIntegerLiteralExpr()) {
                charValue = (char) castExpr.getExpression()
                        .asIntegerLiteralExpr().asNumber().intValue();
            }
        } else if (expr.isCharLiteralExpr()) {
            charValue = expr.asCharLiteralExpr().asChar();
        } else if (expr.isIntegerLiteralExpr()) {
            charValue = (char) expr.asIntegerLiteralExpr().asNumber().intValue();
        }
        return charValue;
    }
}
