package me.ningpp.mmegp.springjdbc;

import me.ningpp.mmegp.TestApplicationStarter;
import me.ningpp.mmegp.dao.SysAutoUserDao;
import me.ningpp.mmegp.dao.SysCompanySimpleDao;
import me.ningpp.mmegp.entity.SysAutoUser;
import me.ningpp.mmegp.entity.SysCompanySimple;
import me.ningpp.mmegp.query.SysAutoUserQueryConditionDTO;
import me.ningpp.mmegp.query.SysCompanySimpleQueryConditionDTO;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlBuilder;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static me.ningpp.mmegp.dss.SysAutoUserDynamicSqlSupport.id;
import static me.ningpp.mmegp.query.PropertyConditionDTO.equalTo;
import static me.ningpp.mmegp.query.PropertyConditionDTO.in;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MmegpSpringDaoTest extends TestApplicationStarter {

    @RepeatedTest(3)
    void insertTest() {
        SysCompanySimpleDao dao = new SysCompanySimpleDao(jdbcTemplate);

        SysCompanySimple company = insertOne(dao);
        var queryCondition = new SysCompanySimpleQueryConditionDTO().id(equalTo(company.getId()));
        assertEquals(1, dao.countByQuery(queryCondition));
        assertEquals(1, dao.deleteById(company.getId()));

        int n = new SecureRandom().nextInt(0, 10);
        List<SysCompanySimple> companys = insertMulti(dao, n);
        List<String> ids = companys.stream().map(SysCompanySimple::getId).toList();
        queryCondition = new SysCompanySimpleQueryConditionDTO().id(in(ids));
        assertEquals(n, dao.countByQuery(queryCondition));
        assertEquals(n, dao.deleteByIds(ids));
    }

    @RepeatedTest(3)
    void insertAutoIncrementTest() {
        SysAutoUserDao dao = new SysAutoUserDao(jdbcTemplate);

        SysAutoUser user1 = insertOne(dao);
        assertNotNull(user1.getId());
        var queryCondition = new SysAutoUserQueryConditionDTO().id(equalTo(user1.getId()));
        assertEquals(1, dao.countByQuery(queryCondition));
        assertEquals(1, dao.deleteById(user1.getId()));

        int n = new SecureRandom().nextInt(0, 10);
        List<SysAutoUser> users = insertMulti(dao, n);
        assertTrue(users.stream().allMatch(u -> u.getId() != null));
        List<Integer> ids = users.stream().map(SysAutoUser::getId).toList();
        queryCondition = new SysAutoUserQueryConditionDTO().id(in(ids));
        assertEquals(n, dao.countByQuery(queryCondition));
        assertEquals(n, dao.deleteByIds(ids));
    }

    @Test
    void insertIllegalTest() {
        SysCompanySimpleDao dao = new SysCompanySimpleDao(jdbcTemplate);
        assertThrows(NullPointerException.class, () -> dao.insert((SysCompanySimple) null));
        List<SysCompanySimple> nullCompany = null;
        assertThrows(IllegalArgumentException.class, () -> dao.insert(nullCompany));
        assertThrows(IllegalArgumentException.class, () -> dao.insert(List.of()));
    }

    @Test
    void selectByIdTest() {
        SysCompanySimpleDao dao = new SysCompanySimpleDao(jdbcTemplate);
        assertNull(dao.selectById(null));
        assertNull(dao.selectById(""));
        assertNull(dao.selectById(uuid()));
    }

    @Test
    void selectByIdsTest() {
        SysCompanySimpleDao dao = new SysCompanySimpleDao(jdbcTemplate);
        assertTrue(dao.selectByIds(null).isEmpty());
        assertTrue(dao.selectByIds(List.of()).isEmpty());
        assertTrue(dao.selectByIds(List.of(uuid())).isEmpty());

        SortSpecification[] sorts = null;
        assertTrue(dao.selectByIds(List.of(uuid()), sorts).isEmpty());
        sorts = new SortSpecification[] { SqlBuilder.sortColumn(id.name()) };
        assertTrue(dao.selectByIds(List.of(uuid()), sorts).isEmpty());
    }

    private SysAutoUser randomUser() {
        SysAutoUser user = new SysAutoUser();
        user.setFirstName(uuid());
        user.setLastName(uuid());
        user.setDeleted((byte) 0);
        return user;
    }

    private SysCompanySimple randomCompany() {
        SysCompanySimple company = new SysCompanySimple();
        company.setId(uuid());
        company.setName(uuid());
        company.setStartDate(LocalDate.of(2022, 10, 29));
        company.setMarketCap(null);
        company.setUnifiedCode("mmegp");
        return company;
    }

    private SysCompanySimple insertOne(SysCompanySimpleDao dao) {
        SysCompanySimple company = randomCompany();
        dao.insert(company);
        return company;
    }

    private List<SysCompanySimple> insertMulti(SysCompanySimpleDao dao, int n) {
        List<SysCompanySimple> companys = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            companys.add(randomCompany());
        }
        dao.insert(companys);
        return companys;
    }

    private SysAutoUser insertOne(SysAutoUserDao dao) {
        SysAutoUser user = randomUser();
        dao.insert(user);
        return user;
    }

    private List<SysAutoUser> insertMulti(SysAutoUserDao dao, int n) {
        List<SysAutoUser> users = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            users.add(randomUser());
        }
        dao.insert(users);
        return users;
    }

}
