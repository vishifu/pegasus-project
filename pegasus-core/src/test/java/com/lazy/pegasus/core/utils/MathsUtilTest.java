package com.lazy.pegasus.core.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MathsUtilTest {

    @ParameterizedTest
    @CsvSource({"7,8", "8,8", "2000,2048", "2049,4096", "8190,8192", "8193,16384", "1048576,1048576"})
    void alignPowerOf2(int n, int expected) {
        int pow2 = MathsUtil.alignWithPowerOf2(n);
        assertEquals(expected, pow2, "must be next power of 2");
    }

}