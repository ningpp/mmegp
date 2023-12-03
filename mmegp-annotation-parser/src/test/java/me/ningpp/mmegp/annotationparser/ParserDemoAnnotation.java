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
import java.time.LocalDate;
import java.time.temporal.Temporal;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ParserDemoAnnotation {

    boolean aaBoolean() default false;
    boolean abBoolean();

    byte acByte() default 1;
    byte adByte();

    char aeChar() default '1';
    char afChar();

    short agShort() default 1;
    short ahShort();

    int aiInt() default 1;
    int ajInt();

    long akLong() default 1;
    long alLong();

    float amFloat() default 1;
    float anFloat();

    double aoDouble() default 1;
    double apDouble();

    Class<? extends Temporal> aqClass() default LocalDate.class;
    Class<? extends Temporal> arClass();

    ParserDemoEnum[] asEnum() default { ParserDemoEnum.ParserDemoEnum1};
    ParserDemoEnum[] atEnum();

    ParserDemoEnum auEnum() default ParserDemoEnum.ParserDemoEnum2;
    ParserDemoEnum avEnum();

    boolean[] awBoolean() default { false };
    boolean[] axBoolean();

    byte[] ayByte() default { 7 };
    byte[] azByte();

    char[] baChar() default { 'c' };
    char[] bbChar();

    short[] bcShort() default { 7 };
    short[] bdShort();

    int[] beInt() default { 7, 11 };
    int[] bfInt();

    long[] bgLong() default { 7, 11 };
    long[] bhLong();

    float[] biFloat() default { 7.11f };
    float[] bjFloat();

    double[] bkDouble() default { 11.7 };
    double[] blDouble();

    ParserDemoInnerAnnotation[] bmInner() default {
            @ParserDemoInnerAnnotation(abString = "bc"),
            @ParserDemoInnerAnnotation(aaString = "x", abString = "yz")
    };

    ParserDemoInnerAnnotation bnInner()
            default @ParserDemoInnerAnnotation(aaString = "bnInner.x", abString = "bnInner.yz");
}
