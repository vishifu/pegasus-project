package com.lavy.pixus.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface LogBundle {

    /**
     * Defines code of bundle (usually a module)
     *
     * @return code of bundle
     */
    String code();

    /**
     * Defines a regex which matches all LogMessage id within bundle
     *
     * @return regex of ids
     */
    String regexID() default "";

    /**
     * Defines an array of deprecated id of logs
     *
     * @return deprecated ids
     */
    int[] deprecatedIDs() default {};
}
