package com.lavy.pixus.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface LogMessage {

    /**
     * Defines unique id for log method
     *
     * @return unique id
     */
    int id();

    /**
     * Defines message to log of method
     *
     * @return log message
     */
    String message();

    /**
     * Defines log level of method
     *
     * @return current log level
     */
    Level level() default Level.INFO;

}
