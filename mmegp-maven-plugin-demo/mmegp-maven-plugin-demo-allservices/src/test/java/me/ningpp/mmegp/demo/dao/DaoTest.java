package me.ningpp.mmegp.demo.dao;

import me.ningpp.mmegp.demo.DemoApplicationStarterHsqldb;
import me.ningpp.mmegp.demo.entity.SysAutoUser;
import me.ningpp.mmegp.demo.entity.SysCompanySimple;
import me.ningpp.mmegp.demo.entity.SysRoleMenu;
import me.ningpp.mmegp.demo.query.SysCompanySimpleQueryConditionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static me.ningpp.mmegp.query.PropertyConditionDTO.in;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DaoTest extends DemoApplicationStarterHsqldb {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    void simpleTest() {
        var dao = new SysCompanySimpleDao(jdbcTemplate);
        List<String> ids = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 2; i++) {
            SysCompanySimple company = new SysCompanySimple();
            company.setId(uuid());
            company.setName(uuid());
            company.setMarketCap(BigDecimal.TEN);
            company.setStartDate(today.minusDays(i));
            company.setUnifiedCode(uuid());
            dao.insert(company);
            ids.add(company.getId());
        }

        var companyies = dao.selectByIds(ids);
        assertEquals(2, companyies.size());

        assertArrayEquals(ids.stream().sorted().toArray(),
            companyies.stream().map(SysCompanySimple::getId).sorted().toArray());

        assertEquals(2, dao.deleteByIds(ids));

        assertEquals(0, dao.deleteByQuery(new SysCompanySimpleQueryConditionDTO().id(in(ids))));
    }

    @Test
    void autoIncrementTest() {
        var dao = new SysAutoUserDao(jdbcTemplate);
        SysAutoUser user = new SysAutoUser();
        user.setFirstName(uuid());
        user.setLastName(uuid());
        user.setDeleted((byte) 0);
        dao.insert(user);
        assertNotNull(user.getId());
        assertTrue(user.getId() > 0);

        SysAutoUser userById = dao.selectById(user.getId());
        assertEquals(user.getFirstName(), userById.getFirstName());
        assertEquals(1, dao.deleteById(user.getId()));

        var roleMenuDao = new SysRoleMenuDao(jdbcTemplate);
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleId(uuid());
        roleMenu.setMenuId(uuid());
        roleMenuDao.insert(roleMenu);
        assertNotNull(roleMenu.getId());
        assertTrue(roleMenu.getId() > 0);

        assertEquals(1, roleMenuDao.deleteById(roleMenu.getId()));

        List<SysRoleMenu> roleMenus = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(uuid());
            sysRoleMenu.setMenuId(uuid());
            roleMenus.add(sysRoleMenu);
        }
        roleMenuDao.insert(roleMenus);
        for (SysRoleMenu sysRoleMenu : roleMenus) {
            assertNotNull(sysRoleMenu.getId());
            assertTrue(sysRoleMenu.getId() > 0);
        }
        assertEquals(2, roleMenuDao.deleteByIds(roleMenus.stream().map(SysRoleMenu::getId).toList()));
    }

}
