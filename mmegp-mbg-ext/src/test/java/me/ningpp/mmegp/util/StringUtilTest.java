package me.ningpp.mmegp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringUtilTest {

    @Test
    void firstLowerCaseTest() {
        assertNull(StringUtil.firstLowerCase(null));
        assertEquals("", StringUtil.firstLowerCase(""));
        assertEquals("abc", StringUtil.firstLowerCase("abc"));
        assertEquals("abc", StringUtil.firstLowerCase("Abc"));
        assertEquals("aBc", StringUtil.firstLowerCase("aBc"));
        assertEquals("aBc", StringUtil.firstLowerCase("ABc"));
    }

    @Test
    void firstUpperCaseTest() {
        assertNull(StringUtil.firstUpperCase(null));
        assertEquals("", StringUtil.firstUpperCase(""));
        assertEquals("Abc", StringUtil.firstUpperCase("abc"));
        assertEquals("Abc", StringUtil.firstUpperCase("Abc"));
        assertEquals("ABc", StringUtil.firstUpperCase("aBc"));
        assertEquals("ABc", StringUtil.firstUpperCase("ABc"));
    }

}
