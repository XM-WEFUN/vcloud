package com.bootvue.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreAuth {
    boolean superOnly() default false; // 只有运营平台管理员可以访问

    String[] hasRole() default ""; // 哪些role权限字段可以访问

    String[] hasPermission() default ""; // 哪些菜单permission权限字段可以访问
}
