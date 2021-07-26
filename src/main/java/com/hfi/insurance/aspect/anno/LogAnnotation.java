package com.hfi.insurance.aspect.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface LogAnnotation {
    boolean simpleLog() default false;
}
