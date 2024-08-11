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

import com.github.javaparser.ast.expr.MemberValuePair;
import me.ningpp.mmegp.JavaParserUtil;
import me.ningpp.mmegp.annotations.SoftDelete;
import me.ningpp.mmegp.enums.SoftDeleteStrategy;
import me.ningpp.mmegp.meta.model.SoftDeleteModel;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SoftDeleteMetaInfoHandler extends AbstractAnnotationMetaInfoHandler {

    private static final Map<String, SoftDeleteStrategy> STRATEGIES = Arrays.stream(SoftDeleteStrategy.values())
            .collect(Collectors.toMap(SoftDeleteStrategy::name, v -> v));

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return SoftDelete.class;
    }

    @Override
    protected void handleTypeAnnotation(IntrospectedTable table, Map<String, List<MemberValuePair>> memberMap) {
        String columnName = JavaParserUtil.parseString(memberMap, "column", null);
        Optional<IntrospectedColumn> optionalColumn = table.getColumn(columnName);
        if (optionalColumn.isEmpty()) {
            throw new IllegalArgumentException("invalid column name for soft delete, column not found!"
                + " table name = " + table.getTableConfiguration().getTableName()
                + " soft delete column = " + columnName);
        }

        SoftDeleteStrategy strategy = JavaParserUtil
                .parseEnumValue("strategy", STRATEGIES, memberMap, SoftDeleteStrategy.FIXED_VALUE);

        String notDeletedValue = JavaParserUtil.parseString(memberMap, "notDeletedValue", "0");
        String deletedValue = JavaParserUtil.parseString(memberMap, "deletedValue", "1");

        table.setAttribute(SoftDeleteModel.class.getName(),
                new SoftDeleteModel(optionalColumn.get(), strategy, notDeletedValue, deletedValue));
    }

}
