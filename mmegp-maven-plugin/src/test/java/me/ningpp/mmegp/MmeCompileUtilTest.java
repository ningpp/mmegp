package me.ningpp.mmegp;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;

/**
 * 
 * @author ningpp
 * @date 2021-09-29 14:54:21
 */
public class MmeCompileUtilTest {

    private static final String JAVA_SOURCE_FILE_CONTENT = "    package me.ningpp.mmegp.entity;\r\n"
            + "\r\n"
            + "    import me.ningpp.mmegp.annotations.Generated;\r\n"
            + "    import me.ningpp.mmegp.annotations.GeneratedColumn;\r\n"
            + "    import me.ningpp.mmegp.enums.AggregateFunction;\r\n"
            + "    import org.apache.ibatis.type.JdbcType;\r\n"
            + "\r\n"
            + "    @Generated(table = \"test_entity\"%s)\r\n"
            + "    public class TestEntity {\r\n"
            + "        @GeneratedColumn(name = \"ID\", jdbcType = JdbcType.VARCHAR, id = true, blob = false, generatedValue = false, aggregates = {})\r\n"
            + "        private String id;\r\n"
            + "        @GeneratedColumn(name = \"DIC_ID\", jdbcType = JdbcType.VARCHAR, aggregates = AggregateFunction.MIN)\r\n"
            + "        private Integer dicId;\r\n"
            + "        @GeneratedColumn(name = \"OF_YEAR\", jdbcType = JdbcType.INTEGER, aggregates = { AggregateFunction.MIN, AggregateFunction.MAX} )\r\n"
            + "        private Integer ofYear;\r\n"
            + "\r\n"
            + "        @GeneratedColumn(name = \"IMAGE_DATA\", jdbcType = JdbcType.LONGVARBINARY, id = false, blob = true, generatedValue = false)\r\n"
            + "        private byte[] imageData;\r\n"
            + "\r\n"
            + "        @GeneratedColumn(name = \"IMAGE_DATA2\", jdbcType = JdbcType.LONGVARBINARY, id = false, blob = true, generatedValue = false)\r\n"
            + "        private Byte[] imageData;\r\n"
            + "\r\n"
            + "        @GeneratedColumn(name = \"serial_number\", jdbcType = JdbcType.INTEGER, id = true, generatedValue = %s)\r\n"
            + "        private Integer serialNumber;\r\n"
            + "\r\n"
            + "        public Integer getSerialNumber() {\r\n"
            + "            return serialNumber;\r\n"
            + "        }\r\n"
            + "\r\n"
            + "        public void setSerialNumber(Integer serialNumber) {\r\n"
            + "            this.serialNumber = serialNumber;\r\n"
            + "        }\r\n"
            + "\r\n"
            + "\r\n"
            + "        public String getId() {\r\n"
            + "            return id;\r\n"
            + "        }\r\n"
            + "\r\n"
            + "        public void setId(String id) {\r\n"
            + "            this.id = id;\r\n"
            + "        }\r\n"
            + "\r\n"
            + "        public String getDicId() {\r\n"
            + "            return dicId;\r\n"
            + "        }\r\n"
            + "\r\n"
            + "        public void setDicId(String dicId) {\r\n"
            + "            this.dicId = dicId;\r\n"
            + "        }\r\n"
            + "\r\n"
            + "        public Integer getOfYear() {\r\n"
            + "            return ofYear;\r\n"
            + "        }\r\n"
            + "\r\n"
            + "        public void setOfYear(Integer ofYear) {\r\n"
            + "            this.ofYear = ofYear;\r\n"
            + "        }\r\n"
            + "\r\n"
            + "        public byte[] getImageData() {\r\n"
            + "            return imageData;\r\n"
            + "        }\r\n"
            + "\r\n"
            + "        public void setImageData(byte[] imageData) {\r\n"
            + "            this.imageData = imageData;\r\n"
            + "        }\r\n"
            + "\r\n"
            + "        public Byte[] getImageData2() {\r\n"
            + "            return imageData2;\r\n"
            + "        }\r\n"
            + "\r\n"
            + "        public void setImageData2(Byte[] imageData2) {\r\n"
            + "            this.imageData2 = imageData2;\r\n"
            + "        }\r\n"
            + "    }\r\n"
            + "";

    @Test
    void buildIntrospectedTableTest() throws ClassNotFoundException, IOException, InterruptedException {
        String targetProject = System.getProperty("java.dir");
        String javaClientGeneratorConfigurationType = "XMLMAPPER";
        String modelPackageName = "me.ningpp.mmegp.entity";
        String mapperPackageName = "me.ningpp.mmegp.mapper";
        
        Context context = new Context(ModelType.FLAT);

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.addProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE, Boolean.FALSE.toString());
        commentGeneratorConfiguration.addProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS, Boolean.TRUE.toString());
        commentGeneratorConfiguration.addProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS, Boolean.FALSE.toString());
        commentGeneratorConfiguration.addProperty(PropertyRegistry.COMMENT_GENERATOR_DATE_FORMAT, "");
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetProject(targetProject);
        sqlMapGeneratorConfiguration.setTargetPackage(mapperPackageName);
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        JavaClientGeneratorConfiguration javaClientGeneratorCfg = new JavaClientGeneratorConfiguration();
        javaClientGeneratorCfg.setTargetProject(targetProject);
        javaClientGeneratorCfg.setConfigurationType(javaClientGeneratorConfigurationType);
        javaClientGeneratorCfg.setTargetPackage(mapperPackageName);
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorCfg);

        JavaModelGeneratorConfiguration jmgConfig = new JavaModelGeneratorConfiguration();
        jmgConfig.setTargetProject(targetProject);
        jmgConfig.setTargetPackage(modelPackageName);
        jmgConfig.addProperty(PropertyRegistry.MODEL_GENERATOR_EXAMPLE_PACKAGE, modelPackageName);
        jmgConfig.addProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS, Boolean.TRUE.toString());
        context.setJavaModelGeneratorConfiguration(jmgConfig);

        //为了初始化pluginAggregator
        context.generateFiles(new NullProgressCallback(), Collections.emptyList(), 
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        List<Pair<String, List<String>>> pairs = List.of(
                Pair.of("", null),
                Pair.of(", countGroupByColumns = \"OF_YEAR\"", List.of("OF_YEAR")),
                Pair.of(", countGroupByColumns = {}", null),
                Pair.of(", countGroupByColumns = {\"OF_YEAR\"}", List.of("OF_YEAR")),
                Pair.of(", countGroupByColumns = {\"OF_YEAR\", \"DIC_ID\"}", List.of("OF_YEAR", "DIC_ID"))
        );
        boolean[] hasGeneratedKeys = { false, true };
        for (boolean hasGeneratedKey : hasGeneratedKeys) {
            for (Pair<String, List<String>> pair : pairs) {
                IntrospectedTable introspectedTable = MmeCompileUtil.buildIntrospectedTable(context, 
                        String.format(Locale.CHINESE, 
                                JAVA_SOURCE_FILE_CONTENT, 
                                pair.getLeft(), String.valueOf(hasGeneratedKey)),
                        new EmptyMetaInfoHandler());
                assertEquals("test_entity", introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
                assertEquals("test_entity", introspectedTable.getTableConfiguration().getTableName());
                List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
                assertEquals(6, columns.size());
                assertEquals("ID", columns.get(0).getActualColumnName());
                assertEquals("DIC_ID", columns.get(2).getActualColumnName());
                assertEquals("OF_YEAR", columns.get(3).getActualColumnName());
                assertEquals("IMAGE_DATA", columns.get(4).getActualColumnName());
                assertEquals("IMAGE_DATA2", columns.get(5).getActualColumnName());

                assertEquals("serial_number", columns.get(1).getActualColumnName());
                assertEquals(hasGeneratedKey, columns.get(1).isIdentity());
                assertEquals(hasGeneratedKey, columns.get(1).isAutoIncrement());
                Optional<GeneratedKey> generatedKey = introspectedTable.getTableConfiguration().getGeneratedKey();
                assertEquals(hasGeneratedKey, generatedKey.isPresent());
                if (generatedKey.isPresent()) {
                    assertEquals("serial_number", generatedKey.get().getColumn());
                    assertTrue(generatedKey.get().isIdentity());
                    assertTrue(generatedKey.get().isJdbcStandard());
                }

                assertEquals("MIN", columns.get(2).getProperties().get("aggregates").toString());
                assertEquals("MIN,MAX", columns.get(3).getProperties().get("aggregates").toString());

                String[] countGroupByColumns = StringUtils
                        .split(introspectedTable.getTableConfigurationProperty(
                                JavaParserUtil.COUNT_GROUP_BY_COLUMNS_NAME), ";");
                if (pair.getRight() == null) {
                    assertTrue(countGroupByColumns == null || countGroupByColumns.length == 0);
                } else {
                    assertArrayEquals(pair.getRight().toArray(new String[] {}), countGroupByColumns);
                }
            }
        }
    }

}
