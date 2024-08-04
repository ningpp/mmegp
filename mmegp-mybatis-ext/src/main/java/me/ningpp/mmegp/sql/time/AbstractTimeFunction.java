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
import java.util.Map;

public abstract class AbstractTimeFunction implements SqlTimeFunction {

    protected final Map<TemporalField, TemporalFieldTranslator> mappings;

    protected AbstractTimeFunction(Map<TemporalField, TemporalFieldTranslator> mappings) {
        this.mappings = mappings;
        init();
    }

    protected void init() {
        mappings.put(ChronoField.YEAR, new YearFieldTranslator());
        mappings.put(ChronoField.YEAR_OF_ERA, new YearFieldTranslator());
        mappings.put(ChronoField.MONTH_OF_YEAR, new MonthOfYearFieldTranslator());
        mappings.put(ChronoField.DAY_OF_MONTH, new DayOfMonthFieldTranslator());
        mappings.put(ChronoField.HOUR_OF_DAY, new HourOfDayFieldTranslator());
        mappings.put(ChronoField.MINUTE_OF_HOUR, new MinuteOfHourFieldTranslator());
        mappings.put(ChronoField.SECOND_OF_MINUTE, new SecondOfMinuteFieldTranslator());
    }

    protected abstract String translateYear(int minWidth, int maxWidth,
            SignStyle signStyle, int subsequentWidth);

    protected abstract String translateMonthOfYear(int minWidth, int maxWidth,
            SignStyle signStyle, int subsequentWidth);

    protected abstract String translateDayOfMonth(int minWidth, int maxWidth,
            SignStyle signStyle, int subsequentWidth);

    protected abstract String translateHourOfDay(int minWidth, int maxWidth,
            SignStyle signStyle, int subsequentWidth);

    protected abstract String translateMinuteOfHour(int minWidth, int maxWidth,
            SignStyle signStyle, int subsequentWidth);

    protected abstract String translateSecondOfMinute(int minWidth, int maxWidth,
            SignStyle signStyle, int subsequentWidth);

    private class YearFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            return translateYear(minWidth, maxWidth, signStyle, subsequentWidth);
        }
    }

    private class MonthOfYearFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            return translateMonthOfYear(minWidth, maxWidth, signStyle, subsequentWidth);
        }
    }

    private class DayOfMonthFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            return translateDayOfMonth(minWidth, maxWidth, signStyle, subsequentWidth);
        }
    }

    private class HourOfDayFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            return translateHourOfDay(minWidth, maxWidth, signStyle, subsequentWidth);
        }
    }

    private class MinuteOfHourFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            return translateMinuteOfHour(minWidth, maxWidth, signStyle, subsequentWidth);
        }
    }

    private class SecondOfMinuteFieldTranslator implements TemporalFieldTranslator {
        @Override
        public String translate(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            return translateSecondOfMinute(minWidth, maxWidth, signStyle, subsequentWidth);
        }
    }

    @Override
    public final TemporalFieldTranslator getFieldTranslator(TemporalField temporalField) {
        return mappings.get(temporalField);
    }

}
