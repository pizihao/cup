package com.qlu.cup.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: cup
 * @description: 添加在类上，表示为一个实体类
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    /**
     * 实体类对应的表的名称
     * @return
     */
    String value();
}
