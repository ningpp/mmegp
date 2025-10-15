package me.ningpp.mmegp.mybatis.dsql;

import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlColumn;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.time.LocalDate;

public final class SysCompanySimpleDynamicSqlSupport {
    public static final SysCompanySimple sysCompanySimple = new SysCompanySimple();

    public static final SqlColumn<String> id = sysCompanySimple.id;

    public static final SqlColumn<String> name = sysCompanySimple.name;

    public static final SqlColumn<LocalDate> startDate = sysCompanySimple.startDate;

    public static final SqlColumn<BigDecimal> marketCap = sysCompanySimple.marketCap;

    public static final SqlColumn<String> unifiedCode = sysCompanySimple.unifiedCode;

    public static final BasicColumn[] ALL_COLUMNS = BasicColumn.columnList(
            id,
            name,
            startDate,
            marketCap,
            unifiedCode
    );

    public static final class SysCompanySimple extends AliasableSqlTable<SysCompanySimple> {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<LocalDate> startDate = column("start_date", JDBCType.DATE);

        public final SqlColumn<BigDecimal> marketCap = column("market_cap", JDBCType.DECIMAL);

        public final SqlColumn<String> unifiedCode = column("unified_code", JDBCType.VARCHAR);

        public SysCompanySimple() {
            super("sys_company", SysCompanySimple::new);
        }
    }
}