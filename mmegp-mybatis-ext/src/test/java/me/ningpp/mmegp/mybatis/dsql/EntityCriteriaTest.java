package me.ningpp.mmegp.mybatis.dsql;

import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.select.aggregate.CountAll;

import java.util.ArrayList;
import java.util.List;

import static me.ningpp.mmegp.mybatis.dsql.SysCompanySimpleDynamicSqlSupport.*;
import static me.ningpp.mmegp.query.PropertyConditionDTO.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityCriteriaTest extends BaseEntityTest {

    @Test
    void toQuery2Test() {
        var dto = new EntityCriteriaDTO();
        List<EntityCriteriaNodeDTO> children = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            var node = new EntityCriteriaNodeDTO();
            node.setCondition(new SysCompanySimpleQueryConditionDTO().id(equalTo("id"+i)));
            node.setSubGroups(List.of(EntityCriteriaNodeDTO.of(
                    new SysCompanySimpleQueryConditionDTO().name(equalTo("name"+i))
            )));
            node.setOperator(SqlOperator.AND);
            children.add(node);
        }
        dto.setChildren(children);
        dto.setOperator(SqlOperator.OR);
        var ssp = toSSP(dto.toQuery(sysCompanySimple).columns(new CountAll()).toSelectModel());
        assertEquals("select count(*) from sys_company where (id = :p1 and name = :p2)" +
                " or (id = :p3 and name = :p4)" +
                " or (id = :p5 and name = :p6)", ssp.getSelectStatement());
        assertEquals(6, ssp.getParameters().size());
        assertEquals("id1", ssp.getParameters().get("p1"));
        assertEquals("name1", ssp.getParameters().get("p2"));
        assertEquals("id2", ssp.getParameters().get("p3"));
        assertEquals("name2", ssp.getParameters().get("p4"));
        assertEquals("id3", ssp.getParameters().get("p5"));
        assertEquals("name3", ssp.getParameters().get("p6"));
    }

    @Test
    void toQueryTest() {
        var dto = new EntityCriteriaDTO(null, SqlOperator.AND);
        var ssp = toSSP(dto.toQuery(sysCompanySimple).columns(new CountAll()).toSelectModel());
        assertEquals("select count(*) from sys_company", ssp.getSelectStatement());
        assertTrue(ssp.getParameters().isEmpty());

        dto = new EntityCriteriaDTO(List.of(), SqlOperator.AND);
        ssp = toSSP(dto.toQuery(sysCompanySimple).columns(new CountAll()).toSelectModel());
        assertEquals("select count(*) from sys_company", ssp.getSelectStatement());
        assertTrue(ssp.getParameters().isEmpty());

        var idEqualToA = EntityCriteriaNodeDTO.of(new SysCompanySimpleQueryConditionDTO().id(equalTo("A")));
        var idEqualToB = EntityCriteriaNodeDTO.of(new SysCompanySimpleQueryConditionDTO().id(equalTo("B")));

        dto = new EntityCriteriaDTO(List.of(idEqualToA), SqlOperator.AND);
        ssp = toSSP(dto.toQuery(sysCompanySimple).columns(new CountAll()).toSelectModel());
        assertEquals("select count(*) from sys_company where id = :p1", ssp.getSelectStatement());
        assertEquals(1, ssp.getParameters().size());
        assertEquals("A", ssp.getParameters().get("p1"));

        dto = new EntityCriteriaDTO(List.of(idEqualToA, idEqualToB), SqlOperator.AND);
        ssp = toSSP(dto.toQuery(sysCompanySimple).columns(new CountAll()).toSelectModel());
        assertEquals("select count(*) from sys_company where id = :p1 and id = :p2", ssp.getSelectStatement());
        assertEquals(2, ssp.getParameters().size());
        assertEquals("A", ssp.getParameters().get("p1"));
        assertEquals("B", ssp.getParameters().get("p2"));

        dto = new EntityCriteriaDTO();
        dto.setChildren(List.of(idEqualToA, idEqualToB));
        dto.setOperator(SqlOperator.OR);
        ssp = toSSP(dto.toQuery(sysCompanySimple).columns(new CountAll()).toSelectModel());
        assertEquals("select count(*) from sys_company where id = :p1 or id = :p2", ssp.getSelectStatement());
        assertEquals(2, ssp.getParameters().size());
        assertEquals("A", ssp.getParameters().get("p1"));
        assertEquals("B", ssp.getParameters().get("p2"));
        assertEquals(SqlOperator.OR, dto.getOperator());
        assertEquals(2, dto.getChildren().size());

        var nameEqualToA = new SysCompanySimpleQueryConditionDTO().name(equalTo("A"));
        var nameEqualToB = EntityCriteriaNodeDTO.of(new SysCompanySimpleQueryConditionDTO().name(equalTo("B")));
        dto = new EntityCriteriaDTO(List.of(
            idEqualToA,
            new EntityCriteriaNodeDTO(nameEqualToA, List.of(nameEqualToB), SqlOperator.OR)
        ), SqlOperator.AND);
        ssp = toSSP(dto.toQuery(sysCompanySimple).columns(new CountAll()).toSelectModel());
        assertEquals("select count(*) from sys_company where id = :p1 and (name = :p2 or name = :p3)", ssp.getSelectStatement());
        assertEquals(3, ssp.getParameters().size());
        assertEquals("A", ssp.getParameters().get("p1"));
        assertEquals("A", ssp.getParameters().get("p2"));
        assertEquals("B", ssp.getParameters().get("p3"));

        var unifiedCodeEqualToC = EntityCriteriaNodeDTO.of(new SysCompanySimpleQueryConditionDTO().unifiedCode(equalTo("C")));
        var nameEqualToBOrUnifiedCodeEqualToC = new EntityCriteriaNodeDTO(
                nameEqualToB.getCondition(),
                List.of(unifiedCodeEqualToC),
                SqlOperator.AND
        );
        dto = new EntityCriteriaDTO(List.of(
                idEqualToA,
                new EntityCriteriaNodeDTO(nameEqualToA, List.of(nameEqualToBOrUnifiedCodeEqualToC), SqlOperator.OR)
        ), SqlOperator.AND);
        ssp = toSSP(dto.toQuery(sysCompanySimple).columns(new CountAll()).toSelectModel());
        assertEquals(
                "select count(*) from sys_company where id = :p1 and (name = :p2 or (name = :p3 and unified_code = :p4))",
                ssp.getSelectStatement()
        );
        assertEquals(4, ssp.getParameters().size());
        assertEquals("A", ssp.getParameters().get("p1"));
        assertEquals("A", ssp.getParameters().get("p2"));
        assertEquals("B", ssp.getParameters().get("p3"));
        assertEquals("C", ssp.getParameters().get("p4"));
    }

}
