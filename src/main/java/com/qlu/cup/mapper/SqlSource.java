package com.qlu.cup.mapper;

/**
 * @program: cup
 * @description: sql
 * @author: liuwenhao
 * @create: 2021-01-28 12:38
 **/
public interface SqlSource {
    BoundSql getBoundSql(Object parameterObject);
}