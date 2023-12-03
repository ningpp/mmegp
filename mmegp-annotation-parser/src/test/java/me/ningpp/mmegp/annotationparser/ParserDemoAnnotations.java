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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.YearMonth;

import static me.ningpp.mmegp.annotationparser.ParserDemoEnum.ParserDemoEnum1;
import static me.ningpp.mmegp.annotationparser.ParserDemoEnum.ParserDemoEnum2;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ParserDemoAnnotations {

    ParserDemoAnnotation[] value1() default {};

    ParserDemoAnnotation[] value2();

    ParserDemoAnnotation value3() default @ParserDemoAnnotation(
            abBoolean = true,
            adByte = 1,
            afChar = 1,
            ahShort = 1,
            ajInt = 1,
            alLong = 1,
            anFloat = 1,
            apDouble = 1,
            arClass = YearMonth.class,
            atEnum = {ParserDemoEnum1, ParserDemoEnum2},
            avEnum = ParserDemoEnum1,
            axBoolean = false,
            azByte = { 1, 2, 3 },
            bbChar = { 1, 3 },
            bdShort = { 7 },
            bfInt = { 311 },
            bhLong = { 711L },
            bjFloat = { 2.718f },
            bkDouble = { 3.1415926 },
            blDouble = { 3.14, 2.718 }
    );

    ParserDemoAnnotation value4();

}
