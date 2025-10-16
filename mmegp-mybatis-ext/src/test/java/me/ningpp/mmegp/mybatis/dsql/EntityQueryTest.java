package me.ningpp.mmegp.mybatis.dsql;

import me.ningpp.mmegp.query.PropertyConditionDTO;
import me.ningpp.mmegp.query.RangeDTO;
import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlBuilder;

import java.time.LocalDate;
import java.util.List;

import static me.ningpp.mmegp.mybatis.dsql.SysCompanySimpleDynamicSqlSupport.*;
import static me.ningpp.mmegp.query.PropertyConditionDTO.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityQueryTest extends BaseEntityTest {

    @Test
    void buildCriteriaGroupTest() {
        assertNull(new SysCompanySimpleQueryConditionDTO().buildCriteriaGroup());
    }

    @Test
    void toSelectCountTest() {
        var ssp = toSSP(new SysCompanySimpleQueryConditionDTO().toSelectCount());
        assertEquals("select count(*) from sys_company", ssp.getSelectStatement());
        assertTrue(ssp.getParameters().isEmpty());
    }

    @Test
    void toSelectCountDistinctTest() {
        var ssp = toSSP(new SysCompanySimpleQueryConditionDTO().toSelectCountDistinct(id));
        assertEquals("select count(distinct id) from sys_company", ssp.getSelectStatement());
        assertTrue(ssp.getParameters().isEmpty());
    }

    @Test
    void toSelectTest() {
        var ssp = toSSP(
            new SysCompanySimpleQueryConditionDTO().toSelect(
                new BasicColumn[] {id, name},
                SqlBuilder.sortColumn(name.name()), SqlBuilder.sortColumn(id.name()).descending()
            )
        );
        assertEquals("select id, name from sys_company order by name, id DESC", ssp.getSelectStatement());
        assertTrue(ssp.getParameters().isEmpty());

        ssp = toSSP(
                new SysCompanySimpleQueryConditionDTO().toSelect(
                        new BasicColumn[] {id, name},
                        SqlBuilder.sortColumn(name.name())
                )
        );
        assertEquals("select id, name from sys_company order by name", ssp.getSelectStatement());
        assertTrue(ssp.getParameters().isEmpty());
    }

    @Test
    void toDeleteTest() {
        var dsp = toDSP(new SysCompanySimpleQueryConditionDTO().toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertTrue(dsp.getParameters().isEmpty());
    }

    @Test
    void conditionEqualTest() {
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(equalTo("a"));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where id = :p1", dsp.getDeleteStatement());
        assertEquals(1, dsp.getParameters().size());
        assertEquals("a", dsp.getParameters().get("p1"));
    }

    @Test
    void conditionNotEqualTest() {
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(notEqualTo("a"));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where id <> :p1", dsp.getDeleteStatement());
        assertEquals(1, dsp.getParameters().size());
        assertEquals("a", dsp.getParameters().get("p1"));
    }

    @Test
    void conditionInTest() {
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(in(List.of("a", "1")));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where id in (:p1,:p2)", dsp.getDeleteStatement());
        assertEquals(2, dsp.getParameters().size());
        assertEquals("a", dsp.getParameters().get("p1"));
        assertEquals("1", dsp.getParameters().get("p2"));

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(in(null));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(in(List.of()));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());
    }

    @Test
    void conditionNotInTest() {
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(notIn(List.of("a", "1")));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where id not in (:p1,:p2)", dsp.getDeleteStatement());
        assertEquals(2, dsp.getParameters().size());
        assertEquals("a", dsp.getParameters().get("p1"));
        assertEquals("1", dsp.getParameters().get("p2"));

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(notIn(null));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(notIn(List.of()));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());
    }

    @Test
    void conditionBetweenAndTest() {
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(betweenAnd(new RangeDTO<>("A", "Z")));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where id between :p1 and :p2", dsp.getDeleteStatement());
        assertEquals(2, dsp.getParameters().size());
        assertEquals("A", dsp.getParameters().get("p1"));
        assertEquals("Z", dsp.getParameters().get("p2"));

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(betweenAnd(null));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(betweenAnd(new RangeDTO<>(null, null)));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(betweenAnd(new RangeDTO<>("A", null)));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());
    }

    @Test
    void conditionNotBetweenAndTest() {
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(notBetweenAnd(new RangeDTO<>("a", "f")));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where id not between :p1 and :p2", dsp.getDeleteStatement());
        assertEquals(2, dsp.getParameters().size());
        assertEquals("a", dsp.getParameters().get("p1"));
        assertEquals("f", dsp.getParameters().get("p2"));

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(notBetweenAnd(null));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(notBetweenAnd(new RangeDTO<>(null, null)));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(notBetweenAnd(new RangeDTO<>("a", null)));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());
    }

    @Test
    void conditionLikeTest() {
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(like("%mmegp%"));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where id like :p1", dsp.getDeleteStatement());
        assertEquals(1, dsp.getParameters().size());
        assertEquals("%mmegp%", dsp.getParameters().get("p1"));
    }

    @Test
    void conditionNotLikeTest() {
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.id(notLike("%mmegp%"));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where id not like :p1", dsp.getDeleteStatement());
        assertEquals(1, dsp.getParameters().size());
        assertEquals("%mmegp%", dsp.getParameters().get("p1"));
    }

    @Test
    void conditionIsNullTest() {
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.startDate(isNull(Boolean.TRUE));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where start_date is null", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.startDate(isNull(Boolean.FALSE));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());
    }

    @Test
    void conditionLessTest() {
        LocalDate today = LocalDate.now();
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.startDate(less(today));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where start_date < :p1", dsp.getDeleteStatement());
        assertEquals(1, dsp.getParameters().size());
        assertEquals(today, dsp.getParameters().get("p1"));
    }

    @Test
    void conditionLessEqualTest() {
        LocalDate today = LocalDate.now();
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.startDate(lessEqual(today));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where start_date <= :p1", dsp.getDeleteStatement());
        assertEquals(1, dsp.getParameters().size());
        assertEquals(today, dsp.getParameters().get("p1"));
    }

    @Test
    void conditionNullTest() {
        QueryDTO<SysCompanySimple> queryDto = new QueryDTO<>(sysCompanySimple);
        var scqcDto = new SysCompanySimpleQueryConditionDTO();
        PropertyConditionDTO<String> condition = null;
        scqcDto.conditionEqual(queryDto, id, condition);
        scqcDto.conditionNotEqual(queryDto, id, condition);

        scqcDto.conditionIn(queryDto, id, condition);
        scqcDto.conditionNotIn(queryDto, id, condition);

        scqcDto.conditionBetweenAnd(queryDto, id, condition);
        scqcDto.conditionNotBetweenAnd(queryDto, id, condition);

        scqcDto.conditionLess(queryDto, id, condition);
        scqcDto.conditionLessEqual(queryDto, id, condition);
        scqcDto.conditionGreater(queryDto, id, condition);
        scqcDto.conditionGreaterEqual(queryDto, id, condition);

        scqcDto.conditionIsNull(queryDto, id, condition);
        scqcDto.conditionIsNotNull(queryDto, id, condition);

        scqcDto.conditionLike(queryDto, id, condition);
        scqcDto.conditionNotLike(queryDto, id, condition);

        var dsp = toDSP(queryDto.toDeleteModel());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertTrue(dsp.getParameters().isEmpty());
    }

    @Test
    void conditionGreaterTest() {
        LocalDate today = LocalDate.now();
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.startDate(greater(today));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where start_date > :p1", dsp.getDeleteStatement());
        assertEquals(1, dsp.getParameters().size());
        assertEquals(today, dsp.getParameters().get("p1"));
    }

    @Test
    void conditionGreaterEqualTest() {
        LocalDate today = LocalDate.now();
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.startDate(greaterEqual(today));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where start_date >= :p1", dsp.getDeleteStatement());
        assertEquals(1, dsp.getParameters().size());
        assertEquals(today, dsp.getParameters().get("p1"));
    }

    @Test
    void conditionIsNotNullTest() {
        var queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.startDate(isNotNull(Boolean.TRUE));
        var dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company where start_date is not null", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());

        queryDto = new SysCompanySimpleQueryConditionDTO();
        queryDto.startDate(isNotNull(Boolean.FALSE));
        dsp = toDSP(queryDto.toDelete());
        assertEquals("delete from sys_company", dsp.getDeleteStatement());
        assertEquals(0, dsp.getParameters().size());
    }

}
