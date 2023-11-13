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
package me.ningpp.mmegp.sql.time;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;

public final class DateTimeFormatterBuilderUtil {

    private DateTimeFormatterBuilderUtil() {
    }

    private static final Class<?> DTFB_CLASS = DateTimeFormatterBuilder.class;
    private static final Field DTFB_CLASS_PRINTERPARSERS_FIELD;

    private static final Class<?> DTFB_CLPP_CLASS;
    private static final Field DTFB_CLPP_CLASS_LITERAL_FIELD;

    private static final Class<?> DTFB_NPP_CLASS;
    private static final Field DTFB_NPP_CLASS_TEMPORAL_FIELD;
    private static final Field DTFB_NPP_CLASS_MINWIDTH_FIELD;
    private static final Field DTFB_NPP_CLASS_MAXWIDTH_FIELD;
    private static final Field DTFB_NPP_CLASS_SIGNSTYLE_FIELD;
    private static final Field DTFB_NPP_CLASS_SUBSEQ_WIDTH_FIELD;

    static {
        try {
            Field parsersFieldInDtfbClass = DTFB_CLASS.getDeclaredField("printerParsers");
            parsersFieldInDtfbClass.trySetAccessible();

            Class<?> clppClass = Class.forName("java.time.format.DateTimeFormatterBuilder$CharLiteralPrinterParser");
            Field literalFieldInClppClass = clppClass.getDeclaredField("literal");
            literalFieldInClppClass.trySetAccessible();

            Class<?> nppClass = Class.forName("java.time.format.DateTimeFormatterBuilder$NumberPrinterParser");
            Field temporalFieldFieldInNppClass = nppClass.getDeclaredField("field");
            temporalFieldFieldInNppClass.trySetAccessible();
            Field minWidthFieldInNppClass = nppClass.getDeclaredField("minWidth");
            minWidthFieldInNppClass.trySetAccessible();
            Field maxWidthFieldInNppClass = nppClass.getDeclaredField("maxWidth");
            maxWidthFieldInNppClass.trySetAccessible();
            Field signStyleFieldInNppClass = nppClass.getDeclaredField("signStyle");
            signStyleFieldInNppClass.trySetAccessible();
            Field subsequentWidthFieldInNppClass = nppClass.getDeclaredField("subsequentWidth");
            subsequentWidthFieldInNppClass.trySetAccessible();

            DTFB_CLASS_PRINTERPARSERS_FIELD = parsersFieldInDtfbClass;

            DTFB_CLPP_CLASS = clppClass;
            DTFB_CLPP_CLASS_LITERAL_FIELD = literalFieldInClppClass;

            DTFB_NPP_CLASS = nppClass;
            DTFB_NPP_CLASS_TEMPORAL_FIELD = temporalFieldFieldInNppClass;
            DTFB_NPP_CLASS_MINWIDTH_FIELD = minWidthFieldInNppClass;
            DTFB_NPP_CLASS_MAXWIDTH_FIELD = maxWidthFieldInNppClass;
            DTFB_NPP_CLASS_SIGNSTYLE_FIELD = signStyleFieldInNppClass;
            DTFB_NPP_CLASS_SUBSEQ_WIDTH_FIELD = subsequentWidthFieldInNppClass;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static List<ParserResult> parsePattern(DateTimeFormatterBuilder builder) {
        List<ParserResult> parserResults = new ArrayList<>();
        boolean containNotSupported = false;
        try {
            @SuppressWarnings("unchecked")
            List<Object> printerParsers = (List<Object>) DTFB_CLASS_PRINTERPARSERS_FIELD.get(builder);
            if (printerParsers == null) {
                return List.of();
            }

            for (Object printerParser : printerParsers) {
                CharLiteralPrinterParserResult clppr = null;
                NumberPrinterParserResult nppr = null;
                if (printerParser.getClass() == DTFB_CLPP_CLASS) {
                    char literal = DTFB_CLPP_CLASS_LITERAL_FIELD.getChar(printerParser);
                    clppr = new CharLiteralPrinterParserResult(literal);
                } else if (DTFB_NPP_CLASS.isAssignableFrom(printerParser.getClass())) {
                    TemporalField temporalField = (TemporalField) DTFB_NPP_CLASS_TEMPORAL_FIELD.get(printerParser);
                    int minWidth = DTFB_NPP_CLASS_MINWIDTH_FIELD.getInt(printerParser);
                    int maxWidth = DTFB_NPP_CLASS_MAXWIDTH_FIELD.getInt(printerParser);
                    SignStyle signStyle = (SignStyle) DTFB_NPP_CLASS_SIGNSTYLE_FIELD.get(printerParser);
                    int subsequentWidth = DTFB_NPP_CLASS_SUBSEQ_WIDTH_FIELD.getInt(printerParser);
                    nppr = new NumberPrinterParserResult(
                            temporalField, minWidth, maxWidth, signStyle, subsequentWidth);
                } else {
                    containNotSupported = true;
                }
                parserResults.add(new ParserResult(clppr, nppr));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        if (containNotSupported) {
            throw new IllegalArgumentException("not support this " + builder);
        }
        return List.copyOf(parserResults);
    }

}
