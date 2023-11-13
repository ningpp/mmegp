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

import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * FORMAT           yyyy-MM-dd HH:mm:ss
 * <p><a href="https://learn.microsoft.com/en-us/sql/t-sql/functions/format-transact-sql">FORMAT (Transact-SQL)</a></p>
 * <p><a href="https://learn.microsoft.com/en-us/dotnet/standard/base-types/custom-date-and-time-format-strings">Custom date and time format strings</a></p>
 */
public class SqlServerTimeFunction implements SqlTimeFunction {

    @Override
    public String formatFunctionName() {
        return "FORMAT";
    }

    @Override
    public TemporalFieldTranslator getFieldTranslator(TemporalField temporalField) {
        return TRANSLATOR_MAPPING.get(temporalField);
    }

    protected static final Map<TemporalField, TemporalFieldTranslator> TRANSLATOR_MAPPING = new LinkedHashMap<>();

    static {
        TRANSLATOR_MAPPING.put(ChronoField.YEAR, new YearFieldTranslator());
        TRANSLATOR_MAPPING.put(ChronoField.YEAR_OF_ERA, new YearFieldTranslator());
        TRANSLATOR_MAPPING.put(ChronoField.MONTH_OF_YEAR, new MonthOfYearFieldTranslator());
        TRANSLATOR_MAPPING.put(ChronoField.DAY_OF_MONTH, new DayOfMonthFieldTranslator());
        TRANSLATOR_MAPPING.put(ChronoField.HOUR_OF_DAY, new HourOfDayFieldTranslator());
        TRANSLATOR_MAPPING.put(ChronoField.MINUTE_OF_HOUR, new MinuteOfHourFieldTranslator());
        TRANSLATOR_MAPPING.put(ChronoField.SECOND_OF_MINUTE, new SecondOfMinuteFieldTranslator());
    }

    private static class YearFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            if (minWidth > 1 && minWidth < 6) {
                if (minWidth == maxWidth) {
                    return "y".repeat(minWidth);
                } else if (minWidth == 4) {
                    return "yyyy";
                }
            }
            return null;
        }
    }

    private static class MonthOfYearFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            if (minWidth == 1) {
                return "M";
            } else if (minWidth == 2) {
                return "MM";
            }
            return null;
        }
    }

    private static class DayOfMonthFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            if (minWidth == 1) {
                return "d";
            } else if (minWidth == 2) {
                return "dd";
            }
            return null;
        }
    }

    private static class HourOfDayFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            if (minWidth == 1) {
                return "H";
            } else if (minWidth == 2) {
                return "HH";
            }
            return null;
        }
    }

    private static class MinuteOfHourFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            if (minWidth == 1) {
                return "m";
            } else if (minWidth == 2) {
                return "mm";
            }
            return null;
        }
    }

    private static class SecondOfMinuteFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            if (minWidth == 1) {
                return "s";
            } else if (minWidth == 2) {
                return "ss";
            }
            return null;
        }
    }

}
