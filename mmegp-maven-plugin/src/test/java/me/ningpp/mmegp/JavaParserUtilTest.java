/*
 *    Copyright 2021-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package me.ningpp.mmegp;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaParserUtilTest {

    @Test
    void getMatchedTypeTest() {
        String code = "import org.apache.ibatis.type.JdbcType;\n" +
                "import me.ningpp.mmegp.mybatis.type.set.LinkedHashSetCommaStringTypeHandler;" +
                "import java.util.Set;\n" +
                "\n" +
                "@Generated(table = \"sys_menu\")\n" +
                "public class SysMenu {\n" +
                "    @GeneratedColumn(name = \"string_linkedhashset\", jdbcType = JdbcType.VARCHAR, typeHandler = LinkedHashSetCommaStringTypeHandler.class)\n" +
                "    private Set<String> stringLinkedhashset;\n" +
                "    @GeneratedColumn(name = \"string_linkedhashset2\", jdbcType = JdbcType.VARCHAR, typeHandler = LinkedHashSetCommaStringTypeHandler.class)\n" +
                "    private java.util.HashSet<String> stringLinkedhashset2;\n" +
                "}";

        ParseResult<CompilationUnit> result = JavaParserUtil.newParser().parse(code);
        assertTrue(result.getResult().isPresent());

        ClassOrInterfaceDeclaration modelDeclaration = (ClassOrInterfaceDeclaration) result.getResult().get().getType(0);
        Optional<FieldDeclaration> field1 = modelDeclaration.getFieldByName("stringLinkedhashset");
        Optional<FieldDeclaration> field2 = modelDeclaration.getFieldByName("stringLinkedhashset2");
        assertTrue(field1.isPresent());
        assertTrue(field2.isPresent());

        String field1Type = JavaParserUtil.getMatchedType(
                result.getResult().get().getImports(),
                field1.get().getVariable(0).getType()
        );
        assertEquals("java.util.Set<String>", field1Type);

        String field2Type = JavaParserUtil.getMatchedType(
                result.getResult().get().getImports(),
                field2.get().getVariable(0).getType()
        );
        assertEquals("java.util.HashSet<String>", field2Type);
    }

}
