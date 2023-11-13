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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlTimeFunctionTest {

    @Test
    void translateTest() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        assertEquals(pattern, new H2TimeFunction().tranlateFormat(pattern));
        assertEquals(pattern, new HsqldbTimeFunction().tranlateFormat(pattern));
        assertEquals("%Y-%m-%d %H:%i:%s", new MySqlTimeFunction().tranlateFormat(pattern));
        assertEquals("yyyy-MM-dd HH:mm:ss", new SqlServerTimeFunction().tranlateFormat(pattern));
        assertEquals("YYYY-MM-DD HH24:MI:SS", new OracleAndPostgreSqlTimeFunction().tranlateFormat(pattern));

        String pattern2 = "yy-MM-dd HH:mm:ss";
        assertEquals(pattern2, new H2TimeFunction().tranlateFormat(pattern2));
        assertEquals(pattern2, new HsqldbTimeFunction().tranlateFormat(pattern2));
        assertEquals("%y-%m-%d %H:%i:%s", new MySqlTimeFunction().tranlateFormat(pattern2));
        assertEquals("yy-MM-dd HH:mm:ss", new SqlServerTimeFunction().tranlateFormat(pattern2));
        assertEquals("YY-MM-DD HH24:MI:SS", new OracleAndPostgreSqlTimeFunction().tranlateFormat(pattern2));
    }

    @Test
    void formatFunctionNameTest() {
        assertEquals("FORMATDATETIME", new H2TimeFunction().formatFunctionName());
        assertEquals("TO_CHAR", new HsqldbTimeFunction().formatFunctionName());
        assertEquals("DATE_FORMAT", new MySqlTimeFunction().formatFunctionName());
        assertEquals("FORMAT", new SqlServerTimeFunction().formatFunctionName());
        assertEquals("TO_CHAR", new OracleAndPostgreSqlTimeFunction().formatFunctionName());
    }

}
