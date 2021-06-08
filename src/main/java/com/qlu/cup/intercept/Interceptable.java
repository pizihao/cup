package com.qlu.cup.intercept;

import java.lang.reflect.InvocationTargetException;

/**
 * @program: cup
 * @description:
 * @author: liuwenhao
 * @create: 2021-06-08 10:17
 **/
public interface Interceptable {


    /**
     * 拦截器方法
     *
     * @param variable 拦截对象的数据信息
     * @return Object
     * @author liuwenaho
     * @date 2021/6/4 17:58
     */
    Object interceptor(Variable variable) throws InvocationTargetException, IllegalAccessException;

    /**
     * 插入插件
     *
     * @param target 插入的对象
     * @return Object
     * @author liuwenaho
     * @date 2021/6/5 10:54
     */
    Object plugIn(Object target);
}