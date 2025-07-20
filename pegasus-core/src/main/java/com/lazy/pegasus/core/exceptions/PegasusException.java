package com.lazy.pegasus.core.exceptions;

import java.io.Serial;

/**
 * This class present as a root exception for all Pegasus api workloads.
 */
public class PegasusException extends Exception {
    @Serial
    private static final long serialVersionUID = -7426869443157154557L;

    private final PegasusExceptionType type;

    public PegasusException() {
        this.type = PegasusExceptionType.GENERIC_ERROR;
    }

    public PegasusException(String msg) {
        this(msg, PegasusExceptionType.GENERIC_ERROR);
    }

    public PegasusException(String msg, PegasusExceptionType type) {
        super(msg);
        this.type = type;
    }

    public PegasusException(String msg, Throwable cause) {
        this(msg, cause, PegasusExceptionType.GENERIC_ERROR);
    }

    public PegasusException(String msg, Throwable cause, PegasusExceptionType type) {
        super(msg, cause);
        this.type = type;
    }

    public PegasusException(int code, String msg) {
        this(msg, PegasusExceptionType.ofType(code));
    }

    public PegasusException(int code, String msg, Throwable cause) {
        this(msg, cause,PegasusExceptionType.ofType(code));
    }

    public PegasusExceptionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return getClass().getName() + "::[errorType=" + type + ", message='" + getMessage() + "']";
    }

}
