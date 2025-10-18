package me.ningpp.mmegp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberUtilTest {

    @Test
    void null2ZeroTest() {
        Long l = null;
        assertEquals(0, NumberUtil.null2Zero(l));
        l = 1L;
        assertEquals(1, NumberUtil.null2Zero(l));
    }

}
