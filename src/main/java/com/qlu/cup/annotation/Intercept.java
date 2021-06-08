package com.qlu.cup.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: cup
 * @description: 拦截器注解
 * @author: liuwenhao
 * @create: 2021-03-03 13:16
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Intercept {
    Signature[] value();
}
