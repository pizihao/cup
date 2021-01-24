package com.qlu.cup.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: cup
 * @description: 添加在Entity，表示为一个表字段
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * 表字段名称
     * @return
     */
    String value();

}
