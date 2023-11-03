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
package me.ningpp.mmegp.mybatis.dsql;

import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.insert.InsertModel;
import org.mybatis.dynamic.sql.insert.MultiRowInsertModel;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.util.AbstractColumnMapping;

import java.util.Collection;
import java.util.List;

public final class DynamicSqlUtil {

    private DynamicSqlUtil() {
    }

    public static <T> InsertStatementProvider<T> renderInsert(T row, SqlTable table, List<AbstractColumnMapping> columnMappings) {
        return renderInsert(InsertModel.withRow(row).withTable(table).withColumnMappings(columnMappings).build());
    }

    public static <T> InsertStatementProvider<T> renderInsert(InsertModel<T> insertModel) {
        return insertModel.render(RenderingStrategies.MYBATIS3);
    }

    public static <T> MultiRowInsertStatementProvider<T> renderMultiInsert(Collection<T> records, SqlTable table, List<AbstractColumnMapping> columnMappings) {
        return renderMultiInsert(MultiRowInsertModel.withRecords(records).withTable(table).withColumnMappings(columnMappings).build());
    }

    public static <T> MultiRowInsertStatementProvider<T> renderMultiInsert(MultiRowInsertModel<T> multiRowInsertModel) {
        return multiRowInsertModel.render(RenderingStrategies.MYBATIS3);
    }

}
