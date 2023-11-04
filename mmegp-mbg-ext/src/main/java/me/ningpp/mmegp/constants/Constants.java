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
package me.ningpp.mmegp.constants;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

public final class Constants {

    private Constants() {
    }

    public static final FullyQualifiedJavaType FQJT_DYNAMICSQL_UTIL = new FullyQualifiedJavaType("me.ningpp.mmegp.mybatis.dsql.DynamicSqlUtil");

    public static final FullyQualifiedJavaType FQJT_COLLECTION = new FullyQualifiedJavaType("java.util.Collection");

    public static final FullyQualifiedJavaType FQJT_ARRAYLIST = new FullyQualifiedJavaType("java.util.ArrayList");

    public static final FullyQualifiedJavaType FQJT_PROPERTY_MAPPING = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.PropertyMapping");

    public static final FullyQualifiedJavaType FQJT_ABS_COLUMN_MAPPING = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.AbstractColumnMapping");

    public static final FullyQualifiedJavaType FQJT_MULTI_INSERT_PROVIDER = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider");
}
