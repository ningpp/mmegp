package me.ningpp.mmegp.util;

public final class NumberUtil {

    private NumberUtil() {
    }

    public static long null2Zero(Long l) {
        return l == null ? 0L : l;
    }

}
