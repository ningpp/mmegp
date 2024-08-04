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

import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.TemporalField;
import java.util.List;
import java.util.Locale;

public interface SqlTimeFunction {

    String formatFunctionName();

    default String tranlateFormat(String javaPattern) {
        return tranlateFormat(new DateTimeFormatterBuilder().appendPattern(javaPattern));
    }

    default String tranlateFormat(DateTimeFormatterBuilder builder) {
        List<ParserResult> results = DateTimeFormatterBuilderUtil.parsePattern(builder);
        if (results.isEmpty()) {
            throw new IllegalArgumentException("not support pattern : " + builder);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (ParserResult result : results) {
            if (result.clppr() != null) {
                stringBuilder.append(result.clppr().literal());
            } else if (result.nppr() != null) {
                NumberPrinterParserResult nppr = result.nppr();
                stringBuilder.append(
                        tranlateFormat(
                                nppr.temporalField(),
                                nppr.minWidth(),
                                nppr.maxWidth(),
                                nppr.signStyle(),
                                nppr.subsequentWidth())
                );
            }
        }
        return stringBuilder.toString();
    }

    default String tranlateFormat(TemporalField temporalField,
                            int minWidth,
                            int maxWidth,
                            SignStyle signStyle,
                            int subsequentWidth) {
        TemporalFieldTranslator translator = getFieldTranslator(temporalField);
        if (translator == null) {
            throw new IllegalArgumentException("not support TemporalField : " + temporalField);
        }
        String result = translator.translate(minWidth, maxWidth, signStyle, subsequentWidth);
        if (result == null || result.isEmpty()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "not support temporalField = %s, minWidth=%d, maxWidth=%d, signStyle = %s, subsequentWidth = %d",
                    temporalField.toString(), minWidth, maxWidth, signStyle.name(), subsequentWidth));
        }
        return result;
    }

    default TemporalFieldTranslator getFieldTranslator(TemporalField temporalField) {
        return null;
    }

}
