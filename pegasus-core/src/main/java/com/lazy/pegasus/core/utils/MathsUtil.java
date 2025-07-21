package com.lazy.pegasus.core.utils;

/**
 * A collections of utilities for maths.
 */
public class MathsUtil {

    public static int alignWithPowerOf2(int n) {
        if (n <= 0) {
            return 1; // Or handle as an error/specific case
        }
        n--; // Handle cases where n is already a power of 2
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16; // For 32-bit integers
        return n + 1;
    }

}
