package me.ningpp.mmegp.dss;

import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlColumn;

import java.sql.JDBCType;

public final class SysAutoUserDynamicSqlSupport {
    public static final SysAutoUser sysAutoUser = new SysAutoUser();

    public static final SqlColumn<Integer> id = sysAutoUser.id;

    public static final SqlColumn<String> firstName = sysAutoUser.firstName;

    public static final SqlColumn<String> lastName = sysAutoUser.lastName;

    public static final SqlColumn<Byte> deleted = sysAutoUser.deleted;

    public static final BasicColumn[] ALL_COLUMNS = BasicColumn.columnList(id, firstName, lastName, deleted);

    public static final class SysAutoUser extends AliasableSqlTable<SysAutoUser> {
        public final SqlColumn<Integer> id = column("id", JDBCType.INTEGER);

        public final SqlColumn<String> firstName = column("first_name", JDBCType.VARCHAR);

        public final SqlColumn<String> lastName = column("last_name", JDBCType.VARCHAR);

        public final SqlColumn<Byte> deleted = column("deleted", JDBCType.TINYINT);

        public SysAutoUser() {
            super("sys_auto_user", SysAutoUser::new);
        }
    }
}