package com.bootvue.web.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreAuth {
    String value();  // 用户角色 多个角色逗号分割
}
