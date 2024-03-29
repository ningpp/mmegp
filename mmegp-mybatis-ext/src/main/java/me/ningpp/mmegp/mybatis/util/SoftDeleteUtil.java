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
package me.ningpp.mmegp.mybatis.util;

public final class SoftDeleteUtil {

    private SoftDeleteUtil() {
    }

    public static byte byteValue(String str) {
        return Byte.parseByte(str);
    }

    public static char characterValue(String str) {
        if (str != null && str.length() == 1) {
            return str.charAt(0);
        }
        throw new IllegalArgumentException("value's length != 1, value = " + str);
    }

    public static boolean booleanValue(String str) {
        return Boolean.parseBoolean(str);
    }

    public static short shortValue(String str) {
        return Short.parseShort(str);
    }

    public static int integerValue(String str) {
        return Integer.parseInt(str);
    }

    public static long longValue(String str) {
        return Long.parseLong(str);
    }

    public static String stringValue(String str) {
        return str;
    }

    public static byte empty2ByteValue(Byte v, String defaultValue) {
        return v == null ? byteValue(defaultValue) : v;
    }

    public static char empty2CharacterValue(Character v, String defaultValue) {
        return v == null ? characterValue(defaultValue) : v;
    }

    public static boolean empty2BooleanValue(Boolean v, String defaultValue) {
        return v == null ? booleanValue(defaultValue) : v;
    }

    public static short empty2ShortValue(Short v, String defaultValue) {
        return v == null ? shortValue(defaultValue) : v;
    }

    public static int empty2IntegerValue(Integer v, String defaultValue) {
        return v == null ? integerValue(defaultValue) : v;
    }

    public static long empty2LongValue(Long v, String defaultValue) {
        return v == null ? longValue(defaultValue) : v;
    }

    public static String empty2StringValue(String v, String defaultValue) {
        return v == null || v.isEmpty() ? stringValue(defaultValue) : v;
    }

}
