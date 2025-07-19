package com.lazy.pegasus.core.exceptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum PegasusExceptionType {

    GENERIC_ERROR(9999);

    /**
     * Indicates a unique code for each exception type.
     */
    private final int code;

    /**
     * Used a map to lookup int code to exception type.
     */
    private static final Map<Integer, PegasusExceptionType> MAPS;

    static {
        Map<Integer, PegasusExceptionType> maps = new HashMap<>();
        for (PegasusExceptionType type : PegasusExceptionType.values()) {
            maps.put(type.code, type);
        }

        MAPS = Collections.unmodifiableMap(maps);
    }

    PegasusExceptionType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PegasusExceptionType ofType(int code) {
        PegasusExceptionType type = MAPS.get(code);
        if (type == null) {
            throw new IllegalArgumentException("Illegal code " + code + ". Lookup type not found.");
        }

        return type;
    }
}
