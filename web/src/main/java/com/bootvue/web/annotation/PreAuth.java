package com.bootvue.web.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreAuth {
    boolean superOnly() default false; // 只有运营平台管理员可以访问
}
