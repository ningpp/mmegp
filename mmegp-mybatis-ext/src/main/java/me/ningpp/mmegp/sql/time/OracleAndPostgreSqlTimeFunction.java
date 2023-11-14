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
 * TO_CHAR          YYYY-MM-DD HH24:MI:SS
 * <p><a href="https://docs.oracle.com/en/database/oracle/oracle-database/19/sqlrf/Format-Models.html#GUID-49B32A81-0904-433E-B7FE-51606672183A">Datetime Format Models</a></p>
 * <p><a href="https://www.postgresql.org/docs/current/functions-formatting.html">Data Type Formatting Functions</a></p>
 */
public class OracleAndPostgreSqlTimeFunction extends AbstractTimeFunction implements ToCharTimeFunction {

    public OracleAndPostgreSqlTimeFunction() {
        super(new HashMap<>());
    }

    @Override
    protected String translateYear(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth > 1 && minWidth < 6) {
            if (minWidth == maxWidth) {
                return "Y".repeat(minWidth);
            } else if (minWidth == 4) {
                return "YYYY";
            }
        }
        return null;
    }

    @Override
    protected String translateMonthOfYear(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth == 2) {
            return "MM";
        }
        return null;
    }

    @Override
    protected String translateDayOfMonth(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth == 2) {
            return "DD";
        }
        return null;
    }

    @Override
    protected String translateHourOfDay(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth == 2) {
            return "HH24";
        }
        return null;
    }

    @Override
    protected String translateMinuteOfHour(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth == 2) {
            return "MI";
        }
        return null;
    }

    @Override
    protected String translateSecondOfMinute(int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        if (minWidth == 2) {
            return "SS";
        }
        return null;
    }

}
