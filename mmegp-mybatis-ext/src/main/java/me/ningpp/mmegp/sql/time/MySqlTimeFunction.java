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
import java.util.HashMap;

/**
 * DATE_FORMAT      %Y-%m-%d %H:%i:%s
 * <p><a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html">Date and Time Functions</a></p>
 * <p><a href="https://dev.mysql.com/doc/refman/8.0/en/two-digit-years.html">2-Digit Years in Dates</a></p>
 */
public class MySqlTimeFunction extends AbstractTimeFunction {

    public MySqlTimeFunction() {
        super(new HashMap<>());
    }

    @Override
    public String formatFunctionName() {
        return "DATE_FORMAT";
    }

    @Override
    protected String translateYear(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth == 2 && maxWidth == 2) {
            return "%y";
        } else if (minWidth == 4) {
            return "%Y";
        }
        return null;
    }

    @Override
    protected String translateMonthOfYear(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth == 1) {
            return "%c";
        } else if (minWidth == 2) {
            return "%m";
        }
        return null;
    }

    @Override
    protected String translateDayOfMonth(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth == 1) {
            return "%e";
        } else if (minWidth == 2) {
            return "%d";
        }
        return null;
    }

    @Override
    protected String translateHourOfDay(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth == 2) {
            return "%H";
        }
        return null;
    }

    @Override
    protected String translateMinuteOfHour(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth == 2) {
            return "%i";
        }
        return null;
    }

    @Override
    protected String translateSecondOfMinute(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth == 2) {
            return "%s";
        }
        return null;
    }

}
