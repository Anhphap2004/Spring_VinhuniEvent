package com.vinhuni.VinhuniEvent.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Đây là định nghĩa cái nhãn @RequiredRole
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequiredRole {
    // Chứa danh sách các ID được phép vào (Ví dụ: {1, 3})
    int[] value();
}