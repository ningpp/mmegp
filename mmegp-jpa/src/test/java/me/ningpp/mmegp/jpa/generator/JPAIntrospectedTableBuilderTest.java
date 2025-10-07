package me.ningpp.mmegp.jpa.generator;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import me.ningpp.mmegp.EmptyMetaInfoHandler;
import me.ningpp.mmegp.JavaParserUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.junit.jupiter.api.Test;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.ModelType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JPAIntrospectedTableBuilderTest {

    @Test
    void jpaTableBuilder2Test() {
        String code = """
                import jakarta.persistence.Column;
                import jakarta.persistence.Entity;
                import jakarta.persistence.GeneratedValue;
                import jakarta.persistence.GenerationType;
                import jakarta.persistence.Id;
                import jakarta.persistence.Table;
                @Entity
                @Table(name = "t_contact_info")
                public class ContactInfo {

                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    @Column(name = "contact_id")
                    private Integer id;
                
                    private String name;

                    private String websiteUrl;

                    private boolean starred;
                
                    public Integer getId() {
                        return id;
                    }
                    public void setId(Integer id) {
                        this.id = id;
                    }
                    public String getName() {
                        return name;
                    }
                    public void setName(String name) {
                        this.name = name;
                    }
                    public String getWebsite() {
                        return website;
                    }
                    public void setWebsite(String website) {
                        this.website = website;
                    }
                    @Column(name = "is_starred")
                    public boolean isStarred() {
                        return starred;
                    }
                    public void setStarred(boolean starred) {
                        this.starred = starred;
                    }
                }
                """;
        IntrospectedTable table = buildIntrospectedTable(code,
                CamelCaseToUnderscoresNamingStrategy.class.getName());
        assertEquals("t_contact_info", table.getFullyQualifiedTable().getFullyQualifiedTableNameAtRuntime());

        assertEquals(1, table.getPrimaryKeyColumns().size());
        assertEquals("contact_id", table.getPrimaryKeyColumns().get(0).getActualColumnName());
        assertEquals("id", table.getPrimaryKeyColumns().get(0).getJavaProperty());
        assertTrue(table.getPrimaryKeyColumns().get(0).isIdentity());
        assertEquals(1, table.getNonPrimaryKeyColumns().stream()
                .filter(c -> "name".equals(c.getActualColumnName())).count());
        assertEquals(1, table.getNonPrimaryKeyColumns().stream()
                .filter(c -> "website_url".equals(c.getActualColumnName())).count());
        assertEquals(1, table.getNonPrimaryKeyColumns().stream()
                .filter(c -> "is_starred".equals(c.getActualColumnName())).count());
    }

    @Test
    void jpaTableBuilderTest() {
        IntrospectedTable table1 = buildIntrospectedTable("public class NotEntity {}");
        assertNull(table1);

        IntrospectedTable table2 = buildIntrospectedTable("""
            import jakarta.persistence.Entity;
            
            @Entity
            public class NoColumnEntity {}
            """);
        assertNull(table2);

        String table3Code = """
            import jakarta.persistence.Entity;
            
            @Entity
            public class MultiVariableEntity {
                private void abc() {}
                public int abcd(int x) { return x * x; }
                private int a,b;
            }
            """;
        assertThrows(IllegalArgumentException.class,
                () -> buildIntrospectedTable(table3Code));

        IntrospectedTable table4 = buildIntrospectedTable("""
            import jakarta.persistence.Entity;
            import jakarta.persistence.Table;
            @Entity
            @Table(name = "")
            public class WithTableButNoColumnEntity {}
            """);
        assertNull(table4);

        String acode = """
            import jakarta.persistence.Entity;
            import jakarta.persistence.Table;
            @Entity
            @Table(name = "tbl_table5")
            public class WithTableButNoColumnEntity {}
            """;
        IntrospectedTable table5 = buildIntrospectedTable(acode);
        assertNull(table5);
        IntrospectedTable table6 = buildIntrospectedTable(acode,
                CamelCaseToUnderscoresNamingStrategy.class.getName());
        assertNull(table6);

        String table7Code = """
                import jakarta.persistence.Column;
                import jakarta.persistence.Entity;
                import jakarta.persistence.Table;
                
                @Entity
                @Table(name = "t_contact_info")
                public class ContactInfo {
                    @Column
                    private boolean starred;
                
                    @Column(name = "is_starred")
                    public boolean isStarred() {
                        return starred;
                    }
                
                    public void setStarred(boolean starred) {
                        this.starred = starred;
                    }
                }
                """;
        assertThrows(IllegalArgumentException.class,
                () -> buildIntrospectedTable(table7Code));

        String table8Code = """
                import jakarta.persistence.Column;
                import jakarta.persistence.Entity;
                import jakarta.persistence.Lob;
                import jakarta.persistence.Table;
                import jakarta.persistence.Transient;
                import jakarta.persistence.GeneratedValue;
                import jakarta.persistence.GenerationType;
                
                @Entity
                @Table(name = "t_contact_info")
                public class ContactInfo {
                    private static final long serialVersionUID = 1L;
                    @Id
                    @GeneratedValue(strategy = GenerationType.UUID)
                    private String id;
                    @Lob
                    @Column(name = "img_cnt")
                    private byte[] imgContent;
                    @Transient
                    private String table123;
                    @Column
                    private IntrospectedTable table;
                }
                """;
        assertThrows(IllegalArgumentException.class,
                () -> buildIntrospectedTable(table8Code));

        var table9 = buildIntrospectedTable("""
                import jakarta.persistence.Entity;
                import jakarta.persistence.GeneratedValue;
                import jakarta.persistence.GenerationType;
                import jakarta.persistence.Id;
                import jakarta.persistence.Table;
                
                @Entity
                @Table(name = "t_contact_info")
                public class Contact {
                    @Id
                    @GeneratedValue(strategy = GenerationType.AUTO, generator = "increment")
                    private Integer id;
                }
                """);
        assertNotNull(table9);
    }

    private IntrospectedTable buildIntrospectedTable(String javaCode) {
        return buildIntrospectedTable(javaCode, null);
    }

    private IntrospectedTable buildIntrospectedTable(String javaCode, String namingStrategy) {
        Context context = new Context(ModelType.FLAT);
        if (StringUtils.isNotEmpty(namingStrategy)) {
            context.addProperty("tablePhysicalNamingStrategyClassName", namingStrategy);
            context.addProperty("columnPhysicalNamingStrategyClassName", namingStrategy);
        }
        ParseResult<CompilationUnit> parseResult = JavaParserUtil.newParser().parse(javaCode);
        Optional<CompilationUnit> cuOptional = parseResult.getResult();
        return new JPAIntrospectedTableBuilder().buildIntrospectedTable(
                context,
                cuOptional.get(),
                new EmptyMetaInfoHandler()
        );
    }

    @Test
    void supportRecordTypeTest() {
        assertFalse(new JPAIntrospectedTableBuilder().supportRecordType());
    }
}
