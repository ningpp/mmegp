package me.ningpp.mmegp.mybatis.dsql.pagination;

import me.ningpp.mmegp.mybatis.dsql.DynamicSqlUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginationSelectRendererTest {

    @Test
    void testRender2() {
        SqlTable table = SqlTable.of("article");
        SelectDSL<SelectModel> selectDsl = SqlBuilder.select(SqlColumn.of("*", table))
                .from(table)
                .where().and(SqlColumn.of("category", table), SqlBuilder.isEqualTo("tech"))
                        .and(SqlColumn.of("author", table), SqlBuilder.isIn("zhangsan", "lisi"))
                .orderBy(SqlBuilder.sortColumn("create_time").descending());
        SelectStatementProvider provider = DynamicSqlUtil.renderSelect(selectDsl,
                LimitOffset.of(3L, 7L),
                new MySqlPaginationModelRenderer());

        String expectedSql = "select * from article " +
                "where category = #{parameters.p1} " +
                "and author in (#{parameters.p2},#{parameters.p3}) " +
                "order by create_time DESC " +
                "LIMIT #{parameters.p5}, #{parameters.p4}";
        assertEquals(expectedSql, provider.getSelectStatement());
        assertEquals(5, provider.getParameters().size());
        assertEquals("tech", provider.getParameters().get("p1"));
        assertEquals("zhangsan", provider.getParameters().get("p2"));
        assertEquals("lisi", provider.getParameters().get("p3"));
        assertEquals(3L, provider.getParameters().get("p4"));
        assertEquals(7L, provider.getParameters().get("p5"));
    }

    @ParameterizedTest
    @MethodSource("generateParamAndResults")
    void testRender(ParamAndResult par) {
        PaginationSelectRenderer selectRenderer = new PaginationSelectRenderer(
                buildSelectModel(),
                LimitOffset.of(par.getLimit(), par.getOffset()),
                par.getRender(),
                RenderingStrategies.MYBATIS3, new AtomicInteger(par.getInitialSequenceValue()), null
        );
        SelectStatementProvider stmtProvider = selectRenderer.render();
        assertEquals(par.getExpectedSql(), stmtProvider.getSelectStatement());
        assertMapEquals(par.getExpectedSqlParams(), stmtProvider.getParameters());
    }

    private static final String SQL_NO_PAGING = "select * from article order by create_time DESC";

    public static List<ParamAndResult> generateParamAndResults() {
        List<ParamAndResult> pars = new ArrayList<>();
        int[] sequenceValues = { 1, new SecureRandom().nextInt(999) };
        for (int sequenceValue : sequenceValues) {
            pars.addAll(paramAndResults4OffsetFetch(sequenceValue));
            pars.addAll(paramAndResults4LimitOffset(sequenceValue));
            pars.addAll(paramAndResults4MySql(sequenceValue));
            pars.addAll(paramAndResults4SqlServer(sequenceValue));
        }
        return pars;
    }

    public static List<ParamAndResult> paramAndResults4OffsetFetch(int initialSequenceValue) {
        PaginationModelRenderer render =  OffsetFetchPaginationModelRendererProvider.RENDERER;
        ParamAndResult par1 = ParamAndResult.of(initialSequenceValue, render, null, null, SQL_NO_PAGING, Map.of());
        ParamAndResult par2 = ParamAndResult.of(initialSequenceValue, render, 3L, null,
                SQL_NO_PAGING + " FETCH FIRST #{parameters.p" + initialSequenceValue + "} ROWS ONLY", Map.of("p" + initialSequenceValue, 3L));
        ParamAndResult par3 = ParamAndResult.of(initialSequenceValue, render, null, 7L, SQL_NO_PAGING, Map.of());
        ParamAndResult par4 = ParamAndResult.of(initialSequenceValue, render, 3L, 7L,
                SQL_NO_PAGING + " OFFSET #{parameters.p" + (initialSequenceValue+1) + "} ROWS FETCH NEXT #{parameters.p" + initialSequenceValue + "} ROWS ONLY",
                Map.of("p" + (initialSequenceValue+1), 7L, "p" + initialSequenceValue, 3L));
        return List.of(par1, par2, par3, par4);
    }

    public static List<ParamAndResult> paramAndResults4LimitOffset(int initialSequenceValue) {
        PaginationModelRenderer render =  LimitOffsetPaginationModelRendererProvider.RENDERER;
        ParamAndResult par1 = ParamAndResult.of(initialSequenceValue, render, null, null, SQL_NO_PAGING, Map.of());
        ParamAndResult par2 = ParamAndResult.of(initialSequenceValue, render, 3L, null,
                SQL_NO_PAGING + " LIMIT #{parameters.p" + initialSequenceValue + "}", Map.of("p" + initialSequenceValue, 3L));
        ParamAndResult par3 = ParamAndResult.of(initialSequenceValue, render, null, 7L, SQL_NO_PAGING, Map.of());
        ParamAndResult par4 = ParamAndResult.of(initialSequenceValue, render, 3L, 7L,
                SQL_NO_PAGING + " LIMIT #{parameters.p" + initialSequenceValue + "} OFFSET #{parameters.p" + (initialSequenceValue+1) + "}",
                Map.of("p" + (initialSequenceValue+1), 7L, "p" + initialSequenceValue, 3L));
        return List.of(par1, par2, par3, par4);
    }

    public static List<ParamAndResult> paramAndResults4MySql(int initialSequenceValue) {
        PaginationModelRenderer render =  MySqlPaginationModelRendererProvider.RENDERER;
        ParamAndResult par1 = ParamAndResult.of(initialSequenceValue, render, null, null, SQL_NO_PAGING, Map.of());
        ParamAndResult par2 = ParamAndResult.of(initialSequenceValue, render, 3L, null,
                SQL_NO_PAGING + " LIMIT #{parameters.p" + initialSequenceValue + "}", Map.of("p" + initialSequenceValue, 3L));
        ParamAndResult par3 = ParamAndResult.of(initialSequenceValue, render, null, 7L, SQL_NO_PAGING, Map.of());
        ParamAndResult par4 = ParamAndResult.of(initialSequenceValue, render, 3L, 7L,
                SQL_NO_PAGING + " LIMIT #{parameters.p" + (initialSequenceValue+1) + "}, #{parameters.p" + initialSequenceValue + "}",
                Map.of("p" + (initialSequenceValue+1), 7L, "p" + initialSequenceValue, 3L));
        return List.of(par1, par2, par3, par4);
    }

    public static List<ParamAndResult> paramAndResults4SqlServer(int initialSequenceValue) {
        PaginationModelRenderer render =  SqlServerPaginationModelRendererProvider.RENDERER;
        ParamAndResult par1 = ParamAndResult.of(initialSequenceValue, render, null, null, SQL_NO_PAGING, Map.of());
        ParamAndResult par2 = ParamAndResult.of(initialSequenceValue, render, 3L, null,
                SQL_NO_PAGING + " OFFSET 0 ROWS FETCH NEXT #{parameters.p" + initialSequenceValue + "} ROWS ONLY", Map.of("p" + initialSequenceValue, 3L));
        ParamAndResult par3 = ParamAndResult.of(initialSequenceValue, render, null, 7L, SQL_NO_PAGING, Map.of());
        ParamAndResult par4 = ParamAndResult.of(initialSequenceValue, render, 3L, 7L,
                SQL_NO_PAGING + " OFFSET #{parameters.p" + (initialSequenceValue+1) + "} ROWS FETCH NEXT #{parameters.p" + initialSequenceValue + "} ROWS ONLY",
                Map.of("p" + (initialSequenceValue+1), 7L, "p" + initialSequenceValue, 3L));
        return List.of(par1, par2, par3, par4);
    }

    private SelectModel buildSelectModel() {
        SqlTable table = SqlTable.of("article");
        SelectDSL<SelectModel> dsl = SqlBuilder.select(SqlColumn.of("*", table))
                .from(table)
                .orderBy(SqlBuilder.sortColumn("create_time").descending());
        return dsl.build();
    }

    private static class ParamAndResult {
        private final int initialSequenceValue;
        private final PaginationModelRenderer render;
        private final Long limit;
        private final Long offset;
        private final String expectedSql;
        private final Map<String, Object> expectedSqlParams;

        private ParamAndResult(int initialSequenceValue, PaginationModelRenderer render,
                               Long limit, Long offset, String expectedSql, Map<String, Object> expectedSqlParams) {
            this.initialSequenceValue = initialSequenceValue;
            this.render = render;
            this.limit = limit;
            this.offset = offset;
            this.expectedSql = expectedSql;
            this.expectedSqlParams = expectedSqlParams;
        }

        public static ParamAndResult of(int initialSequenceValue, PaginationModelRenderer render,
                                        Long limit, Long offset, String expectedSql, Map<String, Object> expectedSqlParams) {
            return new ParamAndResult(initialSequenceValue, render, limit, offset, expectedSql, expectedSqlParams);
        }

        public int getInitialSequenceValue() {
            return initialSequenceValue;
        }

        public PaginationModelRenderer getRender() {
            return render;
        }

        public Long getLimit() {
            return limit;
        }

        public Long getOffset() {
            return offset;
        }

        public String getExpectedSql() {
            return expectedSql;
        }

        public Map<String, Object> getExpectedSqlParams() {
            return expectedSqlParams;
        }
    }

    private static <K, V> void assertMapEquals(Map<K, V> expected, Map<K, V> actual) {
        assertEquals(expected.size(), actual.size());
        Set<Entry<K, V>> expectedEntrySet = expected.entrySet();
        for (Entry<K, V> expectedEntry : expectedEntrySet) {
            assertTrue(actual.containsKey(expectedEntry.getKey()));
            if (expectedEntry.getValue() == null) {
                assertNull(actual.get(expectedEntry.getKey()));
            } else {
                assertEquals(expectedEntry.getValue(), actual.get(expectedEntry.getKey()));
            }
        }
    }

}
