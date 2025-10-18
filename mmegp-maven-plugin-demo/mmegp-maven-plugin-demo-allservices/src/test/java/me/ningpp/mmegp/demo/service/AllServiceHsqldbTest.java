package me.ningpp.mmegp.demo.service;

import me.ningpp.mmegp.demo.DemoApplicationStarterHsqldb;
import me.ningpp.mmegp.demo.entity.SysAutoUser;
import me.ningpp.mmegp.demo.entity2.SysUser;
import me.ningpp.mmegp.demo.mapper.SysAutoUserDynamicSqlSupport;
import me.ningpp.mmegp.demo.mapper.SysAutoUserMapper;
import me.ningpp.mmegp.demo.mapper.SysUserDynamicSqlSupport;
import me.ningpp.mmegp.demo.mapper.SysUserMapper;
import me.ningpp.mmegp.mybatis.dsql.pagination.LimitOffset;
import me.ningpp.mmegp.mybatis.dsql.pagination.Page;
import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.SelectModel;

import java.util.Locale;

import static me.ningpp.mmegp.demo.mapper.SysAutoUserDynamicSqlSupport.firstName;
import static me.ningpp.mmegp.demo.mapper.SysAutoUserDynamicSqlSupport.lastName;
import static me.ningpp.mmegp.demo.mapper.SysAutoUserDynamicSqlSupport.sysAutoUser;
import static me.ningpp.mmegp.demo.mapper.SysUserDynamicSqlSupport.name;
import static me.ningpp.mmegp.demo.mapper.SysUserDynamicSqlSupport.sysUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllServiceHsqldbTest extends DemoApplicationStarterHsqldb {

    @Test
    void selectAutoUserPageTest() {
        SelectDSL<SelectModel> dsl = SqlBuilder.select(SysAutoUserDynamicSqlSupport.ALL_COLUMNS)
                .from(sysAutoUser)
                .where()
                .and(firstName, SqlBuilder.isLike("%firstName%"))
                .and(SysAutoUserDynamicSqlSupport.id, SqlBuilder.isLessThan(0))
                .orderBy(
                        SqlBuilder.sortColumn(lastName.name()),
                        SqlBuilder.sortColumn(firstName.name()),
                        SqlBuilder.sortColumn(SysAutoUserDynamicSqlSupport.id.name()).descending()
                );
        Page<SysAutoUser> autoUserPage = allService.selectAutoUserPage(dsl,
                LimitOffset.of(3L, 7L));
        assertTrue(autoUserPage.getItems().isEmpty());
        assertEquals(0, autoUserPage.getTotalCount());
    }

    @Test
    void selectUserPageTest() {
        int total = 31;
        for (int i = 0; i < total; i++) {
            SysUser user = new SysUser();
            user.setId(String.format(Locale.ROOT, "%09d", i));
            user.setName("name" + i);
            allService.insertUser(user);
        }

        SelectDSL<SelectModel> dsl = SqlBuilder.select(SysUserDynamicSqlSupport.ALL_COLUMNS)
                .from(sysUser)
                .where()
                .and(name, SqlBuilder.isLike("name%"))
                .orderBy(SqlBuilder.sortColumn(SysUserDynamicSqlSupport.id.name()));
        Page<SysUser> userPage = allService.selectUserPage(dsl,
                LimitOffset.of(3L, 7L));
        assertEquals(total, userPage.getTotalCount());
        assertEquals(3, userPage.getItems().size());
        assertEquals("name7", userPage.getItems().get(0).getName());
        assertEquals("000000007", userPage.getItems().get(0).getId());
        assertEquals("name8", userPage.getItems().get(1).getName());
        assertEquals("000000008", userPage.getItems().get(1).getId());
        assertEquals("name9", userPage.getItems().get(2).getName());
        assertEquals("000000009", userPage.getItems().get(2).getId());
    }

}
