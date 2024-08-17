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
package me.ningpp.mmegp.annotationparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TextBlockLiteralExpr;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static me.ningpp.mmegp.annotationparser.ModelBasedAnnotationParser.parseSingleValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelBasedAnnotationParserTest {

    @Test
    void parseTest() {
        String code = """
                package me.ningpp.mmegp.annotationparser;

                import java.time.Year;
                import java.time.YearMonth;

                
                 @ParserDemoAnnotations(
                         value2 = {
                             @ParserDemoAnnotation(
                                     abBoolean = true,
                                     adByte = 3,
                                     afChar = 3,
                                     ahShort = 3,
                                     ajInt = 3,
                                     alLong = 3,
                                     anFloat = 3,
                                     apDouble = 3,
                                     arClass = Year.class,
                                     atEnum = {ParserDemoEnum1, ParserDemoEnum2},
                                     avEnum = ParserDemoEnum1,
                                     axBoolean = false,
                                     azByte = {1, 2, 3},
                                     bbChar = {1, 3},
                                     bdShort = {7},
                                     bfInt = {311},
                                     bhLong = {711L},
                                     bjFloat = {2.718f},
                                     bkDouble = {3.1415926},
                                     blDouble = {31.4, 27.18}
                             ),
                             @ParserDemoAnnotation(
                                     abBoolean = true,
                                     adByte = 3,
                                     afChar = 3,
                                     ahShort = 3,
                                     ajInt = 3,
                                     alLong = 3,
                                     anFloat = 3,
                                     apDouble = 3,
                                     arClass = Year.class,
                                     atEnum = {ParserDemoEnum1, ParserDemoEnum2},
                                     avEnum = ParserDemoEnum1,
                                     axBoolean = false,
                                     azByte = {1, 2, 3},
                                     bbChar = {1, 3},
                                     bdShort = {7},
                                     bfInt = {311},
                                     bhLong = {711L},
                                     bjFloat = {2.718f},
                                     bkDouble = {3.1415926},
                                     blDouble = {3.14159, 2.71828}
                             )
                     },
                     value4 = @ParserDemoAnnotation(
                         abBoolean = true,
                         adByte = 3,
                         afChar = 3,
                         ahShort = 3,
                         ajInt = 3,
                         alLong = 3,
                         anFloat = 3,
                         apDouble = 3,
                         arClass = Year.class,
                         atEnum = {ParserDemoEnum1, ParserDemoEnum2},
                         avEnum = ParserDemoEnum1,
                         axBoolean = false,
                         azByte = {1, 2, 3},
                         bbChar = {1, 3},
                         bdShort = {7},
                         bfInt = {311},
                         bhLong = {711L},
                         bjFloat = {2.718f},
                         bkDouble = {3.1415926},
                         blDouble = {3.14, 2.718}
                     )
                 )
                 @ParserDemoAnnotation(
                         abBoolean = true,
                         adByte = 3,
                         afChar = 3,
                         ahShort = 3,
                         ajInt = 3,
                         alLong = 3,
                         anFloat = 3,
                         apDouble = 3,
                         arClass = java.time.LocalDateTime.class,
                         atEnum = {ParserDemoEnum1, ParserDemoEnum2},
                         avEnum = ParserDemoEnum1,
                         axBoolean = false,
                         azByte = {1, 2, 3},
                         bbChar = {1, 3},
                         bdShort = {7},
                         bfInt = {311},
                         bhLong = {711L},
                         bjFloat = {2.718f},
                         bkDouble = {3.1415926},
                         blDouble = {3.14, 2.718}
                 )
                public class Abc {}
                """;
        CompilationUnit compilationUnit = newParser().parse(code).getResult().get();
        ParserDemoAnnotationModel model = ModelBasedAnnotationParser.parse(ParserDemoAnnotation.class,
                ParserDemoAnnotationModel.class,
                compilationUnit.getType(0),
                compilationUnit.getImports());
        assertNotNull(model);
        ParserDemoAnnotationsModel multiModel = ModelBasedAnnotationParser.parse(ParserDemoAnnotations.class,
                ParserDemoAnnotationsModel.class,
                compilationUnit.getType(0),
                compilationUnit.getImports(),
                ParserDemoAnnotationModel.class.getPackageName());
        assertNotNull(multiModel);
    }

    @Test
    void parseSingleValueTest() {
        Expression exp = new StringLiteralExpr("mmegp");
        Object parsed = parseSingleValue(String.class, exp, Map.of(), "me.ningpp");
        assertEquals("mmegp", parsed);

        exp = new TextBlockLiteralExpr("mmegp");
        parsed = parseSingleValue(String.class, exp, Map.of(), "me.ningpp");
        assertEquals("mmegp", parsed);

        assertThrows(IllegalArgumentException.class, () -> parseSingleValue(
                String.class, new IntegerLiteralExpr("1"), Map.of(), "me.ningpp"));
    }

    private static JavaParser newParser() {
        ParserConfiguration jpc = new ParserConfiguration();
        jpc.setLanguageLevel(LanguageLevel.JAVA_17);
        jpc.setCharacterEncoding(StandardCharsets.UTF_8);
        return new JavaParser(jpc);
    }

}
